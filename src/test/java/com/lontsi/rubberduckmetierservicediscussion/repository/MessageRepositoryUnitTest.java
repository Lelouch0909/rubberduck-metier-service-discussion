package com.lontsi.rubberduckmetierservicediscussion.repository;

import com.lontsi.rubberduckmetierservicediscussion.models.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class MessageRepositoryUnitTest {

    @Autowired
    private IMessageRepository messageRepository;


    @BeforeEach
    public void setUp() {
        messageRepository.deleteAll().block();
    }


    @Test

    @DisplayName(" Creation d'une nouvelle discussion - Success")
    public void shouldCreateDiscussion() {

        Message message = new  Message();
        message.setContent("content");
        message.setIdDiscussion("id_discussion");
        StepVerifier.create(messageRepository.save(message))
                .assertNext(savedDiscussion -> {
                    assertEquals(message.getContent(), savedDiscussion.getContent());
                    assertEquals(message.getIdDiscussion(), savedDiscussion.getIdDiscussion());
                    assertEquals(message.getSender(), savedDiscussion.getSender());
                    assertNotNull(savedDiscussion.getCreationDate());
                    assertNotNull(savedDiscussion.getModificationDate());
                    assertNotNull(savedDiscussion.getId());
                })


                .verifyComplete();
    }


    @AfterEach
    public void tearDown() {

        messageRepository.deleteAll().block();
    }


}
