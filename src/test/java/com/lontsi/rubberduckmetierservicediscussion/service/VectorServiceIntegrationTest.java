package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
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



    @BeforeEach
    public void setUp() {
        mongoTemplate.dropCollection(collectionName);

    }


@Test
@DisplayName("Creation d'un document vector - Success")
public void shouldStoreDocument() {
    // 1. Setup
    VectorDocument vectorDocument = new VectorDocument();
    vectorDocument.setId_Message("msg123");
    vectorDocument.setId_discussion("disc456");
    vectorDocument.setEmbedding(new Embedding(new float[]{0.1f, 0.2f}));

    // 2. Mock embedding store response
    when(embeddingStore.add(any(Embedding.class), any(TextSegment.class)))
        .thenReturn("generated-embedding-id");

    // 3. Execute & Verify
    StepVerifier.create(vectorStoreService.storeDocument(vectorDocument))
            .expectNext("generated-embedding-id") // Vérifie l'ID retourné
            .verifyComplete();

    // 4. Verify embedding store interaction
    ArgumentCaptor<TextSegment> segmentCaptor = ArgumentCaptor.forClass(TextSegment.class);
    verify(embeddingStore).add(eq(vectorDocument.getEmbedding()), segmentCaptor.capture());

    TextSegment storedSegment = segmentCaptor.getValue();
    assertEquals("msg123", storedSegment.text());
}
    @AfterEach
    public void tearDown() {
        mongoTemplate.dropCollection(collectionName);

    }


}
