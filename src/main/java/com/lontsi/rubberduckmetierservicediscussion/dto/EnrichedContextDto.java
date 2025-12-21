package com.lontsi.rubberduckmetierservicediscussion.dto;

import java.time.Instant;

public record EnrichedContextDto(String text, double score, String sender, Instant timestamp) {
}
