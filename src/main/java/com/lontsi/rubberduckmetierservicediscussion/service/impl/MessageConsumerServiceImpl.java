package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.response.MessageResponseDto;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
@Slf4j
public class MessageConsumerServiceImpl implements IMessageConsumerService {

    private final SimpMessagingTemplate messagingTemplate;


    @Override
    public  void processMessage(MessageProducerDto message) {
        if (StringUtils.isBlank(message.principal())) {
            log.warn("Invalid principal in message");
            return;
        }

        messagingTemplate.convertAndSendToUser(
                message.principal(),
                "/topic/discussion." + message.id_discussion(),
                new MessageResponseDto(message.id_discussion(), message.content())
        );
    }


    private void sendToTopic(String topic, MessageResponseDto responseDto, String principal) {
        messagingTemplate.convertAndSendToUser(principal, topic, responseDto);
    }


}
