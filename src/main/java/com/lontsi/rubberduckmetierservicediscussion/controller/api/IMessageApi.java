package com.lontsi.rubberduckmetierservicediscussion.controller.api;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;

import com.lontsi.rubberduckmetierservicediscussion.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMessageApi {

    @Operation(
            summary = "Handle incoming WebSocket message",
            description = "This method handles incoming WebSocket messages sent by the client to the '/sendMessage' endpoint."
    )
    @MessageMapping("/sendMessage")
    public Mono<Void> handleMessage(@Payload MessageRequestDto messageRequestDto, SimpMessageHeaderAccessor headerAccessor);


    @Operation(
            summary = "Consume messages from Pulsar",
            description = "This method listens to the 'discussions-responses' Pulsar topic and send the incoming messages with websocket to" +
                    "client to the /topic/discussion.\" +idDiscussion  endpoint "
    )
    @ReactivePulsarListener(subscriptionName = "consumer-discussion",
            topics = "discussions-responses",
            stream = true,
            schemaType = SchemaType.JSON

    )
    Flux<MessageResult<Void>> produceMessage(Flux<Message<MessageProducerDto>> messages);
}
