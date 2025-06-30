package com.lontsi.rubberduckmetierservicediscussion.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.ModelsDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.service.IProcessServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;

/*
 *
 * WebsocketHandler interface qui dis comment gerer les connexions websocket
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomWebSocketHandler implements WebSocketHandler {

    private final IProcessServiceMessage processServiceMessage;
    private final ConcurrentMap<String, WebSocketSession> sessionMap;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Récupère le principal authentifié à partir du handshake WebSocket
        return session.getHandshakeInfo().getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    // Vérifie que l'utilisateur a bien le rôle USER
                    if (!hasRole(authentication, "ROLE_USER")) {
                        log.warn("Tentative de connexion sans le rôle USER par {}", authentication.getName());
                        // Ferme la session WebSocket avec un code explicite si rôle manquant
                        return session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Vous devez être connecté pour interagir avec le chat"));
                    }

                    // Vérifie si l'utilisateur a le rôle PREMIUM pour l'accès aux modèles payants
                    boolean isPremiumUser = hasRole(authentication, "ROLE_PREMIUM");

                    // Gère la réception des messages WebSocket
                    return session.receive()
                            .flatMap(message -> {
                                try {
                                    String payload = message.getPayloadAsText();
                                    MessageRequestDto dto = parseMessage(payload);
                                    log.info("Received message: {} from session: {} by user: {}", payload, dto.id_discussion(), authentication.getName());

                                    // Vérifie si le modèle demandé est gratuit ou premium
                                    boolean isFreeModels = ModelsDto.freeModel.contains(dto.model());
                                    if (!isFreeModels && !isPremiumUser) {
                                        log.warn("Accès refusé au modèle premium {} pour {}", dto.model(), authentication.getName());
                                        // Envoie une erreur au client et ferme la connexion
                                        return session.send(
                                                Mono.just(session.textMessage("Ce modèle est réservé aux utilisateurs premium"))
                                        );
                                    }

                                    // Associe la session au channel (discussion)
                                    sessionMap.put(dto.id_discussion(), session);

                                    // Traite le message via le service, en passant le principal
                                    return processServiceMessage.processMessage(
                                            new MessageProducerDto(authentication.getName(), dto.id_discussion(), dto.content(), dto.model())
                                    );
                                } catch (Exception e) {
                                    log.error("Error while sending message", e);
                                    // Renvoie une erreur au client (sans fermer la session pour une simple erreur de parsing)
                                    return session.send(
                                            Mono.just(session.textMessage("Erreur lors de la réception du message : " + e.getMessage()))
                                    );
                                }
                            })
                            .onErrorResume(e -> {
                                log.error("Erreur côté handler WebSocket : {}", e.getMessage());
                                // Envoie l'erreur au client puis ferme la session
                                return session.send(
                                        Mono.just(session.textMessage("Erreur : " + e.getMessage()))
                                ).then(session.close(CloseStatus.SERVER_ERROR.withReason("Erreur interne : " + e.getMessage())));
                            })
                            // Mono.never() : la connexion reste ouverte tant que le client ne ferme pas
                            .then(Mono.never());
                });
    }

    /*
     * Vérifie si l'utilisateur possède un rôle donné
     */
    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Transforme le payload (json string) en un objet MessageRequestDto
     */
    private MessageRequestDto parseMessage(String payload) {
        try {
            return objectMapper.readValue(payload, MessageRequestDto.class);
        } catch (JsonProcessingException e) {
            log.error("Erreur de parsing du message : {}", e.getMessage());
            throw new RuntimeException("Invalid message format", e);
        }
    }

}