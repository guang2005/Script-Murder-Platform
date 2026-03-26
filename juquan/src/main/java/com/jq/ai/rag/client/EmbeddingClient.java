package com.jq.ai.rag.client;

/**
 * 向量化客户端。
 */
public interface EmbeddingClient {

    /**
     * 将文本转换为向量。
     *
     * @param text 文本
     * @return 向量
     */
    double[] embed(String text);
}
