package com.lontsi.rubberduckmetierservicediscussion.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageProducerService;
import lombok.RequiredArgsConstructor;
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
                    String payload = message.getPayloadAsText();
                    MessageProducerDto dto = parseMessage(payload);
                    System.out.println("Received message: " + payload + " from session: " + dto.id_discussion());
                    // Store the session in the map
                    sessionMap.put(dto.id_discussion(), session);
                    // Send the message to the producer service
                    messageProducerService.sendMessage(dto);
                    return Mono.empty();
                })
                .then();
    }

    /*
        Transform the payload (json string) into a MessageProducerDto object
    */
    private MessageProducerDto parseMessage(String payload) {
        try {
            return objectMapper.readValue(payload, MessageProducerDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid message format", e);
        }
    }

}
