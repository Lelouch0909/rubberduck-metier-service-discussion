package com.lontsi.rubberduckmetierservicediscussion.controller;

import com.lontsi.rubberduckmetierservicediscussion.controller.api.IMessageApi;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageConsumerService;
import com.lontsi.rubberduckmetierservicediscussion.service.IProcessServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.common.schema.SchemaType;
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController implements IMessageApi {

    private final IProcessServiceMessage processServiceMessage;
    private final IMessageConsumerService consumerService;

    @Override
    public Mono<Void> handleMessage(MessageRequestDto messageRequestDto, SimpMessageHeaderAccessor headerAccessor) {

        return processServiceMessage
                .processMessage(messageRequestDto,
                        Objects.requireNonNull(headerAccessor.getSessionAttributes())
                                .get("X-User-Principal")
                                .toString())
                .onErrorMap(NullPointerException.class, ex -> new InvalidOperationException("Pas de principal ", ex.getCause()));


    }

    @ReactivePulsarListener(subscriptionName = "consumer-discussion",
            topics = "discussions-responses",
            stream = true,
            schemaType = SchemaType.JSON

    )
    @Override
    public Flux<MessageResult<Void>> produceMessage(Flux<Message<MessageProducerDto>> messages) {
        return messages
                .flatMap((message ->
                {
                    try {
                        MessageProducerDto messageProducerDto = message.getValue();

                        if (messageProducerDto != null) {
                            consumerService.processMessage(messageProducerDto);

                            // ack
                            log.info("📥 transfered message: {}", messageProducerDto);

                        }

                        return Mono.just(MessageResult.acknowledge(message));

                    } catch (Exception e) {
                        log.error("❌ Error while consuming message", e);
                        return Mono.just(MessageResult.negativeAcknowledge(message));

                    }

                }));

    }
}
