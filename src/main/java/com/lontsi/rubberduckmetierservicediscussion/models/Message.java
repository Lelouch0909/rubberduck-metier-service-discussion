package com.lontsi.rubberduckmetierservicediscussion.models;

import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@EqualsAndHashCode(callSuper = true)
@Document(collection = "Message")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message extends AbstractEntity {

    @Field("id_discussion")
    private String idDiscussion;

    @Field("content")
    private String content;

    @Field("Sender")
    private Sender Sender;

}
