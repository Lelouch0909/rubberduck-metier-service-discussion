package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.models.Discussion;
import dev.langchain4j.data.embedding.Embedding;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IEmbeddingService {

    public Mono<Embedding> generateEmbedding(String text);

}
