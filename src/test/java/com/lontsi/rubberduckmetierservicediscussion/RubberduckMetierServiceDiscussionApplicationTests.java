package com.lontsi.rubberduckmetierservicediscussion;

import com.lontsi.rubberduckmetierservicediscussion.config.MongoStoreConfig;
import com.lontsi.rubberduckmetierservicediscussion.config.PulsarConnectorConfig;
import com.lontsi.rubberduckmetierservicediscussion.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RubberduckMetierServiceDiscussionApplicationTests  {

    @Test
    void contextLoads() {

    }

}
