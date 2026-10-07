package com.ragqa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    /** 缓存 TTL：1 小时 */
    private static final Duration TTL = Duration.ofHours(1);

    /** 缓存 Key 前缀 */
    private static final String PREFIX = "rag:qa:";

    /**
     * 把问题哈希成 Key，避免问题太长或带特殊字符
     */
    private String hashKey(String question) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(question.getBytes(StandardCharsets.UTF_8));
            return PREFIX + HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("Hash 计算失败", e);
        }
    }

    /** 读缓存，命中返回答案，未命中返回 null */
    public String get(String question) {
        return redisTemplate.opsForValue().get(hashKey(question));
    }

    /** 写缓存 */
    public void put(String question, String answer) {
        redisTemplate.opsForValue().set(hashKey(question), answer, TTL);
    }

    /**
     * 上传新文档时清空所有 RAG 缓存
     * 避免用户问同样问题拿到旧文档的答案
     */
    public void clearAll() {
        var keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}