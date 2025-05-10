package com.lontsi.rubberduckmetierservicediscussion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")   // url d entre a la connexion websocket
                .setAllowedOrigins("*") // domaine autorisé à se connecter
                .withSockJS()
                .setInterceptors(httpSessionHandshakeInterceptor())
        ;          // configuration de la connexion
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {

            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                String principal = request.getHeaders().getFirst("X-User-Principal");
                if (principal != null) {
                    attributes.put("PRINCIPAL", principal);
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue");  // configuration du bus de message ( la diffusion des messages se
                                                // fera aux clients abonnes a des destinations commencant par /topic & /queue )

        registry.setApplicationDestinationPrefixes("/app");  // prefix des destinations des messages pour les messages de l application
        registry.setUserDestinationPrefix("/user");  // prefix des destinations des messages pour les messages de l utilisateur
    }
}
