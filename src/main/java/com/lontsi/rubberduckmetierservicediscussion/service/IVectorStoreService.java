package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import dev.langchain4j.data.embedding.Embedding;
import reactor.core.publisher.Mono;

public interface IVectorStoreService {

    Mono<String> storeDocument(VectorDocument vectorDocument);


    Mono<String> findRelevant(Embedding embedding, int limit, String idDiscussion);
}
