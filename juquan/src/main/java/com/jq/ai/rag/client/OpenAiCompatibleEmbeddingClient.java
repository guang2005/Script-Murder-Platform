package com.jq.ai.rag.client;

import com.jq.ai.config.AiProperties;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 基于 LangChain4j 的向量客户端。
 */
@Component
public class OpenAiCompatibleEmbeddingClient implements EmbeddingClient {

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private AiProperties aiProperties;

    @Override
    public double[] embed(String text) {
        if (!aiProperties.isEnabled()) {
            throw new IllegalStateException("AI 功能未启用");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("待向量化文本不能为空");
        }

        Response<Embedding> response = embeddingModel.embed(text);
        float[] vector = response.content().vector();
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i];
        }
        return result;
    }
}
