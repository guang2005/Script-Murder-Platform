package com.jq.ai.rag.store;

import com.jq.ai.rag.dto.RagDocumentDTO;
import com.jq.ai.rag.dto.RagSearchRequestDTO;
import com.jq.ai.rag.dto.RagSearchHitDTO;

import java.util.List;

/**
 * 向量存储抽象。
 */
public interface VectorStore {

    /**
     * 保存或更新文档。
     */
    void upsert(RagDocumentDTO document);

    /**
     * 批量保存或更新文档。
     */
    void upsertBatch(List<RagDocumentDTO> documents);

    /**
     * 按语义检索。
     */
    List<RagSearchHitDTO> search(RagSearchRequestDTO request);
}
