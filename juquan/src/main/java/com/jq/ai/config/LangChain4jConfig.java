package com.jq.ai.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j 模型配置。
 */
@Configuration
public class LangChain4jConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(AiProperties aiProperties) {
        return OllamaChatModel.builder()
                .baseUrl(aiProperties.getBaseUrl())
                .modelName(aiProperties.getModel())
                .temperature(0.7)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel(AiProperties aiProperties) {
        return OllamaEmbeddingModel.builder()
                .baseUrl(aiProperties.getEmbeddingUrl() == null || aiProperties.getEmbeddingUrl().isBlank()
                        ? aiProperties.getBaseUrl()
                        : aiProperties.getEmbeddingUrl())
                .modelName(aiProperties.getEmbeddingModel())
                .build();
    }
}
