package com.lontsi.rubberduckmetierservicediscussion.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lontsi.rubberduckmetierservicediscussion.dto.AssistantTier;
import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.ModelsDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.service.IDiscussionService;
import com.lontsi.rubberduckmetierservicediscussion.service.IProcessServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

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
    private final IDiscussionService discussionService;
//
//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//        // Récupère le principal authentifié à partir du handshake WebSocket
//        return session.getHandshakeInfo().getPrincipal()
//                .cast(Authentication.class)
//                .flatMap(authentication -> {
//                    // Vérifie que l'utilisateur a bien le rôle USER
//                    if (!hasRole(authentication, "ROLE_USER")) {
//                        log.warn("Tentative de connexion sans le rôle USER par {}", authentication.getName());
//                        // Ferme la session WebSocket avec un code explicite si rôle manquant
//                        return session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Vous devez être connecté pour interagir avec le chat"));
//                    }
//
//                    // Vérifie si l'utilisateur a le rôle PREMIUM pour l'accès aux modèles payants
//                    boolean isPremiumUser = hasRole(authentication, "ROLE_PREMIUM");
//                    boolean isProfessionalUser = hasRole(authentication, "ROLE_PROFESSIONAL");
//
//                    // Gère la réception des messages WebSocket
//                    return session.receive()
//                            .flatMap(message -> {
//                                try {
//                                    String payload = message.getPayloadAsText();
//                                    MessageRequestDto dto = parseMessage(payload);
//                                    log.info("Received message: {} from session: {} by user: {}", payload, dto.id_discussion(), authentication.getName());
//                                    // Vérifie si le modèle demandé est gratuit ou premium
//                                    boolean isFreeModels = ModelsDto.freeModel.contains(dto.model());
//                                    if (!isFreeModels && !isPremiumUser && !isProfessionalUser) {
//                                        log.warn("Accès refusé au modèle premium {} pour {}", dto.model(), authentication.getName());
//                                        // Envoie une erreur au client et ferme la connexion
//                                        return session.send(
//                                                Mono.just(session.textMessage("Ce modèle est réservé aux utilisateurs premium et professionels"))
//                                        );
//                                    }
//
//                                    // Associe la session au channel (discussion)
//                                    sessionMap.put(dto.id_discussion(), session);
//                                    AssistantTier tier = AssistantTier.STANDARD;
//                                    if (isPremiumUser) {
//                                        tier = AssistantTier.PREMIUM;
//                                    }
//                                    if (isPremiumUser) {
//                                        tier = AssistantTier.PROFESSIONAL;
//                                    }
//                                    // Traite le message via le service, en passant le principal
//                                    if (!discussionService.isUserDiscussion(authentication.getName(),dto.id_discussion()).block()) throw new InvalidOperationException("IdDiscussion n'existe pas ou n est pas celui de cet utilisateur");
//
//                                    return processServiceMessage.processMessage(
//                                            new MessageProducerDto(authentication.getName(), dto.id_discussion(), dto.content(), dto.model(), tier, dto.mode())
//                                    );
//                                } catch (Exception e) {
//                                    log.error("Error while sending message", e);
//                                    // Renvoie une erreur au client (sans fermer la session pour une simple erreur de parsing)
//                                    return session.send(
//                                            Mono.just(session.textMessage("Erreur lors de la réception du message : " + e.getMessage()))
//                                    );
//                                }
//                            })
//                            .onErrorResume(e -> {
//                                log.error("Erreur côté handler WebSocket : {}", e.getMessage());
//                                // Envoie l'erreur au client puis ferme la session
//                                return session.send(
//                                        Mono.just(session.textMessage("Erreur : " + e.getMessage()))
//                                ).then(session.close(CloseStatus.SERVER_ERROR.withReason("Erreur interne : " + e.getMessage())));
//                            })
//                            //  la connexion reste ouverte tant que le client ne ferme pas
//                            .then(Mono.never());
//                });
//    }
//

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("New WebSocket connection attempt");

        return session.getHandshakeInfo().getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> handleAuthenticatedSession(session, authentication))
                .doOnError(error -> log.error(" WebSocket handler error: {}", error.getMessage()))
                .onErrorResume(error -> closeSessionWithError(session, error));
    }

    private Mono<Void> handleAuthenticatedSession(WebSocketSession session, Authentication authentication) {
        //  Vérification des rôles
        if (!hasRole(authentication, "ROLE_USER")) {
            log.warn("Access denied for user {} - Missing ROLE",
                    authentication.getName());
            return session.close(CloseStatus.NOT_ACCEPTABLE
                    .withReason("Vous devez être connecté pour interagir avec le chat"));
        }

        //  Détermination du tier utilisateur
        AssistantTier userTier = determineUserTier(authentication);
        log.info(" User {} connected with tier: {}",
                authentication.getName(), userTier);

        // Flux réactif principal - réception des messages
        return session.receive()
                .flatMap(webSocketMessage -> handleIncomingMessage(session, webSocketMessage, authentication, userTier))
                .doOnComplete(() -> cleanupSession(session, authentication))
                .doOnCancel(() -> cleanupSession(session, authentication))
                .onErrorResume(error -> handleSessionError(session, error, authentication))
                .then(Mono.never()); // Garde la connexion ouverte
    }

    private Mono<Void> handleIncomingMessage(WebSocketSession session, WebSocketMessage webSocketMessage,
                                             Authentication authentication, AssistantTier userTier) {

        return Mono.fromCallable(webSocketMessage::getPayloadAsText)
                .flatMap(this::parseAndValidateMessage)
                .flatMap(dto -> validateMessageAccess(dto, authentication, userTier))
                .flatMap(dto -> validateDiscussionOwnership(dto, authentication))
                .flatMap(dto -> processValidMessage(session, dto, authentication, userTier))
                .onErrorResume(error -> sendErrorToClient(session, error));
    }

    private Mono<MessageRequestDto> validateDiscussionOwnership(MessageRequestDto dto, Authentication authentication) {
        String username = authentication.getName();

        log.info(" [{}] Validating ownership: user={}, discussion={}",
                username, username, dto.id_discussion());

        return discussionService.isUserDiscussion(username, dto.id_discussion()) // ✅ Pas de .block() !
                .flatMap(isOwner -> {
                    if (isOwner) {
                        log.info("User {} owns discussion {}", username, dto.id_discussion());
                        return Mono.just(dto);
                    } else {
                        log.warn(" Access denied: User {} doesn't own discussion {}",
                                username, dto.id_discussion());
                        return Mono.error(new InvalidOperationException(
                                "Discussion not found or access denied for user: " + username));
                    }
                });
    }

    private Mono<MessageRequestDto> parseAndValidateMessage(String payload) {
        return Mono.fromCallable(() -> {
                    log.info("Parsing message: {}", payload);
                    return parseMessage(payload);
                })
                .onErrorMap(JsonProcessingException.class, error ->
                        new InvalidOperationException("Invalid JSON format: " + error.getMessage()));
    }

    private Mono<MessageRequestDto> validateMessageAccess(MessageRequestDto dto, Authentication authentication,
                                                          AssistantTier userTier) {
        return Mono.fromCallable(() -> {
            boolean isFreeModel = ModelsDto.freeModel.contains(dto.model());
            boolean hasAccess = isFreeModel || userTier != AssistantTier.STANDARD;

            if (!hasAccess) {
                String username = authentication.getName();
                log.warn("Model access denied: user={}, model={}, tier={}",
                        username, dto.model(), userTier);
                throw new InvalidOperationException(
                        "Model '" + dto.model() + "' requires premium or professional access");
            }

            log.info(" Model access granted: model={}, tier={}",
                    dto.model(), userTier);
            return dto;
        });
    }

    private Mono<Void> processValidMessage(WebSocketSession session, MessageRequestDto dto,
                                           Authentication authentication, AssistantTier userTier) {

        String username = authentication.getName();

        // ✅ Associer la session à la discussion
        sessionMap.put(dto.id_discussion(), session);
        log.info(" Session mapped: discussion={}, user={}",
                dto.id_discussion(), username);

        // ✅ Créer le message producer
        MessageProducerDto producerDto = new MessageProducerDto(
                username,
                dto.id_discussion(),
                dto.content(),
                dto.model(),
                userTier,
                dto.mode()
        );

        log.info(" [{}] Processing message: discussion={}, model={}, tier={}",
                username, dto.id_discussion(), dto.model(), userTier);

        return processServiceMessage.processMessage(producerDto)
                .doOnSuccess(result -> log.info("Message processed successfully for user {}", username))
                .doOnError(error -> log.error(" Error processing message for user {}: {}",
                        username, error.getMessage()));
    }


    private Mono<Void> sendErrorToClient(WebSocketSession session, Throwable error) {
        String errorMessage = determineUserFriendlyError(error);
        log.error(" [ Sending error to client: {}", errorMessage);

        return session.send(Mono.just(session.textMessage(errorMessage)))
                .doOnSuccess(v -> log.info("Error message sent to client"))
                .onErrorResume(sendError -> {
                    log.error(" Failed to send error message: {}", sendError.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Void> handleSessionError(WebSocketSession session, Throwable error, Authentication authentication) {
        String username = authentication.getName();
        log.error(" Session error for user {}: {}", username, error.getMessage());

        return sendErrorToClient(session, error)
                .then(session.close(CloseStatus.SERVER_ERROR.withReason("Internal error: " + error.getMessage())));
    }

    private Mono<Void> closeSessionWithError(WebSocketSession session, Throwable error) {
        log.error(" Closing session due to error: {}", error.getMessage());
        return session.close(CloseStatus.SERVER_ERROR.withReason(error.getMessage()));
    }

    private AssistantTier determineUserTier(Authentication authentication) {
        boolean isPremiumUser = hasRole(authentication, "ROLE_PREMIUM");
        boolean isProfessionalUser = hasRole(authentication, "ROLE_PROFESSIONAL");

        if (isProfessionalUser) {
            return AssistantTier.PROFESSIONAL;  // Le plus élevé
        } else if (isPremiumUser) {
            return AssistantTier.PREMIUM;
        } else {
            return AssistantTier.STANDARD;
        }
    }


    private void cleanupSession(WebSocketSession session, Authentication authentication) {
        String username = authentication.getName();
        log.info("Cleaning up session for user: {}", username);

        // Supprimer la session de toutes les discussions
        sessionMap.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }

    private String determineUserFriendlyError(Throwable error) {
        log.debug("Determining user-friendly error for: {}",
                error.getClass().getSimpleName());

        if (error instanceof InvalidOperationException ioe) {
            return "❌ " + ioe.getMessage();
        } else if (error instanceof JsonProcessingException jpe) {
            return " Erreur de format JSON";
        } else if (error instanceof TimeoutException te) {
            return " Timeout - Veuillez réessayer";
        } else if (error instanceof IllegalArgumentException iae) {
            return " Paramètres invalides: " + iae.getMessage();
        } else if (error instanceof RuntimeException re) {
            log.error("Unexpected runtime error: {}", re.getMessage());
            return " Une erreur inattendue s'est produite";
        } else {
            log.error(" Unknown error type: {}", error.getClass().getName());
            return " Erreur système - Contactez l'administrateur";
        }
    }

    /*
     * Vérifie si l'utilisateur possède un rôle donné
     */
    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            log.warn("Authorities : " + authority);
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