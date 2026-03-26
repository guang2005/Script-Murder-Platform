package com.jq.ai.rag.service;

import com.jq.ai.rag.dto.ScriptRecommendRequestDTO;
import com.jq.ai.rag.dto.ScriptRecommendResultDTO;

/**
 * 剧本推荐服务。
 */
public interface ScriptRecommendService {

    ScriptRecommendResultDTO recommend(ScriptRecommendRequestDTO request);
}
