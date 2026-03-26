package com.jq.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模型调用配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jq.ai")
public class AiProperties {

    /**
     * 是否启用 AI 调用。
     */
    private boolean enabled = true;

    /**
     * OpenAI 兼容接口地址，例如：
     * https://api.openai.com/v1/chat/completions
     */
    private String baseUrl;

    /**
     * OpenAI 兼容 embedding 接口地址，例如：
     * https://api.openai.com/v1/embeddings
     */
    private String embeddingUrl;

    /**
     * 调用模型的 API Key。
     */
    private String apiKey;

    /**
     * 默认模型名称。
     */
    private String model = "gpt-4o-mini";

    /**
     * 默认 embedding 模型名称。
     */
    private String embeddingModel = "text-embedding-3-small";

    /**
     * 连接超时时间，单位毫秒。
     */
    private int connectTimeoutMs = 5000;

    /**
     * 读取超时时间，单位毫秒。
     */
    private int readTimeoutMs = 30000;

    /**
     * 默认检索条数。
     */
    private int ragTopK = 5;

    /**
     * RAG 相似度阈值。
     */
    private double ragScoreThreshold = 0.75d;
}
