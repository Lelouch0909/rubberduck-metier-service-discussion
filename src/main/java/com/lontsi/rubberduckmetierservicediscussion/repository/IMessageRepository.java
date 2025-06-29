package com.lontsi.rubberduckmetierservicediscussion.repository;

import com.lontsi.rubberduckmetierservicediscussion.models.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface IMessageRepository extends ReactiveMongoRepository<Message, String> {
    Mono<Long> countAllByIdDiscussion(String idDiscussion);
}
