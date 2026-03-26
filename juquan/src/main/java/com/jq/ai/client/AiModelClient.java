package com.jq.ai.client;

import com.jq.ai.dto.AiChatRequestDTO;
import com.jq.ai.dto.AiChatResponseDTO;

/**
 * AI 模型访问抽象。
 */
public interface AiModelClient {

    /**
     * 调用对话模型。
     *
     * @param request 请求参数
     * @return 模型响应
     */
    AiChatResponseDTO chat(AiChatRequestDTO request);
}
