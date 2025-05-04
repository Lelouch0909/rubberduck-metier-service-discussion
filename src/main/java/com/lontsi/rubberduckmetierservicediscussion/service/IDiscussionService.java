package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.DiscussionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IDiscussionService {
    public Mono<String> createDiscussion(String idUser);

    Flux<DiscussionDto> findAllUserDiscussion(String idUser);
}
