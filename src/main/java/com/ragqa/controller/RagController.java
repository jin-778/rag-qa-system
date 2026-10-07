package com.ragqa.controller;

import com.ragqa.common.Result;
import com.ragqa.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public Result<Map<String, Object>> ask(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        if (question == null || question.isBlank()) {
            return Result.error("问题不能为空");
        }
        RagService.RagAnswer answer = ragService.ask(question);
        Map<String, Object> data = new HashMap<>();
        data.put("answer", answer.answer());
        data.put("sources", answer.sources());
        data.put("degraded", answer.degraded());  // 告诉前端是否降级
        return Result.success(data);
    }
}