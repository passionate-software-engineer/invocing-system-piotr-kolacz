package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@AllArgsConstructor
public class FileBasedDatabase<T extends WithId> implements Database<T> {

  private final Path databasePath;
  private final IdProvider idProvider;
  private final FilesService filesService;
  private final JsonService jsonService;
  private final Class<T> clazz;

  @Override
  public long save(T item) {
    try {
      item.setId(idProvider.getNextIdAndIncrement());
      filesService.appendLineToFile(databasePath, jsonService.toJson(item));

      return item.getId();
    } catch (IOException ex) {
      throw new RuntimeException("Database failed to save item", ex);
    }
  }

  @Override
  public Optional<T> getById(long id) {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .filter(line -> containsId(line, id))
          .map(line -> jsonService.toObject(line, clazz))
          .findFirst();
    } catch (IOException ex) {
      throw new RuntimeException("Database failed to get invoice with id: " + id, ex);
    }
  }

  @Override
  public List<T> getAll() {
    try {
      return filesService.readAllLines(databasePath)
          .stream()
          .map(line -> jsonService.toObject(line, clazz))
          .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new RuntimeException("Failed to read invoices from file", ex);
    }
  }

  @Override
  public Optional<T> update(long id, T updatedItem) {
    try {
      List<String> allInvoices = filesService.readAllLines(databasePath);
      var invoicesWithoutInvoiceWithGivenId = allInvoices
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());

      updatedItem.setId(id);
      invoicesWithoutInvoiceWithGivenId.add(jsonService.toJson(updatedItem));

      filesService.writeLinesToFile(databasePath, invoicesWithoutInvoiceWithGivenId);

      allInvoices.removeAll(invoicesWithoutInvoiceWithGivenId);
      return allInvoices.isEmpty() ? Optional.empty()
          : Optional.of(jsonService.toObject(allInvoices.get(0), clazz));

    } catch (IOException ex) {
      throw new RuntimeException("Failed to update invoice with id: " + id, ex);
    }

  }

  @Override
  public Optional<T> delete(long id) {
    try {
      var allInvoices = filesService.readAllLines(databasePath);

      var invoicesExceptDeleted = allInvoices
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());

      filesService.writeLinesToFile(databasePath, invoicesExceptDeleted);

      allInvoices.removeAll(invoicesExceptDeleted);

      return allInvoices.isEmpty() ? Optional.empty() :
          Optional.of(jsonService.toObject(allInvoices.get(0), clazz));

    } catch (IOException ex) {
      throw new RuntimeException("Failed to delete invoice with id: " + id, ex);
    }
  }

  private boolean containsId(String line, long id) {
    return line.contains("{\"id\":" + id + ",");
  }
}
