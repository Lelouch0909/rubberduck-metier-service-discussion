package com.lontsi.rubberduckmetierservicediscussion.dto;

import com.lontsi.rubberduckmetierservicediscussion.models.type.Model;

public record MessageProducerDto(String principal, String id_discussion, String content, Model model,
                                 AssistantTier tier,
                                 AssistanceMode mode) {
}
