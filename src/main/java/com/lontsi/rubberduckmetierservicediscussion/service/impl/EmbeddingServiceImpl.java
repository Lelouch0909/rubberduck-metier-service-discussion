package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.service.IEmbeddingService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmbeddingServiceImpl implements IEmbeddingService {
    private final EmbeddingModel embeddedModel;

    @Autowired
    public EmbeddingServiceImpl(AllMiniLmL6V2EmbeddingModel embeddedModel) {
        this.embeddedModel = embeddedModel;
    }


    @Override
    public Mono<Embedding> generateEmbedding(String text) {
        return Mono.fromCallable(()->{
            if (!StringUtils.hasText(text)) {
                throw new InvalidOperationException("Text is null", ErrorCodes.Embedding_Text_Not_Provided);
            }
            return embeddedModel.embed(text).content();
        }).subscribeOn(Schedulers.boundedElastic());

    }
}
