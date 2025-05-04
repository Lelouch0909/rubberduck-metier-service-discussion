package com.lontsi.rubberduckmetierservicediscussion.controller.api;

import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import reactor.core.publisher.Mono;

public interface IMessageApi {

    @MessageMapping("/sendMessage")
    public void handleMessage(@Payload MessageRequestDto messageRequestDto, SimpMessageHeaderAccessor headerAccessor);

}
