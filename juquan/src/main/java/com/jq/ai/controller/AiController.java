package com.jq.ai.controller;

import com.jq.ai.dto.AiChatRequestDTO;
import com.jq.ai.dto.AiChatResponseDTO;
import com.jq.ai.service.AiInvokeService;
import com.jq.dto.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 调用入口。
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AiInvokeService aiInvokeService;

    /**
     * 直接调用模型，适合调试和后续外部业务复用。
     */
    @PostMapping("/chat")
    public Result chat(@RequestBody AiChatRequestDTO request) {
        AiChatResponseDTO response = aiInvokeService.chat(request);
        return Result.ok(response);
    }

    /**
     * 用系统提示词 + 用户输入的简化调用方式。
     */
    @PostMapping("/chat/text")
    public Result chatText(@RequestBody ChatTextRequest request) {
        String content = aiInvokeService.chat(request.getSystemPrompt(), request.getUserPrompt());
        return Result.ok(content);
    }

    public static class ChatTextRequest {
        private String systemPrompt;
        private String userPrompt;

        public String getSystemPrompt() {
            return systemPrompt;
        }

        public void setSystemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
        }

        public String getUserPrompt() {
            return userPrompt;
        }

        public void setUserPrompt(String userPrompt) {
            this.userPrompt = userPrompt;
        }
    }
}
