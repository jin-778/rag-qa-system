package com.ragqa.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // 基于内存的向量库，重启后数据丢失
        // 生产环境可替换为 RedisVectorStore / PgVectorStore
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}