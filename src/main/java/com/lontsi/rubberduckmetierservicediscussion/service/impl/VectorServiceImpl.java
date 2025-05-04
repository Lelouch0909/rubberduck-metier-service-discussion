package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import com.lontsi.rubberduckmetierservicediscussion.service.IVectorStoreService;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VectorServiceImpl implements IVectorStoreService {

    @Autowired
    private  MongoDbEmbeddingStore embeddingStore;


    @Override
    public Mono<String> storeDocument(VectorDocument vectorDocument) {
        TextSegment textSegment = TextSegment.from(vectorDocument.getId_Message());
        textSegment.metadata().put("id_discussion", vectorDocument.getId_discussion());
        try {
           String id = embeddingStore.add(vectorDocument.getEmbedding(), textSegment);
            return Mono.just(id);
        }
        catch (Exception e) {
            return Mono.error(new InvalidOperationException("Error while storing document", e, ErrorCodes.Vector_Save_Exception));
        }
    }
}
