package com.lontsi.rubberduckmetierservicediscussion.dto;

import dev.langchain4j.data.document.Metadata;

import java.time.Instant;

public record EnrichedContextDto(String text, double score, String sender, Instant timestamp) {
}
