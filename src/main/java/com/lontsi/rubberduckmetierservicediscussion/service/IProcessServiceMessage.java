package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import reactor.core.publisher.Mono;

public interface IProcessServiceMessage {

        Mono<Void> processMessage(MessageRequestDto requestDto,String principal);

}
