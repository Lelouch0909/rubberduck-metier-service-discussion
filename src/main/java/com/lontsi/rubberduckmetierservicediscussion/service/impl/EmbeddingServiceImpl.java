package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.service.IEmbeddingService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements IEmbeddingService {

    private final EmbeddingModel embeddedModel;

    @Override
    public Mono<Embedding> generateEmbedding(String text) {
        return Mono.fromCallable(() -> {
            if (!StringUtils.hasText(text)) {
                throw new InvalidOperationException("Text is null", ErrorCodes.Embedding_Text_Not_Provided);
            }
            return embeddedModel.embed(text).content();
        }).subscribeOn(Schedulers.boundedElastic());

    }
}
