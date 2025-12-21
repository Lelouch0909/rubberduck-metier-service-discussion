package com.lontsi.rubberduckmetierservicediscussion.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.github.GitHubModelsEmbeddingModel;
import dev.langchain4j.model.github.GitHubModelsEmbeddingModelName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class EmbeddingConfig {

    @Value("${model.github.token}")
    private String GITHUB_TOKEN ;

    @Bean
    public EmbeddingModel embeddingModel(){
            return GitHubModelsEmbeddingModel.builder()
                    .gitHubToken(GITHUB_TOKEN)
                    .modelName(GitHubModelsEmbeddingModelName.TEXT_EMBEDDING_3_LARGE)
                    .dimensions(3072)
                    .logRequestsAndResponses(false)
                    .maxRetries(2)
                    .timeout(Duration.ofSeconds(10))
                    .build();
    }
}
