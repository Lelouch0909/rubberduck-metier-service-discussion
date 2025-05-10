package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;

public interface IMessageConsumerService {


    void processMessage(MessageProducerDto message);

}
