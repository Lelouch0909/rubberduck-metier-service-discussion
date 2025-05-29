package com.lontsi.rubberduckmetierservicediscussion.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SseDispatcher is responsible for managing Server-Sent Events (SSE) for discussions.
 * It allows clients to subscribe to a discussion and receive messages in real-time.
 */
@Component
@Slf4j
public class SseDispatcher {

    private final Map<String, Sinks.Many<String>> sinks = new ConcurrentHashMap<>();

    // Connexion d’un client à une discussion
    public Flux<ServerSentEvent<String>> subscribe(String discussionId) {
        Sinks.Many<String> sink = sinks.computeIfAbsent(discussionId,
                id -> Sinks.many().multicast().onBackpressureBuffer());

        log.warn("Client subscribed to discussion: {}", discussionId);
        return sink.asFlux()
                .map(data -> ServerSentEvent.builder(data).build())

                .doOnCancel(() -> log.info("Client disconnected from discussion: {}", discussionId))
                .onErrorResume(error -> {
                    log.error("Error in SSE stream for discussion: {}", discussionId, error);
                    return Flux.empty();
                });
    }

    // Envoi d’un message à une discussion
    public void send(String discussionId, String message) {
        Sinks.Many<String> sink = sinks.get(discussionId);
        if (sink != null) {
            sink.tryEmitNext(message);
        }
    }

    // Optionnel : fermer une discussion (par exemple après timeout)
    public void close(String discussionId) {
        Sinks.Many<String> sink = sinks.remove(discussionId);
        if (sink != null) {
            sink.tryEmitComplete();
        }
    }
}
