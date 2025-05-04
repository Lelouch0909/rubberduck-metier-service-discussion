package com.lontsi.rubberduckmetierservicediscussion.dto;


import com.lontsi.rubberduckmetierservicediscussion.models.Message;
import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;
import lombok.Builder;

import java.util.List;

@Builder
public class MessageDto {

    private String idDiscussion;

    private String message;

    private String content;

    private Sender Sender;

    public MessageDto fromMessage(Message message) {
        return MessageDto.builder()
                .idDiscussion(message.getIdDiscussion())
                .message(message.getContent())
                .content(message.getContent())
                .Sender(message.getSender())
                .build();
    }



}
