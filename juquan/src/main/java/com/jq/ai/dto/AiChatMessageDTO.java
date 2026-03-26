package com.jq.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat 消息结构。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiChatMessageDTO {

    /**
     * 消息角色：system / user / assistant。
     */
    private String role;

    /**
     * 消息内容。
     */
    private String content;

    public static AiChatMessageDTO system(String content) {
        return AiChatMessageDTO.builder().role("system").content(content).build();
    }

    public static AiChatMessageDTO user(String content) {
        return AiChatMessageDTO.builder().role("user").content(content).build();
    }

    public static AiChatMessageDTO assistant(String content) {
        return AiChatMessageDTO.builder().role("assistant").content(content).build();
    }
}
