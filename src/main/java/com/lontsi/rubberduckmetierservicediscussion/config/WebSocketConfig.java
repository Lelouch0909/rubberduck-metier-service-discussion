package com.lontsi.rubberduckmetierservicediscussion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")   // url d entre a la connexion websocket
                .setAllowedOrigins("*") // domaine autorisé à se connecter
                .withSockJS();          // configuration de la connexion
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue");  // configuration du bus de message ( la diffusion des messages se
                                                // fera aux clients abonnes a des destinations commencant par /topic & /queue )

        registry.setApplicationDestinationPrefixes("/app");  // prefix des destinations des messages pour les messages de l application
        registry.setUserDestinationPrefix("/user");  // prefix des destinations des messages pour les messages de l utilisateur
    }
}
