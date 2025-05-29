package com.lontsi.rubberduckmetierservicediscussion.config;

import com.mongodb.client.MongoClient;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.OffsetDateTime;
import java.util.Optional;

@Configuration
//@Profile("!test")

@EnableMongoRepositories("com.lontsi.rubberduckmetierservicediscussion.repository")
public class MongoStoreConfig {

    @Value("${langchain4j.vectorStoreDB}")
    private String vectorStoreDB;

    @Value("${langchain4j.vectorStoreCollectionName}")
    private String vectorStoreCollectionName;

    @Value("${langchain4j.vectorStoreIndexName}")
    private String vectorIndexName;

    @Value("${langchain4j.vectorStoreConnectionString}")
    private String vectorStoreConnectionString;

    @Autowired
    private MongoClient mongoClient;

    @Bean
    public MongoDbEmbeddingStore mongoDbEmbeddingStore() {
        return MongoDbEmbeddingStore.builder()
                .fromClient(mongoClient)
                .databaseName(vectorStoreDB)
                .collectionName(vectorStoreCollectionName)
                .createIndex(true)
                .indexName(vectorIndexName)
                .build();

    }



}
