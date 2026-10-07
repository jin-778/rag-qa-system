package com.ragqa.controller;

import com.ragqa.common.Result;
import com.ragqa.service.VectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final VectorService vectorService;

    @GetMapping
    public Result<List<Map<String, Object>>> search(@RequestParam("query") String query) {
        List<Document> docs = vectorService.search(query, 5);
        List<Map<String, Object>> results = docs.stream()
                .map(doc -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("content", doc.getText());
                    m.put("source", doc.getMetadata().getOrDefault("source", "未知"));
                    return m;
                })
                .collect(Collectors.toList());
        return Result.success(results);
    }
}