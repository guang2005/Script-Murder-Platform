package com.jq.ai.controller;

import com.jq.ai.rag.dto.RagAnswerRequestDTO;
import com.jq.ai.rag.dto.RagAnswerResponseDTO;
import com.jq.ai.rag.dto.RagDocumentDTO;
import com.jq.ai.rag.dto.RagIngestRequestDTO;
import com.jq.ai.rag.dto.RagSearchHitDTO;
import com.jq.ai.rag.dto.RagSearchRequestDTO;
import com.jq.ai.rag.service.RagService;
import com.jq.dto.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用 RAG 入口。
 */
@RestController
@RequestMapping("/ai/rag")
public class RagController {

    @Resource
    private RagService ragService;

    @PostMapping("/ingest")
    public Result ingest(@RequestBody RagIngestRequestDTO request) {
        List<RagDocumentDTO> documents = request == null ? null : request.getDocuments();
        ragService.ingest(documents);
        return Result.ok();
    }

    @PostMapping("/search")
    public Result search(@RequestBody RagSearchRequestDTO request) {
        List<RagSearchHitDTO> hits = ragService.search(request);
        return Result.ok(hits);
    }

    @PostMapping("/answer")
    public Result answer(@RequestBody RagAnswerRequestDTO request) {
        RagAnswerResponseDTO response = ragService.answer(request);
        return Result.ok(response);
    }
}
