package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import com.lontsi.rubberduckmetierservicediscussion.service.IVectorStoreService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.filter.comparison.IsIn;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class VectorServiceImpl implements IVectorStoreService {

    @Autowired
    private MongoDbEmbeddingStore embeddingStore;


    @Override
    public Mono<String> storeDocument(VectorDocument vectorDocument) {
        return Mono.fromCallable(() -> {
                    TextSegment textSegment = TextSegment.from(vectorDocument.getId_Message());
                    textSegment.metadata().put("id_discussion", vectorDocument.getId_discussion());
                    return embeddingStore.add(vectorDocument.getEmbedding(), textSegment);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> new InvalidOperationException("Error while storing document", e, ErrorCodes.Vector_Save_Exception));
    }


    @Override
    public Mono<String> findRelevant(Embedding embedding, int limit, String idDiscussion) {
        return Mono.fromCallable(() -> {
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                    .queryEmbedding(embedding)
                    .maxResults(limit)
                    .minScore(0.75)
                    .filter(new IsIn("id_discussion", Collections.singleton(idDiscussion)))
                    .build();
            return embeddingStore.search(request).matches().stream().map(textSegmentEmbeddingMatch ->
                    textSegmentEmbeddingMatch.embedded().text()
            ).collect(Collectors.joining("\n---\n"));
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
