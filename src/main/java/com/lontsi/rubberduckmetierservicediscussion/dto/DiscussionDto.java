package com.lontsi.rubberduckmetierservicediscussion.dto;

import com.lontsi.rubberduckmetierservicediscussion.models.Discussion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Schema(name = "Discussion-Dto", description = "Dto de la discussion",
        example = "{\n" +
                "  \"idDiscussion\": \"idDiscussion\",\n" +
                "  \"title\": \"title\",\n" +
                "  \"creationDate\": \"2023-05-04T10:00:00.000Z\",\n" +
                "  \"modificationDate\": \"2023-05-04T10:00:00.000Z\"\n" +
                "}")
@Builder
@Data
public class DiscussionDto {

    private String idDiscussion;

    private String title;

    private Instant creationDate;
    private Instant modificationDate;


    public static DiscussionDto fromEntity(Discussion discussion) {
        return DiscussionDto.builder()
                .idDiscussion(discussion.getId())
                .title(discussion.getTitle())
                .creationDate(discussion.getCreationDate())
                .modificationDate(discussion.getModificationDate())
                .build();
    }


}
