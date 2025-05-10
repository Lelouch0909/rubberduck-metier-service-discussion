package com.lontsi.rubberduckmetierservicediscussion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "l objet a passer en requete contenant les details du message")

public record MessageRequestDto(String id_discussion, String content) {

}
