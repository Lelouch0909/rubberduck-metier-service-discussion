package com.lontsi.rubberduckmetierservicediscussion.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "Discussion")
@NoArgsConstructor
@AllArgsConstructor
public class Discussion extends AbstractEntity {

    @Field("id_user")
    private String idUser;
    @Field("title")
    private String title;
}
