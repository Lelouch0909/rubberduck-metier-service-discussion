package com.lontsi.rubberduckmetierservicediscussion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.web.reactive.config.EnableWebFlux;


@SpringBootApplication
@EnableDiscoveryClient
@EnableReactiveMongoAuditing
@EnableWebFlux
public class RubberduckMetierServiceDiscussionApplication {

    public static void main(String[] args) {


        SpringApplication.run(RubberduckMetierServiceDiscussionApplication.class, args);

    }


}
