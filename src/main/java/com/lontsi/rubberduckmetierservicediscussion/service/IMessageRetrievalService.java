package com.lontsi.rubberduckmetierservicediscussion.service;

import reactor.core.publisher.Mono;

public interface IMessageRetrievalService {

    Mono<String> enrichPromptWithContext(String prompt, String idDiscussion);
}
