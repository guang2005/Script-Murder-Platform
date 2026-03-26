package com.jq.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * AI 返回结果的统一封装。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponseDTO {

    /**
     * 模型最终回复文本。
     */
    private String content;

    /**
     * 原始返回，方便排查问题。
     */
    private Map<String, Object> rawResponse;

    /**
     * 模型名称。
     */
    private String model;

    /**
     * 结束原因。
     */
    private String finishReason;
}
