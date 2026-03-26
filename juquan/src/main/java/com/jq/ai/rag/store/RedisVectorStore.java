package com.jq.ai.rag.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jq.ai.config.AiProperties;
import com.jq.ai.rag.client.EmbeddingClient;
import com.jq.ai.rag.dto.RagDocumentDTO;
import com.jq.ai.rag.dto.RagSearchHitDTO;
import com.jq.ai.rag.dto.RagSearchRequestDTO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 Redis 的向量存储。
 */
@Component
public class RedisVectorStore implements VectorStore {

    private static final String DOC_KEY_PREFIX = "jq:rag:doc:";
    private static final String DOC_INDEX_KEY = "jq:rag:doc:index";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private EmbeddingClient embeddingClient;

    @Resource
    private AiProperties aiProperties;

    @Override
    public void upsert(RagDocumentDTO document) {
        if (document == null) {
            throw new IllegalArgumentException("文档不能为空");
        }
        if (document.getId() == null || document.getId().isBlank()) {
            throw new IllegalArgumentException("文档 id 不能为空");
        }
        String content = buildEmbeddingText(document);
        double[] embedding = embeddingClient.embed(content);
        document.setEmbedding(embedding);
        writeDocument(document);
    }

    @Override
    public void upsertBatch(List<RagDocumentDTO> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        for (RagDocumentDTO document : documents) {
            upsert(document);
        }
    }

    @Override
    public List<RagSearchHitDTO> search(RagSearchRequestDTO request) {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()) {
            throw new IllegalArgumentException("检索文本不能为空");
        }
        double[] queryVector = embeddingClient.embed(request.getQuery());
        Integer requestedTopK = request.getTopK();
        int topK = requestedTopK == null || requestedTopK <= 0 ? aiProperties.getRagTopK() : requestedTopK;

        Set<String> ids = stringRedisTemplate.opsForSet().members(DOC_INDEX_KEY);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<RagDocumentDTO> docs = ids.stream()
                .map(this::readDocument)
                .filter(Objects::nonNull)
                .filter(doc -> request.getSourceType() == null
                        || request.getSourceType().isBlank()
                        || request.getSourceType().equals(doc.getSourceType()))
                .collect(Collectors.toList());

        return docs.stream()
                .map(doc -> toHit(doc, cosineSimilarity(queryVector, doc.getEmbedding())))
                .filter(hit -> hit.getScore() >= aiProperties.getRagScoreThreshold())
                .sorted(Comparator.comparingDouble(RagSearchHitDTO::getScore).reversed())
                .limit(topK)
                .collect(Collectors.toList());
    }

    private void writeDocument(RagDocumentDTO document) {
        try {
            stringRedisTemplate.opsForValue().set(DOC_KEY_PREFIX + document.getId(), objectMapper.writeValueAsString(document));
            stringRedisTemplate.opsForSet().add(DOC_INDEX_KEY, document.getId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("RAG 文档序列化失败", e);
        }
    }

    private RagDocumentDTO readDocument(String id) {
        String raw = stringRedisTemplate.opsForValue().get(DOC_KEY_PREFIX + id);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(raw, RagDocumentDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("RAG 文档反序列化失败", e);
        }
    }

    private RagSearchHitDTO toHit(RagDocumentDTO document, double score) {
        return RagSearchHitDTO.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .tags(document.getTags())
                .sourceType(document.getSourceType())
                .metadata(document.getMetadata())
                .score(score)
                .build();
    }

    private String buildEmbeddingText(RagDocumentDTO document) {
        StringBuilder sb = new StringBuilder();
        if (document.getTitle() != null) {
            sb.append(document.getTitle()).append('\n');
        }
        if (document.getTags() != null && !document.getTags().isEmpty()) {
            sb.append(String.join(" ", document.getTags())).append('\n');
        }
        if (document.getContent() != null) {
            sb.append(document.getContent());
        }
        return sb.toString().trim();
    }

    private double cosineSimilarity(double[] left, double[] right) {
        if (left == null || right == null || left.length == 0 || right.length == 0) {
            return 0.0d;
        }
        int len = Math.min(left.length, right.length);
        double dot = 0.0d;
        double leftNorm = 0.0d;
        double rightNorm = 0.0d;
        for (int i = 0; i < len; i++) {
            dot += left[i] * right[i];
            leftNorm += left[i] * left[i];
            rightNorm += right[i] * right[i];
        }
        if (leftNorm == 0.0d || rightNorm == 0.0d) {
            return 0.0d;
        }
        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }
}
