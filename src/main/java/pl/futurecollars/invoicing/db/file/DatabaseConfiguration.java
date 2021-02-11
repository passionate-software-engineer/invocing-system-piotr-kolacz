package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class DatabaseConfiguration {

  @Bean
  public IdProvider idService(FilesService filesService) throws IOException {
    Path idFilePath = Files.createTempFile("db", "id.txt");
    return new IdProvider(idFilePath, filesService);
  }

  @Bean
  public Database fileBasedDatabase(IdProvider idProvider, FilesService filesService, JsonService jsonService) throws IOException {
    Path databaseFilePath = Files.createTempFile("db", "invoices.txt");
    return new FileBasedDatabase(databaseFilePath, idProvider, filesService, jsonService);
  }

}
