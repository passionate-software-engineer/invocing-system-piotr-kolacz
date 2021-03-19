package pl.futurecollars.invoicing.db;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdProvider;
import pl.futurecollars.invoicing.db.jpa.InvoiceRepository;
import pl.futurecollars.invoicing.db.jpa.JpaDatabase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.mongo.MongoBasedDatabase;
import pl.futurecollars.invoicing.db.mongo.MongoIdProvider;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
  public IdProvider idProvider(
      FilesService filesService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.id.file}") String idFile
  ) throws IOException {
    Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
    return new IdProvider(idFilePath, filesService);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
  public Database<Invoice> invoiceFileBasedDatabase(
      IdProvider idProvider,
      FilesService filesService,
      JsonService jsonService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.invoices.file}") String invoicesFile
  ) throws IOException {
    Path databaseFilePath = Files.createTempFile(databaseDirectory, invoicesFile);
    return new FileBasedDatabase<>(databaseFilePath, idProvider, filesService, jsonService, Invoice.class);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
  public Database<Company> companyFileBasedDatabase(
      IdProvider idProvider,
      FilesService filesService,
      JsonService jsonService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.companies.file}") String companiesFile
  ) throws IOException {
    Path databaseFilePath = Files.createTempFile(databaseDirectory, companiesFile);
    return new FileBasedDatabase<>(databaseFilePath, idProvider, filesService, jsonService, Company.class);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "sql")
  public Database<Invoice> sqlDatabase(JdbcTemplate jdbcTemplate) {
    return new SqlDatabase(jdbcTemplate);
  }

  @Bean // TODO [PK] workaround until correct implementation is available
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "sql")
  public Database<Company> companySqlDatabase() {
    return new InMemoryDatabase<>();
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "jpa")
  public Database<Invoice> jpaDatabase(InvoiceRepository invoiceRepository) {
    return new JpaDatabase(invoiceRepository);
  }

  @Bean // TODO [PK] workaround until correct implementation is available
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "jpa")
  public Database<Company> companyJpaDatabase() {
    return new InMemoryDatabase<>();
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
  public Database<Invoice> invoiceInMemoryDatabase() {
    return new InMemoryDatabase<>();
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
  public Database<Company> companyInMemoryDatabase() {
    return new InMemoryDatabase<>();
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
  public MongoDatabase mongoDb(
      @Value("${invoicing-system.database.name}") String databaseName
  ) {
    CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    MongoClientSettings settings = MongoClientSettings.builder()
        .codecRegistry(pojoCodecRegistry)
        .build();

    MongoClient client = MongoClients.create(settings);
    return client.getDatabase(databaseName);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
  public MongoIdProvider mongoIdProvider(
      @Value("${invoicing-system.database.counter.collection}") String collectionName,
      MongoDatabase mongoDb
  ) {
    MongoCollection<Document> collection = mongoDb.getCollection(collectionName);
    return new MongoIdProvider(collection);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
  public Database<Invoice> mongoDatabase(
      @Value("${invoicing-system.database.collection}") String collectionName,
      MongoDatabase mongoDb,
      MongoIdProvider mongoIdProvider
  ) {
    MongoCollection<Invoice> collection = mongoDb.getCollection(collectionName, Invoice.class);
    return new MongoBasedDatabase(collection, mongoIdProvider);
  }

  @Bean // TODO [PK] workaround until correct implementation is available
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
  public Database<Company> companyMongoDatabase() {
    return new InMemoryDatabase<>();
  }

}
