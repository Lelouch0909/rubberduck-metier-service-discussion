package com.lontsi.rubberduckmetierservicediscussion.dto;

import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarMessage;

@PulsarMessage(schemaType = SchemaType.JSON)
public record MessageConsumerDto(String principal, String id_discussion, String content) {
}
