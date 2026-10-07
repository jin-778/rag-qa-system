package com.ragqa.service;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 Token 的文本切片器
 * 策略：固定窗口 + 重叠，避免语义在边界处断裂
 */
@Component
public class TextSplitter {

    /** 每个分片的最大 Token 数 */
    private static final int CHUNK_SIZE = 500;

    /** 相邻分片的重叠 Token 数，防止语义断裂 */
    private static final int CHUNK_OVERLAP = 100;

    private final Encoding encoding = Encodings.newDefaultEncodingRegistry()
            .getEncoding(EncodingType.CL100K_BASE);

    /**
     * 将长文本切成若干 Token 分片
     */
    public List<String> split(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }

        IntArrayList tokens = encoding.encode(text);
        int total = tokens.size();

        int start = 0;
        while (start < total) {
            int end = Math.min(start + CHUNK_SIZE, total);
            // 构造一个新 IntArrayList，逐元素添加
            IntArrayList window = new IntArrayList();
            for (int i = start; i < end; i++) {
                window.add(tokens.get(i));
            }
            String chunk = encoding.decode(window);
            chunks.add(chunk);

            if (end == total) break;
            start = end - CHUNK_OVERLAP;
        }

        return chunks;
    }
}