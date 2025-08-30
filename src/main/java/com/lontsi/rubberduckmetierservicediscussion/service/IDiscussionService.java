package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.DiscussionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDiscussionService {
    Mono<String> createDiscussion(String idUser);

    Flux<DiscussionDto> findAllUserDiscussion(String idUser);

    Mono<Boolean> isUserDiscussion(String idUser, String idDiscussion);
}
