package com.lontsi.rubberduckmetierservicediscussion.models;


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
}
