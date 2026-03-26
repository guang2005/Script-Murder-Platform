package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 检索命中的文档。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagSearchHitDTO {

    private String id;
    private String title;
    private String content;
    private List<String> tags;
    private String sourceType;
    private Map<String, Object> metadata;
    private double score;
}
