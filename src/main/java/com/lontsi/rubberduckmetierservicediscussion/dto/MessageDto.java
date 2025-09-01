package com.lontsi.rubberduckmetierservicediscussion.dto;

import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;

import java.time.Instant;

public record MessageDto(String idDiscussion, String content,  Sender sender) {
}
