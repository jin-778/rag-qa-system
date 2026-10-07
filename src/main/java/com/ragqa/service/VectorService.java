package com.ragqa.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VectorService {

    private final VectorStore vectorStore;

    public VectorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 将文本切片向量化后存入向量库
     */
    public void addChunks(List<String> chunks, String filename) {
        List<Document> documents = chunks.stream()
                .map(chunk -> new Document(chunk, Map.of("source", filename)))
                .toList();
        vectorStore.add(documents);
    }

    /**
     * 根据问题检索最相关的文档片段
     */
    public List<Document> search(String query, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();
        return vectorStore.similaritySearch(request);
    }
}