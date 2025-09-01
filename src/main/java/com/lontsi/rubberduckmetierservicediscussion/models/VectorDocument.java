package com.lontsi.rubberduckmetierservicediscussion.models;


import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;
import dev.langchain4j.data.embedding.Embedding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VectorDocument {

    private String id_discussion;
    private String id_Message;
    private Embedding embedding;
    private String text;
    private Sender sender;
}
