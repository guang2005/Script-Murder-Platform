package com.jq.ai.controller;

import com.jq.ai.rag.dto.ScriptRecommendRequestDTO;
import com.jq.ai.rag.dto.ScriptRecommendResultDTO;
import com.jq.ai.rag.service.ScriptRecommendService;
import com.jq.dto.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 剧本推荐入口。
 */
@RestController
@RequestMapping("/ai/script")
public class ScriptRecommendController {

    @Resource
    private ScriptRecommendService scriptRecommendService;

    @PostMapping("/recommend")
    public Result recommend(@RequestBody ScriptRecommendRequestDTO request) {
        ScriptRecommendResultDTO result = scriptRecommendService.recommend(request);
        return Result.ok(result);
    }
}
