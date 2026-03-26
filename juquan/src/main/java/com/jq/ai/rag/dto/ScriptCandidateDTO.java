package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 剧本候选信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScriptCandidateDTO {

    private String id;
    private String name;
    private String summary;
    private Integer peopleCount;
    private Integer durationMinutes;
    private String genre;
    private String style;
    private List<String> tags;
}
