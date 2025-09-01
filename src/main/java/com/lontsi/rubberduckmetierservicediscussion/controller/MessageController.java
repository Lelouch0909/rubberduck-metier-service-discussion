package com.lontsi.rubberduckmetierservicediscussion.controller;

import com.lontsi.rubberduckmetierservicediscussion.controller.api.IMessageApi;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageConsumerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageService;
import com.lontsi.rubberduckmetierservicediscussion.service.IProcessServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.lontsi.rubberduckmetierservicediscussion.config.Utils.MESSAGE_ENDPOINT;

@RestController(MESSAGE_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
public class MessageController implements IMessageApi {

    private final ConcurrentMap<String, WebSocketSession> sessionMap;

    private final IMessageService messageService;

    @Override
    public Flux<MessageResult<Void>> produceMessage(Flux<Message<MessageConsumerDto>> messages) {
        return messages
                .groupBy(message -> message.getValue().id_discussion()) // Grouper par discussion
                .flatMap(groupedFlux ->
                        groupedFlux
                                .bufferUntil(message -> isEndOfMessage(message.getValue())) // Accumuler jusqu'à la fin du message
                                .flatMap(messageChunks -> {
                                    if (messageChunks.isEmpty()) {
                                        return Mono.empty();
                                    }

                                    Message<MessageConsumerDto> firstMessage = messageChunks.get(0);
                                    String idDiscussion = firstMessage.getValue().id_discussion();

                                    // Concaténer tous les fragments
                                    String completeContent = messageChunks.stream()
                                            .map(msg -> msg.getValue().content())
                                            .collect(Collectors.joining());

                                    WebSocketSession session = sessionMap.get(idDiscussion);

                                    if (session != null && session.isOpen()) {
                                        // Envoyer chaque fragment immédiatement via WebSocket
                                        Flux<Void> sendChunks = Flux.fromIterable(messageChunks)
                                                .flatMap(msg -> session.send(Mono.just(session.textMessage(msg.getValue().content()))));

                                        // Puis sauvegarder le message complet
                                        return sendChunks
                                                .then(messageService.saveMessage(new MessageDto(idDiscussion, completeContent, Sender.IA)))
                                                .then(Mono.fromRunnable(() -> log.info("📥 Complete message transferred and saved for discussion: {}", idDiscussion)))
                                                .thenMany(Flux.fromIterable(messageChunks).map(MessageResult::acknowledge))
                                                .onErrorResume(error -> {
                                                    log.error("❌ Error while processing message chunks for idDiscussion: {}", idDiscussion, error);
                                                    return Flux.fromIterable(messageChunks).map(MessageResult::negativeAcknowledge);
                                                });
                                    } else {
                                        log.error("❌ No session found for idDiscussion: {}", idDiscussion);
                                        return Flux.fromIterable(messageChunks).map(MessageResult::acknowledge);
                                    }
                                })
                );
    }

    private boolean isEndOfMessage(MessageConsumerDto messageDto) {
        return messageDto.content().endsWith("[END]") || messageDto.content().contains("STREAM_END");
    }
}
