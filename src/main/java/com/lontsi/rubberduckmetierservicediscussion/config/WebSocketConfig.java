package com.lontsi.rubberduckmetierservicediscussion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.Scannable;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class WebSocketConfig {


    /*
        A la reception de la requete, spring recherche quel handler peut gerer l url /ws/chat ->
        SimpleUrlHandlerMapping est un routeur spring il va mapper les url aux handlers correspndants
        en ajoutant une priorite si il y en a pluseurs
    *  Permet de lier l url à une session specifique ici de websocket
    * */
    @Bean
    public SimpleUrlHandlerMapping handlerMapping(WebSocketHandler handler){
        return new SimpleUrlHandlerMapping(Map.of("/ws/chat", handler), 10);
    }

    /*
    *   Il s occupe de faire la connexion (WebSocketHandlerAdapter), recoit la requete http,
    *  verifie si tout est correct et cree la websocket session et enfin appelle
    * la CustomWebSocketHandler.handle(session)
    * */
    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter(){
        return new WebSocketHandlerAdapter();
    }
    @Bean
    public ConcurrentMap<String, WebSocketSession> sessionMap() {
        return new ConcurrentHashMap<>();
    }


    @Bean
    public Sinks.Many<String> sink(){
        return Sinks.many().multicast().directBestEffort();
    }
}
