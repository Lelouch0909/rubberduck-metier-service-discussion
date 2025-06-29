package com.lontsi.rubberduckmetierservicediscussion.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentMap;

/*
 *
 * WebsocketHandler interface qui dis comment gerer les connexions websocket
 * */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomWebSocketHandler implements WebSocketHandler {

    private final IMessageProducerService messageProducerService;
    private final ConcurrentMap<String, WebSocketSession> sessionMap;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .flatMap(message -> {
                    try {
                        String payload = message.getPayloadAsText();
                        MessageProducerDto dto = parseMessage(payload);
                        System.out.println("Received message: " + payload + " from session: " + dto.id_discussion());
                        sessionMap.put(dto.id_discussion(), session);
                        return messageProducerService.sendMessage(dto);
                    } catch (Exception e) {
                        log.error("Error while sending message", e);
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return session.send(
                            Mono.just(session.textMessage(e.getMessage()))
                    );
                })
                .then(Mono.never());
    }

    /*
        Transform the payload (json string) into a MessageProducerDto object
    */
    private MessageProducerDto parseMessage(String payload) {
        try {
            return objectMapper.readValue(payload, MessageProducerDto.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Invalid message format", e);
        }
    }

}
