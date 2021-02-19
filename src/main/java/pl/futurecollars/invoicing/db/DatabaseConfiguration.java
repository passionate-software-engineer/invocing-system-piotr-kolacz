package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdProvider;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class DatabaseConfiguration {

  private final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

  @Bean
  public IdProvider idProvider(
      FilesService filesService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.id.file}") String idFile
  ) throws IOException {
    Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
    return new IdProvider(idFilePath, filesService);
  }

  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
  @Bean
  public Database fileBasedDatabase(
      IdProvider idProvider,
      FilesService filesService,
      JsonService jsonService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.invoices.file}") String invoicesFile
  ) throws IOException {
    Path databaseFilePath = Files.createTempFile(databaseDirectory, invoicesFile);
    return new FileBasedDatabase(databaseFilePath, idProvider, filesService, jsonService);
  }

  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
  @Bean
  public Database inMemoryDatabase() {
    logger.debug("Creating in-memory database");
    return new InMemoryDatabase();
  }

}
