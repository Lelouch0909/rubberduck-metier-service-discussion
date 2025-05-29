package com.lontsi.rubberduckmetierservicediscussion.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Sinks;

@RestController
public class DemoController {

    private final Sinks.Many<String> sink;
    public DemoController(Sinks.Many<String> sink) {
        this.sink = sink;
    }
    // Endpoint to send a message to the WebSocket
    @PostMapping("/demo")
    public void sendMessage() {
        sink.emitNext("hello",Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
