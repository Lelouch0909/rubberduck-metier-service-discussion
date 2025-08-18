package com.lontsi.rubberduckmetierservicediscussion.controller;

import com.lontsi.rubberduckmetierservicediscussion.controller.api.IMessageApi;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageConsumerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
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

import static com.lontsi.rubberduckmetierservicediscussion.config.Utils.MESSAGE_ENDPOINT;

@RestController(MESSAGE_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
public class MessageController implements IMessageApi {

    private final ConcurrentMap<String, WebSocketSession> sessionMap;


    @Override
    public Flux<MessageResult<Void>> produceMessage(Flux<Message<MessageConsumerDto>> messages) {
        return messages
                .flatMap((message ->
                {
                    try {
                        MessageConsumerDto messageConsumerDto = message.getValue();

                        if (messageConsumerDto != null) {
                            WebSocketSession session = sessionMap.get(messageConsumerDto.id_discussion());

                            if (session != null && session.isOpen()) {

                                return session.send(Mono.just(session.textMessage(messageConsumerDto.content())))
                                        .thenReturn(MessageResult.acknowledge(message));


                            } else {
                                log.warn("❌ No session found for idDiscussion: {}", (messageConsumerDto.id_discussion()));
                            }                            // ack
                            log.info("📥 transfered message: {}", messageConsumerDto);

                        }

                        return Mono.just(MessageResult.acknowledge(message));

                    } catch (Exception e) {
                        log.error("❌ Error while consuming message", e);
                        return Mono.just(MessageResult.negativeAcknowledge(message));

                    }

                }));

    }
}
