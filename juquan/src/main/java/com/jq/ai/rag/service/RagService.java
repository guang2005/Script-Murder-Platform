package com.jq.ai.rag.service;

import com.jq.ai.rag.dto.RagAnswerRequestDTO;
import com.jq.ai.rag.dto.RagAnswerResponseDTO;
import com.jq.ai.rag.dto.RagDocumentDTO;
import com.jq.ai.rag.dto.RagSearchHitDTO;
import com.jq.ai.rag.dto.RagSearchRequestDTO;

import java.util.List;

/**
 * RAG 服务。
 */
public interface RagService {

    void ingest(List<RagDocumentDTO> documents);

    List<RagSearchHitDTO> search(RagSearchRequestDTO request);

    RagAnswerResponseDTO answer(RagAnswerRequestDTO request);
}
