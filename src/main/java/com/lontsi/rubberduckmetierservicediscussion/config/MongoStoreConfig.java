package com.lontsi.rubberduckmetierservicediscussion.config;

import com.mongodb.client.MongoClient;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
//@Profile("!test")
@EnableMongoRepositories("com.lontsi.rubberduckmetierservicediscussion.repository")
public class MongoStoreConfig {

    @Value("${embedding.vectorStoreDB}")
    private String vectorStoreDB;

    @Value("${embedding.vectorStoreCollectionName}")
    private String vectorStoreCollectionName;

    @Value("${embedding.vectorStoreIndexName}")
    private String vectorIndexName;

    @Autowired
    private MongoClient mongoClient;

    @Bean
    public MongoDbEmbeddingStore mongoDbEmbeddingStore() {
        return MongoDbEmbeddingStore.builder()
                .fromClient(mongoClient)
                .databaseName(vectorStoreDB)
                .collectionName(vectorStoreCollectionName)
                .createIndex(false)
                .indexName(vectorIndexName)
                .build();

    }


}
