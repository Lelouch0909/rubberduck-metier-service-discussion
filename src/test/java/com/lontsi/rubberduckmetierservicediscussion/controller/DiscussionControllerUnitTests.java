package com.lontsi.rubberduckmetierservicediscussion.controller;

import com.lontsi.rubberduckmetierservicediscussion.service.IDiscussionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles("test")
public class DiscussionControllerUnitTests {


    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private IDiscussionService discussionService;



    @Test
    public void shouldCreateDiscussion() {

        when(discussionService.createDiscussion(anyString()))
                .thenAnswer(invocation -> {
                    String arg = invocation.getArgument(0);
                    System.out.println("Service appelé avec: " + arg); // Debug
                    return Mono.just("12345");
                });
        WebTestClient authenticatedClient = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser("user").roles("USER"));


        // 3. Exécutez le test
        authenticatedClient.post()
                .uri("/discussion/creatediscussion/create")
                .header("X-User-Principal", "user")
                .header("X-User-Authorities", "ROLE_USER")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.idUser").isEqualTo("12345");
    }
}
