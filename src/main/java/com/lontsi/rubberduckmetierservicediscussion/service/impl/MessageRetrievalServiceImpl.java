package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.service.IEmbeddingService;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageRetrievalService;
import com.lontsi.rubberduckmetierservicediscussion.service.IVectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageRetrievalServiceImpl implements IMessageRetrievalService {


    private final IEmbeddingService embeddingService;


    private final IVectorStoreService vectorStoreService;

    @Override
    public Mono<String> enrichPromptWithContext(String prompt, String idDiscussion) {
        // 1. On genere l embedding du message
        return embeddingService.generateEmbedding(prompt).flatMap(embedding -> {
            // 2. Recherche vectorielle

            return vectorStoreService.findRelevant(embedding, 5, idDiscussion).flatMap(similarMessage -> {
                // 3. retourner le prompt enrichis
                String result = """
                Prev Context :
                %s

                User Message :
                %s
                """.formatted(similarMessage, prompt);
                return Mono.just(result);
            });
        });

    }
}
