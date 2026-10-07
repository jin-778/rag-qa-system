package com.ragqa.service.impl;

import com.ragqa.service.DocumentService;
import com.ragqa.service.TextSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final TextSplitter textSplitter;

    /** Tika 是线程安全的，可以复用 */
    private final Tika tika = new Tika();

    @Override
    public List<String> parseAndSplit(byte[] fileBytes, String filename) {
        try {
            // 1. Tika 自动识别格式并抽取纯文本
            String text = tika.parseToString(new ByteArrayInputStream(fileBytes));
            log.info("解析文档 [{}] 成功，原始文本长度: {}", filename, text.length());

            // 2. Token 切片
            List<String> chunks = textSplitter.split(text);
            log.info("切片完成，共 {} 个分片", chunks.size());
            return chunks;

        } catch (Exception e) {
            log.error("解析文档 [{}] 失败", filename, e);
            throw new RuntimeException("文档解析失败: " + e.getMessage());
        }
    }
}