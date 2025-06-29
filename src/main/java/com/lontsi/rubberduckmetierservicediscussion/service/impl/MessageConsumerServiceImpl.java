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

  //  private final SimpMessagingTemplate messagingTemplate;


    @Override
    public  void processMessage(MessageProducerDto message) {


    }


    private void sendToTopic(String topic, MessageResponseDto responseDto, String principal) {
    //    messagingTemplate.convertAndSendToUser(principal, topic, responseDto);
    }


}
