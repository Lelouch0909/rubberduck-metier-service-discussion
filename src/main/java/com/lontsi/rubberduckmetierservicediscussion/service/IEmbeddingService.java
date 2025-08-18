package com.lontsi.rubberduckmetierservicediscussion.service;

import dev.langchain4j.data.embedding.Embedding;
import reactor.core.publisher.Mono;

public interface IEmbeddingService {

    Mono<Embedding> generateEmbedding(String text);

}
