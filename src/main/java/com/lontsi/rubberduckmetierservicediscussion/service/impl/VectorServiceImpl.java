package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.dto.EnrichedContextDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorServiceImpl implements IVectorStoreService {

    @Autowired
    private MongoDbEmbeddingStore embeddingStore;

    @Value("${embedding.limit}")
    private int limit;

    @Value("${embedding.min-score}")
    private Double minScore;

    @Override
    public Mono<String> storeDocument(VectorDocument vectorDocument) {
        return Mono.fromCallable(() -> {
            TextSegment textSegment = TextSegment.from(vectorDocument.getText());
            textSegment.metadata().put("id_discussion", vectorDocument.getId_discussion());
            textSegment.metadata().put("id_message", vectorDocument.getId_Message());
            textSegment.metadata().put("sender", vectorDocument.getSender().toString());
            textSegment.metadata().put("timestamp", Instant.now().toString());
            return embeddingStore.add(vectorDocument.getEmbedding(), textSegment);
        }).subscribeOn(Schedulers.boundedElastic()).onErrorMap(e -> new InvalidOperationException("Error while storing document", e, ErrorCodes.Vector_Save_Exception));
    }


    @Override
    public Mono<List<EnrichedContextDto>> findRelevant(Embedding embedding, String idDiscussion) {
        return Mono.fromCallable(() -> {
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder().queryEmbedding(embedding).maxResults(limit).minScore(minScore).filter(new IsIn("id_discussion", Collections.singleton(idDiscussion))).build();
            return embeddingStore.search(request).matches().stream().map(match -> {
                String text = match.embedded().text();
                double score = match.score();
                String sender = match.embedded().metadata().getString("sender");
                String timestampStr = match.embedded().metadata().getString("timestamp");
                Instant timestamp = Instant.parse(timestampStr);
                return new EnrichedContextDto(text, score, sender,timestamp);
            }).collect(Collectors.toList());
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
