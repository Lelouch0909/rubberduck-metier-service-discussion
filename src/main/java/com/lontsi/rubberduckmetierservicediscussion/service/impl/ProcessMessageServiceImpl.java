package com.lontsi.rubberduckmetierservicediscussion.service.impl;

import com.lontsi.rubberduckmetierservicediscussion.dto.MessageProducerDto;
import com.lontsi.rubberduckmetierservicediscussion.dto.request.MessageRequestDto;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageProducerService;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageRetrievalService;
import com.lontsi.rubberduckmetierservicediscussion.service.IMessageService;
import com.lontsi.rubberduckmetierservicediscussion.service.IProcessServiceMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProcessMessageServiceImpl implements IProcessServiceMessage {

    private final IMessageService messageService;
    private final IMessageRetrievalService messageRetrievalService;
    private final IMessageProducerService messageProducerService;

    @Override
    public Mono<Void> processMessage(MessageProducerDto requestDto) {
        return messageService.saveMessage(new MessageRequestDto(requestDto.id_discussion(), requestDto.content(), requestDto.model(), requestDto.mode()))
                .then(messageService.isFirstMessage(requestDto.id_discussion()).flatMap((isfirst) -> {
                    if (!isfirst) {
                        return messageRetrievalService
                                .enrichPromptWithContext(requestDto.content(), requestDto.id_discussion())
                                .flatMap(enrichedPrompt ->
                                        messageProducerService.sendMessage(
                                                new MessageProducerDto(requestDto.principal(), enrichedPrompt, requestDto.id_discussion(), requestDto.model(), requestDto.tier(), requestDto.mode())
                                        )
                                );
                    } else {
                        return messageProducerService
                                .sendMessage(new MessageProducerDto(requestDto.principal(), requestDto.id_discussion(), requestDto.content(), requestDto.model(), requestDto.tier(), requestDto.mode()));
                    }
                })).then();

    }


}
