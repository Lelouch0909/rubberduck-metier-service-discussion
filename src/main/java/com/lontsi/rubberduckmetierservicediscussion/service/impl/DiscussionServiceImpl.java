package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.dto.DiscussionDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.DiscussionRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidEntityException;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.models.Discussion;
import com.lontsi.rubberduckmetierservicediscussion.repository.IDiscussionRepository;
import com.lontsi.rubberduckmetierservicediscussion.service.IDiscussionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DiscussionServiceImpl implements IDiscussionService {


    private final IDiscussionRepository discussionRepository;

    @Value("service.discussion.default_title")
    private String default_title;

    @Autowired
    DiscussionServiceImpl(IDiscussionRepository discussionRepository) {
        this.discussionRepository = discussionRepository;
    }

    @Override
    public Mono<String> createDiscussion(String idUser) {
        if (idUser == null || idUser.isEmpty()) {
            throw new InvalidEntityException("idUser null ou vide", ErrorCodes.ID_USER_NULL);
        }
        DiscussionRequestDto reqdto = new DiscussionRequestDto(idUser, default_title);
        return discussionRepository.save(new Discussion(reqdto.id_user(), reqdto.title()))
                .onErrorResume(
                        ex -> Mono.error(new InvalidOperationException("erreur lors de la creation de la discussion",
                                ex.getCause(), ErrorCodes.DISCUSSION_SAVE_ERROR)
                        ))
                .map(
                        Discussion::getId
                );
    }

    @Override
    public Flux<DiscussionDto> findAllUserDiscussion(String idUser) {
        if (idUser == null || idUser.isEmpty()) {
            throw new InvalidEntityException("idUser null ou vide", ErrorCodes.ID_USER_NULL);
        }
        log.warn("---------------idUser---------: {}", idUser);
        Flux<Discussion> discussions = discussionRepository.findAllByIdUser(idUser)
                .doOnNext(discussion -> log.warn("Discussion trouvée : {}", discussion));

        return discussions.map(DiscussionDto::fromEntity);
    }
}
