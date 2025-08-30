package com.lontsi.rubberduckmetierservicediscussion.repository;

import com.lontsi.rubberduckmetierservicediscussion.models.Discussion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDiscussionRepository extends ReactiveMongoRepository<Discussion, String> {


    Flux<Discussion> findAllByIdUser(String idUser);
    Mono<Boolean> existsByIdAndIdUser(String id, String idUser);

}
