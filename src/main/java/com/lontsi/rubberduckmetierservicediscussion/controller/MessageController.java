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
import org.apache.pulsar.reactive.client.api.MessageResult;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;

import static com.lontsi.rubberduckmetierservicediscussion.config.Utils.MESSAGE_ENDPOINT;

@RestController(MESSAGE_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
public class MessageController implements IMessageApi {

    private final IProcessServiceMessage processServiceMessage;
    private final ConcurrentMap<String, WebSocketSession> sessionMap;


    @Override
    public Mono<Void> handleMessage(MessageRequestDto messageRequestDto) {

        log.warn("receive message :{}", messageRequestDto);
        return processServiceMessage
                .processMessage(messageRequestDto,
                        SecurityContextHolder.getContext().getAuthentication().getName()
                )
                .onErrorMap(NullPointerException.class, ex -> new InvalidOperationException("Pas de principal ", ex.getCause()));

    }



    @Override
    public Flux<MessageResult<Void>> produceMessage(Flux<Message<MessageProducerDto>> messages) {
        return messages
                .flatMap((message ->
                {
                    try {
                        MessageProducerDto messageProducerDto = message.getValue();

                        if (messageProducerDto != null) {
                            WebSocketSession session = sessionMap.get(messageProducerDto.id_discussion());

                            if (session != null && session.isOpen()) {

                                return session.send(Mono.just(session.textMessage(messageProducerDto.content())))
                                        .thenReturn(MessageResult.acknowledge(message));


                            } else {
                                log.warn("❌ No session found for idDiscussion: {}", (messageProducerDto.id_discussion()));
                            }                            // ack
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
