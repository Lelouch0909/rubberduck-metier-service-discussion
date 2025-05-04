package com.lontsi.rubberduckmetierservicediscussion.controller;

import com.lontsi.rubberduckmetierservicediscussion.controller.api.IMessageApi;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Controller
public class MessageController implements IMessageApi {

    @Override
    public void handleMessage(MessageRequestDto messageRequestDto, SimpMessageHeaderAccessor headerAccessor) {
        String principal = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("X-User-Principal").toString();
        /*
            Verifier ici que le message est bien envoyé par l utilisateur
            ****
         */
        System.out.println("Principal: "+principal);
    }
}
