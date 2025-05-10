package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.config.TestConfig;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.models.Message;
import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;
import com.lontsi.rubberduckmetierservicediscussion.repository.IMessageRepository;
import com.lontsi.rubberduckmetierservicediscussion.service.impl.MessageServiceImpl;
import dev.langchain4j.data.embedding.Embedding;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class MessageStoreServiceUnitTest {

    @Mock
    private IMessageRepository messageRepository;

    @Mock
    private IEmbeddingService embeddingService;

    @Mock
    private IVectorStoreService vectorStoreService;

    private IMessageService messageService;

    private MessageRequestDto messageRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    messageService = new MessageServiceImpl(messageRepository, embeddingService, vectorStoreService);
        messageRequestDto = new MessageRequestDto("12345", "msg1");
    }

    @Test
    @DisplayName("Should return error when Save Message Fails")
    void ShouldReturnError_WhenMessageSaveFails() {
        when(messageRepository.save(any())).thenReturn(Mono.error(new DataAccessException("DB error") {
        }));

        StepVerifier.create(messageService.saveMessage(messageRequestDto))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof InvalidOperationException);
                    assertEquals("Error while saving message", ex.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("Should return error when embedding fails")
    void ShouldReturnError_WhenEmbeddingFails() {
        Message savedMessage = new Message("12345", "msg1", Sender.USER);
        when(messageRepository.save(any())).thenReturn(Mono.just(savedMessage));
        when(embeddingService.generateEmbedding(any())).thenThrow(new RuntimeException("Embedding error"));

        StepVerifier.create(messageService.saveMessage(messageRequestDto))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof InvalidOperationException);
                    assertEquals("Embedding error", ex.getMessage());
                })
                .verify();
    }



    @Test
    @DisplayName("should save message and store embedding")
    void ShouldSaveMessageAndStoreEmbedding() {
        // Given
        String content = "Hello world!";
        String idDiscussion = "12345";

        MessageRequestDto dto = new MessageRequestDto(idDiscussion, content);

        Message savedMessage = new Message();
        savedMessage.setId("msg1");
        savedMessage.setContent(content);
        savedMessage.setIdDiscussion(idDiscussion);
        savedMessage.setSender(Sender.USER);

        Embedding embedding = new Embedding(new float[]{1.0f, 2.0f, .0f});

        // When
        when(messageRepository.save(any(Message.class))).thenReturn(Mono.just(savedMessage));
        when(embeddingService.generateEmbedding(content)).thenReturn(Mono.just(embedding));
        when(vectorStoreService.storeDocument(any(VectorDocument.class))).thenReturn(Mono.just("vectorId123"));

        // Then
        StepVerifier.create(messageService.saveMessage(dto))
                .verifyComplete();

        verify(messageRepository).save(argThat(message ->
                message.getContent().equals(content) &&
                        message.getIdDiscussion().equals(idDiscussion) &&
                        message.getSender() == Sender.USER
        ));
        verify(embeddingService).generateEmbedding(content);


        verify(vectorStoreService).storeDocument(argThat(doc ->
                        doc.getId_discussion().equals(idDiscussion) &&
                        doc.getEmbedding().equals(embedding)
        ));
    }

    @AfterEach
    public void tearDown() {

    }

}
