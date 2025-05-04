package com.lontsi.rubberduckmetierservicediscussion.repository;

import com.lontsi.rubberduckmetierservicediscussion.models.Discussion;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiscussionRepositoryTests {

    @Autowired
    private IDiscussionRepository discussionRepository;

    @BeforeAll
    public  void init() {

    }

    @BeforeEach
    public void setUp() {
        discussionRepository.deleteAll().block();
    }


    @Test

    @DisplayName(" Creation d'une nouvelle discussion - Success")
    public void shouldCreateDiscussion() {
        Discussion discussion = new  Discussion();
        discussion.setTitle("title");
        discussion.setIdUser("id_user");
        StepVerifier.create(discussionRepository.save(discussion))
                .assertNext(savedDiscussion -> {
                    assertEquals(discussion.getTitle(), savedDiscussion.getTitle());
                    assertEquals(discussion.getIdUser(), savedDiscussion.getIdUser());
                    org.assertj.core.api.Assertions.assertThat(savedDiscussion.getId()).isNotNull();
                    })

                .verifyComplete();
    }


    @AfterEach
    public void tearDown() {

        discussionRepository.deleteAll().block();
    }
    @AfterAll
    public void end() {
    }

}
