package com.lontsi.rubberduckmetierservicediscussion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

import java.util.Arrays;
import java.util.*;

@SpringBootApplication
@EnableReactiveMongoAuditing
//@EnableDiscoveryClient
public class RubberduckMetierServiceDiscussionApplication {

    public static void main(String[] args) {


            SpringApplication.run(RubberduckMetierServiceDiscussionApplication.class, args);

    }


}
