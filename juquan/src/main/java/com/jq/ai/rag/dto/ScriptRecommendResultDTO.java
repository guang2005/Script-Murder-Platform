package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 剧本推荐结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScriptRecommendResultDTO {

    /**
     * AI 推荐总结。
     */
    private String summary;

    /**
     * 推荐命中结果。
     */
    private List<RagSearchHitDTO> recommendations;
}
