package com.lontsi.rubberduckmetierservicediscussion.controller.api;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageConsumerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import reactor.core.publisher.Flux;


public interface IMessageApi {


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
    Flux<MessageResult<Void>> produceMessage(Flux<Message<MessageConsumerDto>> messages);
}
