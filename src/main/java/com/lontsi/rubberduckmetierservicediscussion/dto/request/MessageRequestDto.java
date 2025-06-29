package com.lontsi.rubberduckmetierservicediscussion.dto.request;

import com.lontsi.rubberduckmetierservicediscussion.models.type.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarMessage;

@Schema(description = "l objet a passer en requete contenant les details du message")
@PulsarMessage(schemaType = SchemaType.JSON)
public record MessageRequestDto(String id_discussion, String content, Model model) {

}
