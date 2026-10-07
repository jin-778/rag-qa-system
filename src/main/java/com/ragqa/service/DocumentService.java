package com.ragqa.service;

import java.util.List;

public interface DocumentService {
    /**
     * 解析上传的文档，返回切片后的文本列表
     */
    List<String> parseAndSplit(byte[] fileBytes, String filename);
}