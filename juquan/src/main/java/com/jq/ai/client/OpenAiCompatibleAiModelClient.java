package com.jq.ai.client;

import com.jq.ai.dto.AiChatMessageDTO;
import com.jq.ai.dto.AiChatRequestDTO;
import com.jq.ai.dto.AiChatResponseDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于 LangChain4j 的聊天模型客户端。
 */
@Component
public class OpenAiCompatibleAiModelClient implements AiModelClient {

    @Resource
    private ChatLanguageModel chatLanguageModel;

    @Override
    public AiChatResponseDTO chat(AiChatRequestDTO request) {
        if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) {
            throw new IllegalArgumentException("AI 请求消息不能为空");
        }

        String prompt = buildPrompt(request.getMessages());
        String content = chatLanguageModel.generate(prompt);

        Map<String, Object> rawResponse = new LinkedHashMap<>();
        rawResponse.put("provider", "langchain4j");
        rawResponse.put("prompt", prompt);
        rawResponse.put("model", request.getModel());

        return AiChatResponseDTO.builder()
                .content(content == null ? "" : content.trim())
                .rawResponse(rawResponse)
                .model(request.getModel())
                .finishReason("stop")
                .build();
    }

    private String buildPrompt(List<AiChatMessageDTO> messages) {
        return messages.stream()
                .map(message -> {
                    String role = message.getRole() == null ? "user" : message.getRole();
                    String content = message.getContent() == null ? "" : message.getContent();
                    return role.toUpperCase() + ": " + content;
                })
                .collect(Collectors.joining("\n\n"));
    }
}
