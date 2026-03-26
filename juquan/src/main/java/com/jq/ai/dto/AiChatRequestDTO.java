package com.jq.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Chat 请求参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiChatRequestDTO {

    /**
     * 模型名称，不传则使用配置默认值。
     */
    private String model;

    /**
     * 聊天消息列表。
     */
    private List<AiChatMessageDTO> messages;

    /**
     * 温度。
     */
    private Double temperature;

    /**
     * Top-p。
     */
    @JsonProperty("top_p")
    private Double topP;

    /**
     * 最大输出 token 数。
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 是否流式输出。
     */
    private Boolean stream;

    public static AiChatRequestDTO of(String systemPrompt, String userPrompt) {
        return AiChatRequestDTO.builder()
                .messages(List.of(
                        AiChatMessageDTO.system(systemPrompt),
                        AiChatMessageDTO.user(userPrompt)
                ))
                .stream(false)
                .build();
    }
}
