package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.service.impl.EmbeddingServiceImpl;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmbeddingServiceUnitTest {

    @Autowired
    private IEmbeddingService embeddingService;

    @Mock
    private AllMiniLmL6V2EmbeddingModel embeddingModel;


    @BeforeAll
    public void init() {

    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // inject the mock EmbeddingModel into the constructor
        embeddingService = new EmbeddingServiceImpl(embeddingModel);
    }

    @Test
    @DisplayName(" Creation d'un embedding - Success")
    public void shouldGenerateEmbedding() {

        // Given
        String text = "Test message";
        Embedding expectedEmbedding = new Embedding(new float[]{1.0f, 2.0f, .0f});

        // When
        when(embeddingModel.embed(text)).thenReturn(Response.from(expectedEmbedding));

        // Then
        Embedding result = embeddingService.generateEmbedding(text);
        assertNotNull(result, "Embedding should not be null");
        assertArrayEquals(expectedEmbedding.vector(), result.vector(), "Embedding should be the same");
    }


    @Test
    void testGenerateEmbeddingWithEmptyText() {

        Assertions.assertThrows(InvalidOperationException.class, () -> {
            embeddingService.generateEmbedding("");
        });
    }


    @AfterEach
    public void tearDown() {
    }

    @AfterAll
    public void end() {
    }

}
