package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageDto;
import reactor.core.publisher.Mono;

public interface IMessageService {
    Mono<Void> saveMessage(MessageDto messageRequestDto);

    Mono<Boolean> isFirstMessage(String idDiscussion);
}
