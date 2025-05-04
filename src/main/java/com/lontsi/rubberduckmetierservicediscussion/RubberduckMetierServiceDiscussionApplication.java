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

    public static boolean isNarcissistic(int number) {
        final double[] result = {0};
        List<Integer> split =  RubberduckMetierServiceDiscussionApplication.decompose(number);
        split.forEach( i -> result[0] = result[0] + Math.pow(i,split.size()));
        return number == result[0];
    }

        public static List<Integer> decompose(int number){
            return  Arrays.stream(String.valueOf(number).split(""))
                    .map(Integer::parseInt).toList();
        }

}
