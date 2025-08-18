package com.lontsi.rubberduckmetierservicediscussion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "l objet a passer en requete contenant les details de la discussion")
public record DiscussionRequestDto(String id_user, String title) {

}
