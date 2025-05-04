package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.models.Discussion;
import dev.langchain4j.data.embedding.Embedding;

import java.util.List;

public interface IEmbeddingService {

    public Embedding generateEmbedding(String text);

}
