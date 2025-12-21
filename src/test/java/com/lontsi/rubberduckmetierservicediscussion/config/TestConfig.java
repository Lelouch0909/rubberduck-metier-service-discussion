package com.lontsi.rubberduckmetierservicediscussion.config;

import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.apache.pulsar.client.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ActiveProfiles("test")
public class TestConfig {


    @Bean
    public MongoDbEmbeddingStore mongoDbEmbeddingStore() {
        return mock(MongoDbEmbeddingStore.class);
    }

    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        PulsarClient pulsarClient = mock(PulsarClient.class);
        ProducerBuilder<byte[]> producerBuilder = mock(ProducerBuilder.class);
        ConsumerBuilder<byte[]> consumerBuilder = mock(ConsumerBuilder.class);

        // Mock the consumer
        Consumer<byte[]> consumer = mock(Consumer.class);

        // Configure to return a mock message
        Message<byte[]> mockMessage = mock(Message.class);
        when(mockMessage.getData()).thenReturn("{\"content\":\"test message\"}".getBytes());

        // Configure receive() behavior
        when(consumer.receive()).thenReturn(mockMessage);

        // Configuration for Producer
        when(pulsarClient.newProducer()).thenReturn(producerBuilder);
        when(producerBuilder.topic(anyString())).thenReturn(producerBuilder);
        when(producerBuilder.create()).thenReturn(mock(Producer.class));

        // Configuration for Consumer
        when(pulsarClient.newConsumer()).thenReturn(consumerBuilder);
        when(consumerBuilder.topic(anyString())).thenReturn(consumerBuilder);
        when(consumerBuilder.subscriptionName(anyString())).thenReturn(consumerBuilder);
        when(consumerBuilder.subscriptionType(any())).thenReturn(consumerBuilder);
        when(consumerBuilder.messageListener(any())).thenReturn(consumerBuilder);
        when(consumerBuilder.subscribe()).thenReturn(consumer); // Return our configured consumer

        return pulsarClient;
    }

}
