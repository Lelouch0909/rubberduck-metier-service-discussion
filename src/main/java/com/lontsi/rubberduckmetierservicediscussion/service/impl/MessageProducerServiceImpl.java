package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerServiceImpl implements IMessageProducerService {
    private static final String TOPIC = "discussions";
    private final ReactivePulsarTemplate<MessageProducerDto> reactivePulsarTemplate;
    private static final int MAX_RETRIES = 3;
    private static final Duration INITIAL_BACKOFF = Duration.ofMillis(500);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);


    @Override
    public Mono<Void> sendMessage(MessageProducerDto message) {
        return reactivePulsarTemplate
                .newMessage(message)
                .withTopic(TOPIC)
                .withMessageCustomizer(messageBuilder -> messageBuilder.key(message.id_discussion()))
                .send().timeout(TIMEOUT)
                        .retryWhen(Retry.backoff(MAX_RETRIES, INITIAL_BACKOFF))
                        .doOnSuccess(unused -> log.debug("Successfully sent message: {}", message))
                        .doOnError(err -> log.error("Failed to send message: {}", message, err))
                        .onErrorResume(e -> Mono.error(
                                new InvalidOperationException("Producer error", e, ErrorCodes.PRODUCER_CREATION_ERROR)
                        )).then();
    }


}
