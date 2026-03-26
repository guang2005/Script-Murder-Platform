package com.jq.ai.service;

import com.jq.ai.dto.AiChatRequestDTO;
import com.jq.ai.dto.AiChatResponseDTO;

/**
 * AI 调用服务。
 */
public interface AiInvokeService {

    /**
     * 调用模型。
     *
     * @param request 请求参数
     * @return 返回结果
     */
    AiChatResponseDTO chat(AiChatRequestDTO request);

    /**
     * 使用默认 system prompt 调用模型。
     *
     * @param userPrompt 用户输入
     * @return 模型回复文本
     */
    String chat(String userPrompt);

    /**
     * 自定义 system prompt 调用模型。
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户输入
     * @return 模型回复文本
     */
    String chat(String systemPrompt, String userPrompt);
}
