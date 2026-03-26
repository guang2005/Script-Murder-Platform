package com.jq.ai.service.impl;

import com.jq.ai.client.AiModelClient;
import com.jq.ai.dto.AiChatRequestDTO;
import com.jq.ai.dto.AiChatResponseDTO;
import com.jq.ai.service.AiInvokeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * AI 调用服务实现。
 */
@Service
public class AiInvokeServiceImpl implements AiInvokeService {

    private static final String DEFAULT_SYSTEM_PROMPT = "你是一个专业的内容推荐助手，请根据用户输入给出简洁、准确、可执行的建议。";

    @Resource
    private AiModelClient aiModelClient;

    @Override
    public AiChatResponseDTO chat(AiChatRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("AI 请求不能为空");
        }
        return aiModelClient.chat(request);
    }

    @Override
    public String chat(String userPrompt) {
        return chat(DEFAULT_SYSTEM_PROMPT, userPrompt);
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        if (userPrompt == null || userPrompt.isBlank()) {
            throw new IllegalArgumentException("用户输入不能为空");
        }
        String finalSystemPrompt = (systemPrompt == null || systemPrompt.isBlank())
                ? DEFAULT_SYSTEM_PROMPT
                : systemPrompt;
        AiChatResponseDTO response = chat(AiChatRequestDTO.of(finalSystemPrompt, userPrompt));
        return response.getContent();
    }
}
