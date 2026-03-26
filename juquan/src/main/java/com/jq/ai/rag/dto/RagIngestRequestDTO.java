package com.jq.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量入库请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagIngestRequestDTO {

    /**
     * 需要入库的文档。
     */
    private List<RagDocumentDTO> documents;
}
