package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.dto.EnrichedContextDto;
import com.lontsi.rubberduckmetierservicediscussion.service.IEmbeddingService;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageRetrievalService;
import com.lontsi.rubberduckmetierservicediscussion.service.IVectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageRetrievalServiceImpl implements IMessageRetrievalService {


    private final IEmbeddingService embeddingService;


    private final IVectorStoreService vectorStoreService;

    @Override
    public Mono<String> enrichPromptWithContext(String prompt, String idDiscussion) {
        // 1. On genere l embedding du message
        return embeddingService.generateEmbedding(prompt).flatMap(embedding -> {
            // 2. Recherche vectorielle

            return vectorStoreService.findRelevant(embedding,  idDiscussion).flatMap(historyList -> {
                // 3. retourner le prompt enrichis
                String history = historyList.stream()
                        .sorted(Comparator.comparing(EnrichedContextDto::timestamp))
                        .map(ctx -> "%s: %s".formatted(ctx.sender(), ctx.text()))
                        .collect(Collectors.joining("\n"));
                String result = """
                        Conversation history :
                        %s
                        
                        Current user message:
                        User: %s
                        """.formatted(history, prompt);

                return Mono.just(result);
            }).doOnError(e -> log.error("Erreur à l'enrichissement du prompt: {} ", e.getMessage()));
        });

    }
}
