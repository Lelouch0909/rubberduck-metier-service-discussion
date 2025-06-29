package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import dev.langchain4j.data.embedding.Embedding;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IVectorStoreService {

        Mono<String> storeDocument(VectorDocument vectorDocument);


        Mono<String> findRelevant(Embedding embedding, int limit, String idDiscussion);
}
