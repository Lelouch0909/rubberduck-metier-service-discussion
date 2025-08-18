package com.lontsi.rubberduckmetierservicediscussion.config;

import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class PulsarConnectorConfig {

    @Value("${pulsar.service-url}")
    private String serviceUrl;

    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .build();
    }

}
