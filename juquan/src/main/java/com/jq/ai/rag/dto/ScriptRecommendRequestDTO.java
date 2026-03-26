package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 剧本推荐请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScriptRecommendRequestDTO {

    /**
     * 用户偏好描述。
     */
    private String query;

    /**
     * 候选剧本列表。
     */
    private List<ScriptCandidateDTO> candidates;

    /**
     * 返回条数。
     */
    private Integer topK;
}
