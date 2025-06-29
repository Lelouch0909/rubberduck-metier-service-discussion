package com.lontsi.rubberduckmetierservicediscussion.controller.api;


import com.lontsi.rubberduckmetierservicediscussion.dto.DiscussionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.lontsi.rubberduckmetierservicediscussion.config.Utils.DISCUSSION_ENDPOINT;

@Schema(name = "Discussion-Api", description = "API de gestion des discussions ")
@PreAuthorize("hasRole('USER')")
public interface IDiscussionApi {

    @PostMapping(DISCUSSION_ENDPOINT + "/create")
    @Operation(summary = "Creer une nouvelle discussion", description = "Créer une nouvelle discussion pour l utilisateur et retourne son id" +
            "de la discussion qui apres les premiers messages sera update pour correspondre au sujet de la discussion")
    @ApiResponse(
            responseCode = "201",
            description = "Discussion créée",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(type = "string",example = "01102465416554")
            )

    )
    @ResponseStatus(HttpStatus.CREATED)
    Mono<String> createDiscussion();

    @GetMapping(DISCUSSION_ENDPOINT + "/findAllUserDiscussion")
    @Operation(summary = "Rechercher toutes les discussions d'un utilisateur", description = "retourne la liste des discussions")
            @ApiResponse(
            responseCode = "302",
            description = "Discussions trouvées",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = DiscussionDto.class))
            )

    )
    @ResponseStatus(HttpStatus.FOUND)
    Flux<DiscussionDto> findAllUserDiscussion();

}
