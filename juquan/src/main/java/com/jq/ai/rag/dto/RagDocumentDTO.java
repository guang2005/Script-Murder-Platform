package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * RAG 文档。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagDocumentDTO {

    /**
     * 文档唯一标识。
     */
    private String id;

    /**
     * 文档标题。
     */
    private String title;

    /**
     * 文档正文，用于向量化。
     */
    private String content;

    /**
     * 标签。
     */
    private List<String> tags;

    /**
     * 来源类型，例如 script / faq / blog。
     */
    private String sourceType;

    /**
     * 额外元数据。
     */
    private Map<String, Object> metadata;

    /**
     * 向量。
     */
    private double[] embedding;
}
