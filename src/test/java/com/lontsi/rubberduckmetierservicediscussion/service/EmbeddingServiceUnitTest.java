package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.service.impl.EmbeddingServiceImpl;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class EmbeddingServiceUnitTest {

    private IEmbeddingService embeddingService;

    @Mock
    private AllMiniLmL6V2EmbeddingModel embeddingModel;


    @BeforeEach
    public void setUp() {
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

        StepVerifier.create(embeddingService.generateEmbedding(text))
                .assertNext(result -> {
                    assertNotNull(result, "Embedding should not be null");
                    assertArrayEquals(expectedEmbedding.vector(), result.vector(), "Embedding should be the same");

                })
                .verifyComplete();


    }


    @Test
    void testGenerateEmbeddingWithEmptyText() {
        StepVerifier.create(embeddingService.generateEmbedding(""))
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidOperationException &&
                                throwable.getMessage().equals("Text is null") &&
                                ((InvalidOperationException) throwable).getErrorCode() == ErrorCodes.Embedding_Text_Not_Provided
                )
                .verify();
    }
    @AfterEach
    public void tearDown() {
    }


}
