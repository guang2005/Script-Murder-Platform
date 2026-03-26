package com.jq.ai.rag.service.impl;

import com.jq.ai.client.AiModelClient;
import com.jq.ai.dto.AiChatRequestDTO;
import com.jq.ai.rag.client.EmbeddingClient;
import com.jq.ai.rag.dto.RagSearchHitDTO;
import com.jq.ai.rag.dto.ScriptCandidateDTO;
import com.jq.ai.rag.dto.ScriptRecommendRequestDTO;
import com.jq.ai.rag.dto.ScriptRecommendResultDTO;
import com.jq.ai.rag.service.ScriptRecommendService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 剧本推荐服务实现。
 */
@Service
public class ScriptRecommendServiceImpl implements ScriptRecommendService {

    @Resource
    private EmbeddingClient embeddingClient;

    @Resource
    private AiModelClient aiModelClient;

    @Override
    public ScriptRecommendResultDTO recommend(ScriptRecommendRequestDTO request) {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()) {
            throw new IllegalArgumentException("推荐条件不能为空");
        }
        if (request.getCandidates() == null || request.getCandidates().isEmpty()) {
            throw new IllegalArgumentException("候选剧本不能为空");
        }

        double[] queryVector = embeddingClient.embed(request.getQuery());
        int topK = request.getTopK() == null || request.getTopK() <= 0 ? 5 : request.getTopK();

        List<RagSearchHitDTO> hits = request.getCandidates().stream()
                .map(candidate -> toHit(candidate, similarity(queryVector, embeddingClient.embed(buildScriptText(candidate)))))
                .sorted(Comparator.comparingDouble(RagSearchHitDTO::getScore).reversed())
                .limit(topK)
                .collect(Collectors.toList());

        String prompt = buildPrompt(request.getQuery(), hits);
        String summary = aiModelClient.chat(AiChatRequestDTO.of(
                "你是一个专业的剧本推荐助手。请根据给定的候选剧本和用户偏好，输出简洁的推荐结论。",
                prompt
        )).getContent();

        return ScriptRecommendResultDTO.builder()
                .summary(summary)
                .recommendations(hits)
                .build();
    }

    private RagSearchHitDTO toHit(ScriptCandidateDTO candidate, double score) {
        return RagSearchHitDTO.builder()
                .id(candidate.getId())
                .title(candidate.getName())
                .content(candidate.getSummary())
                .tags(candidate.getTags())
                .sourceType("script")
                .metadata(java.util.Map.of(
                        "peopleCount", candidate.getPeopleCount(),
                        "durationMinutes", candidate.getDurationMinutes(),
                        "genre", candidate.getGenre(),
                        "style", candidate.getStyle()
                ))
                .score(score)
                .build();
    }

    private String buildPrompt(String query, List<RagSearchHitDTO> hits) {
        String candidates = hits.stream()
                .map(hit -> String.format(
                        "剧本：%s%n简介：%s%n标签：%s%n人数：%s%n时长：%s%n相似度：%.4f",
                        hit.getTitle(),
                        hit.getContent(),
                        hit.getTags() == null ? "" : String.join(",", hit.getTags()),
                        hit.getMetadata() == null ? "" : hit.getMetadata().getOrDefault("peopleCount", ""),
                        hit.getMetadata() == null ? "" : hit.getMetadata().getOrDefault("durationMinutes", ""),
                        hit.getScore()
                ))
                .collect(Collectors.joining("\n\n"));

        return """
                用户偏好：
                %s

                候选剧本：
                %s

                请输出：
                1. 最推荐的剧本排序。
                2. 每个剧本的简短推荐理由。
                3. 如果存在不适合的项，也请说明原因。
                """.formatted(query, candidates);
    }

    private String buildScriptText(ScriptCandidateDTO candidate) {
        StringBuilder sb = new StringBuilder();
        if (candidate.getName() != null) {
            sb.append(candidate.getName()).append('\n');
        }
        if (candidate.getTags() != null && !candidate.getTags().isEmpty()) {
            sb.append(String.join(" ", candidate.getTags())).append('\n');
        }
        if (candidate.getGenre() != null) {
            sb.append(candidate.getGenre()).append('\n');
        }
        if (candidate.getStyle() != null) {
            sb.append(candidate.getStyle()).append('\n');
        }
        if (candidate.getSummary() != null) {
            sb.append(candidate.getSummary());
        }
        return sb.toString().trim();
    }

    private double similarity(double[] left, double[] right) {
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
