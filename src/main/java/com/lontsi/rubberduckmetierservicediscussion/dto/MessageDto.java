package com.lontsi.rubberduckmetierservicediscussion.dto;

import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;

public record MessageDto(String idDiscussion, String content,  Sender sender) {
}
