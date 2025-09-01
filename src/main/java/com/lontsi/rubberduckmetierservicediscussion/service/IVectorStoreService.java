package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.dto.EnrichedContextDto;
import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import dev.langchain4j.data.embedding.Embedding;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IVectorStoreService {

    Mono<String> storeDocument(VectorDocument vectorDocument);


    Mono<List<EnrichedContextDto>>  findRelevant(Embedding embedding, String idDiscussion);
}
