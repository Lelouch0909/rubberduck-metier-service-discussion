package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import reactor.core.publisher.Mono;

public interface IProcessServiceMessage {

    Mono<Void> processMessage(MessageProducerDto requestDto);

}
