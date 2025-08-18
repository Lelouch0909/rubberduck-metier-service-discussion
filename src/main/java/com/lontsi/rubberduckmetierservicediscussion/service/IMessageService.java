package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import reactor.core.publisher.Mono;

public interface IMessageService {
    Mono<Void> saveMessage(MessageRequestDto messageRequestDto);

    Mono<Boolean> isFirstMessage(String idDiscussion);
}
