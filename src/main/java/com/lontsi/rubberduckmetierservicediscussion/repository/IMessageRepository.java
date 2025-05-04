package com.lontsi.rubberduckmetierservicediscussion.repository;

import com.lontsi.rubberduckmetierservicediscussion.models.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IMessageRepository extends ReactiveMongoRepository<Message, String> {
}
