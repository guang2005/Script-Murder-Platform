package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG 问答请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagAnswerRequestDTO {

    /**
     * 用户问题。
     */
    private String query;

    /**
     * 检索条数。
     */
    private Integer topK;
}
