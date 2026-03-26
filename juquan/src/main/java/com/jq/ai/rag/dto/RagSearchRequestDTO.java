package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检索请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagSearchRequestDTO {

    /**
     * 检索问题或查询文本。
     */
    private String query;

    /**
     * 返回条数。
     */
    private Integer topK;

    /**
     * 来源类型过滤。
     */
    private String sourceType;
}
