package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VectorServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(VectorServiceIntegrationTest.class);
    @Autowired
    private MongoDbEmbeddingStore embeddingStore;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IEmbeddingService embeddingService;

    @Autowired
    private IVectorStoreService vectorStoreService;

    @Value("${langchain4j.vectorStoreCollectionName}")
    private String collectionName;

    @BeforeAll
    public void init() {

    }

    @BeforeEach
    public void setUp() {
        mongoTemplate.dropCollection(collectionName);

    }

    @Test
    @DisplayName(" Creation d'un document vector - Success")
    public void shouldStoreDocument() {
        VectorDocument vectorDocument = new VectorDocument();
        vectorDocument.setId_Message("id_Message");
        vectorDocument.setId_discussion("id_discussion");
        Embedding embedding = embeddingService.generateEmbedding("text test");
        vectorDocument.setEmbedding(embedding);
        StepVerifier.create(vectorStoreService.storeDocument(vectorDocument))
                .assertNext(id -> {
                    Query query = new Query(Criteria.where("_id").is(id));
                    Document stored = mongoTemplate.findOne(query, Document.class, collectionName);
                    assertThat(stored).isNotNull();
                    log.warn(stored.toJson());
                    assertThat(stored.getString("_id")).isEqualTo(id);
                    assertThat(((List<?>) stored.get("embedding")).size()).isEqualTo(embedding.vector().length);
                })
                .verifyComplete();

    }


    @AfterEach
    public void tearDown() {
        mongoTemplate.dropCollection(collectionName);

    }

    @AfterAll
    public void end() {
    }

}
