package com.lontsi.rubberduckmetierservicediscussion.controller;

import com.lontsi.rubberduckmetierservicediscussion.controller.api.IDiscussionApi;
import com.lontsi.rubberduckmetierservicediscussion.dto.DiscussionDto;
import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidEntityException;
import com.lontsi.rubberduckmetierservicediscussion.service.IDiscussionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.lontsi.rubberduckmetierservicediscussion.config.Utils.DISCUSSION_ENDPOINT;

@Slf4j
@RestController(DISCUSSION_ENDPOINT)
public class DiscussionController implements IDiscussionApi {

    private final IDiscussionService discussionService;

    @Autowired
    public DiscussionController(IDiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @Override
    public Mono<String> createDiscussion() {
        return ReactiveSecurityContextHolder.getContext().flatMap(
                SecurityContext -> {

                    String principal = SecurityContext.getAuthentication().getName().toString();
                    log.warn("Auth header detected: principal={}", principal);

                    if (principal == null) {
                        // Pas d'auth info, on continue sans auth
                        return Mono.error(new InvalidEntityException("erreur lors de la creation de la discussion idUser null ou vide",
                                ErrorCodes.PRINCIPAL_NULL));
                    }
                    return discussionService.createDiscussion(principal);
                }
        );
    }

    @Override
    public Flux<DiscussionDto> findAllUserDiscussion() {

        return ReactiveSecurityContextHolder.getContext().flatMapMany(
                SecurityContext -> {

                    String principal = SecurityContext.getAuthentication().getName().toString();

                    if (principal == null) {
                        // Pas d'auth info, on continue sans auth
                        return Flux.error(new InvalidEntityException("erreur lors de la creation de la discussion idUser null ou vide",
                                ErrorCodes.PRINCIPAL_NULL));
                    }
                    return discussionService.findAllUserDiscussion(principal);
                }
        );
    }
}
