package com.jq.ai.rag.service.impl;

import com.jq.ai.client.AiModelClient;
import com.jq.ai.dto.AiChatRequestDTO;
import com.jq.ai.rag.dto.RagAnswerRequestDTO;
import com.jq.ai.rag.dto.RagAnswerResponseDTO;
import com.jq.ai.rag.dto.RagDocumentDTO;
import com.jq.ai.rag.dto.RagSearchHitDTO;
import com.jq.ai.rag.dto.RagSearchRequestDTO;
import com.jq.ai.rag.service.RagService;
import com.jq.ai.rag.store.VectorStore;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG 服务实现。
 */
@Service
public class RagServiceImpl implements RagService {

    private static final String DEFAULT_SYSTEM_PROMPT =
            "你是一个基于知识库回答问题的助手。请优先根据给定资料回答，回答要简洁、准确，并指出匹配理由。";

    @Resource
    private VectorStore vectorStore;

    @Resource
    private AiModelClient aiModelClient;

    @Override
    public void ingest(List<RagDocumentDTO> documents) {
        vectorStore.upsertBatch(documents);
    }

    @Override
    public List<RagSearchHitDTO> search(RagSearchRequestDTO request) {
        return vectorStore.search(request);
    }

    @Override
    public RagAnswerResponseDTO answer(RagAnswerRequestDTO request) {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()) {
            throw new IllegalArgumentException("问题不能为空");
        }
        List<RagSearchHitDTO> hits = search(RagSearchRequestDTO.builder()
                .query(request.getQuery())
                .topK(request.getTopK())
                .build());

        String context = hits.stream()
                .map(hit -> String.format(
                        "标题：%s%n内容：%s%n标签：%s%n相似度：%.4f",
                        hit.getTitle(),
                        hit.getContent(),
                        hit.getTags() == null ? "" : String.join(",", hit.getTags()),
                        hit.getScore()
                ))
                .collect(Collectors.joining("\n\n"));

        String userPrompt = """
                请基于以下知识库内容回答问题。

                问题：
                %s

                知识库内容：
                %s

                回答要求：
                1. 优先使用知识库中的内容。
                2. 如果资料不足，请明确说明不足之处。
                3. 输出简洁、结构清晰。
                """.formatted(request.getQuery(), context.isBlank() ? "无召回结果" : context);

        String answer = aiModelClient.chat(AiChatRequestDTO.of(DEFAULT_SYSTEM_PROMPT, userPrompt)).getContent();
        return RagAnswerResponseDTO.builder()
                .answer(answer)
                .sources(hits)
                .build();
    }
}
