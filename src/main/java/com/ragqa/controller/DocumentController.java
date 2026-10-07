package com.ragqa.controller;

import com.ragqa.common.Result;
import com.ragqa.service.CacheService;
import com.ragqa.service.DocumentService;
import com.ragqa.service.VectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final VectorService vectorService;
    private final CacheService cacheService;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        try {
            String filename = file.getOriginalFilename();
            List<String> chunks = documentService.parseAndSplit(file.getBytes(), filename);
            vectorService.addChunks(chunks, filename);
            // 上传新文档后，清空旧缓存，避免答案过期
            cacheService.clearAll();
            return Result.success("入库成功，共 " + chunks.size() + " 个分片");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}