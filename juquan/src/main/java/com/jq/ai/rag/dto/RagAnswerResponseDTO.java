package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RAG 问答响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagAnswerResponseDTO {

    /**
     * 模型最终回答。
     */
    private String answer;

    /**
     * 召回到的来源。
     */
    private List<RagSearchHitDTO> sources;
}
