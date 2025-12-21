package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.AssistanceMode;
import com.lontsi.rubberduckmetierservicediscussion.dto.AssistantTier;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.models.type.Model;
import com.lontsi.rubberduckmetierservicediscussion.service.impl.ProcessMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
public class ProcessMessageUnitTest {

    @Mock
    private IMessageService messageService;

    @Mock
    private IMessageRetrievalService messageRetrievalService;

    @Mock
    private IMessageProducerService messageProducerService;

    private IProcessServiceMessage processServiceMessage;

    private MessageRequestDto messageRequestDto;
    private MessageDto messageDto;
    private String principal;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        processServiceMessage = new ProcessMessageServiceImpl(messageService, messageRetrievalService, messageProducerService);
        messageRequestDto = new MessageRequestDto("Test content", "test-discussion-id", Model.QUACK_1o, AssistanceMode.EXPLICATIF);
        principal = "test-principal";
    }

    @Test
    public void testProcessMessage_whenFirstMessage() {
        // Mock du comportement de messageService.isFirstMessage() pour renvoyer true
        when(messageService.isFirstMessage(messageRequestDto.id_discussion())).thenReturn(Mono.just(true));

        // Mock du service d'envoi de message pour vérifier l'envoi
        when(messageProducerService.sendMessage(any())).thenReturn(Mono.empty());
        when(messageService.saveMessage(messageDto)).thenReturn(Mono.empty());

        // Lancer le test
        StepVerifier.create(processServiceMessage.processMessage(new MessageProducerDto(principal, messageRequestDto.id_discussion(), messageRequestDto.content(), messageRequestDto.model(),AssistantTier.STANDARD, AssistanceMode.EXPLICATIF)))
                .verifyComplete();  // Vérifie que le flux se termine correctement (pas d'erreur)

        // Vérification que le service d'envoi de message a été appelé
        verify(messageProducerService, times(1)).sendMessage(any());
    }

    @Test
    public void testProcessMessage_whenNotFirstMessage() {
        // Mock du comportement de messageService.isFirstMessage() pour renvoyer false
        when(messageService.isFirstMessage(messageRequestDto.id_discussion())).thenReturn(Mono.just(false));

        // Configurer le mock pour saveMessage
        when(messageService.saveMessage(messageDto)).thenReturn(Mono.empty());

        // Mock du comportement d'enrichissement du prompt
        when(messageRetrievalService.enrichPromptWithContext(any(), any()))
                .thenReturn(Mono.just("Enriched prompt"));

        // Mock du service d'envoi de message pour vérifier l'envoi
        when(messageProducerService.sendMessage(any())).thenReturn(Mono.empty());

        // Lancer le test

        StepVerifier.create(processServiceMessage
                        .processMessage(new MessageProducerDto(principal, messageRequestDto.id_discussion(), messageRequestDto.content(), messageRequestDto.model(),AssistantTier.STANDARD, AssistanceMode.EXPLICATIF)))
                .verifyComplete();  // Vérifie que le flux se termine correctement (pas d'erreur)

        // Vérification que le service d'envoi de message a été appelé avec le prompt enrichi
        verify(messageProducerService, times(1)).sendMessage(any());
    }

    @Test
    public void testProcessMessage_whenSaveMessageFails() {
        // Mock pour faire échouer le saveMessage
        when(messageService.saveMessage(any())).thenReturn(Mono.error(new RuntimeException("Save failed")));
        when(messageService.isFirstMessage(messageRequestDto.id_discussion())).thenReturn(Mono.just(true));

        // Lancer le test et vérifier qu'une erreur est retournée
        StepVerifier.create(processServiceMessage.processMessage(
                        new MessageProducerDto(principal, messageRequestDto.id_discussion(), messageRequestDto.content(), messageRequestDto.model(),AssistantTier.STANDARD, AssistanceMode.EXPLICATIF)
                ))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Save failed"))
                .verify();
    }
}
