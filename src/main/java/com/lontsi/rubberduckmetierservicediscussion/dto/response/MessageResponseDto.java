package com.lontsi.rubberduckmetierservicediscussion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "l objet retourne a la reception d un message")
public record MessageResponseDto (String id_discussion, String response) {
}
