package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import reactor.core.publisher.Mono;

public interface IMessageProducerService {


    Mono<Void> sendMessage(MessageProducerDto message);
}
