package com.lontsi.rubberduckmetierservicediscussion.controller.api;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;

import com.lontsi.rubberduckmetierservicediscussion.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.lontsi.rubberduckmetierservicediscussion.config.Utils.MESSAGE_ENDPOINT;

public interface IMessageApi {

    /**
     * Handles incoming messages from the client.
     * This method processes the message request and sends it to the appropriate service.
     *
     * @param messageRequestDto The message request data transfer object containing the message details.
     * @return A Mono that completes when the message is processed.
     */
    @Operation(
            summary = "Handle incoming message",
            description = "This method handles WebSocket messages sent by the client."
    )
    @PostMapping(MESSAGE_ENDPOINT + "/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> handleMessage(@RequestBody MessageRequestDto messageRequestDto);




    /**
     * Consumes messages from the Pulsar topic 'discussions-responses'.
     * This method listens to incoming messages and processes them.
     *
     * @param messages Flux of messages to be processed.
     * @return Flux of MessageResult indicating the result of processing each message.
     */

    @Operation(
            summary = "Consume messages from Pulsar",
            description = "This method listens to the 'discussions-responses' Pulsar topic and send the incoming messages with websocket to" +
                    "client to the /topic/discussion.\" +idDiscussion  endpoint "
    )

    @ReactivePulsarListener(subscriptionName = "consumer-discussion",
            topics = "discussions-responses",
            stream = true,
            schemaType = SchemaType.JSON,
            subscriptionType = SubscriptionType.Key_Shared


    )
    Flux<MessageResult<Void>> produceMessage(Flux<Message<MessageProducerDto>> messages);
}
