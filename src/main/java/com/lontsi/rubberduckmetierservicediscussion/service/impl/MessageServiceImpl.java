package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.exception.ErrorCodes;
import com.lontsi.rubberduckmetierservicediscussion.exception.InvalidOperationException;
import com.lontsi.rubberduckmetierservicediscussion.models.Message;
import com.lontsi.rubberduckmetierservicediscussion.models.VectorDocument;
import com.lontsi.rubberduckmetierservicediscussion.models.type.Sender;
import com.lontsi.rubberduckmetierservicediscussion.repository.IMessageRepository;
import com.lontsi.rubberduckmetierservicediscussion.service.IEmbeddingService;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageService;
import com.lontsi.rubberduckmetierservicediscussion.service.IVectorStoreService;
import dev.langchain4j.data.embedding.Embedding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.core.RepositoryCreationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageServiceImpl implements IMessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);
    @Autowired
    private IMessageRepository messageRepository;
    @Autowired
    private IEmbeddingService embeddingService;
    @Autowired
    private IVectorStoreService vectorStoreService;


    // save the message in the database
    @Override
    public Mono<Void> saveMessage(MessageRequestDto messageRequestDto) {

        // convert to Message object
        Message message = new Message();
        message.setContent(messageRequestDto.content());
        message.setIdDiscussion(messageRequestDto.id_discussion());
        message.setSender(Sender.USER);


        // save the message in the database
        return messageRepository.save(message)
                .flatMap(savedMessage -> {
                    try {
                        // Generate embedding
                        Embedding embedding = embeddingService.generateEmbedding(message.getContent());
                        // Save embedding
                        VectorDocument vectorDocument = toVectorDocument(savedMessage, embedding);

                        return vectorStoreService.storeDocument(vectorDocument);

                    } catch (RuntimeException e) {
                        log.error("Embedding generation failed", e);
                        return Mono.error(new InvalidOperationException("Embedding error", e.getCause(), ErrorCodes.EMBEDDING_CREATION_ERROR));
                    }

                })
                .onErrorMap(DataAccessException.class, e ->
                        new InvalidOperationException("Error while saving message", e.getCause(), ErrorCodes.MESSAGE_SAVE_ERROR)
                )
                .then();

    }

    // convert to VectorDocument object
    private VectorDocument toVectorDocument(Message message, Embedding embedding) {
        return new VectorDocument(
                message.getIdDiscussion(),
                message.getId(),
                embedding

        );
    }
}
