package com.lontsi.rubberduckmetierservicediscussion.service;

import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

public interface IVectorStoreService {

        Mono<String> storeDocument(VectorDocument vectorDocument);

}
