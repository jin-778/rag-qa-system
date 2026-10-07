package com.ragqa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RagService {

    private final VectorService vectorService;
    private final CacheService cacheService;
    private final ChatClient chatClient;
    private final String promptTemplate;

    public RagService(VectorService vectorService,
                      CacheService cacheService,
                      ChatClient.Builder chatClientBuilder) throws Exception {
        this.vectorService = vectorService;
        this.cacheService = cacheService;
        this.chatClient = chatClientBuilder.build();
        ClassPathResource resource = new ClassPathResource("prompt-template.st");
        this.promptTemplate = new String(resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    public RagAnswer ask(String question) {
        // 1. 先查缓存
        String cached = cacheService.get(question);
        if (cached != null) {
            return new RagAnswer(cached, List.of("缓存命中"), false);
        }

        // 2. 检索
        List<Document> docs;
        try {
            docs = vectorService.search(question, 5);
        } catch (Exception e) {
            log.error("向量检索失败", e);
            return new RagAnswer("知识库检索服务暂时不可用，请稍后再试。",
                    List.of(), true);
        }

        // 3. 如果检索结果为空，直接返回，不调大模型（节省成本）
        if (docs.isEmpty()) {
            return new RagAnswer("知识库中未找到与您问题相关的内容。",
                    List.of(), false);
        }

        // 4. 拼 Prompt
        String context = docs.stream()
                .map(doc -> "【来源：" + doc.getMetadata().getOrDefault("source", "未知") + "】\n"
                        + doc.getText())
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = promptTemplate
                .replace("{context}", context)
                .replace("{question}", question);

        List<String> sources = docs.stream()
                .map(doc -> (String) doc.getMetadata().getOrDefault("source", "未知"))
                .distinct()
                .collect(Collectors.toList());

        // 5. 调用大模型，失败时降级
        try {
            String answer = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            cacheService.put(question, answer);
            return new RagAnswer(answer, sources, false);
        } catch (Exception e) {
            log.error("大模型调用失败，触发降级", e);
            // 降级：返回检索到的片段，让用户看到原始内容
            String fallback = "AI 服务暂时不可用，以下是知识库中检索到的相关内容：\n\n" + context;
            return new RagAnswer(fallback, sources, true);
        }
    }

    /** degraded=true 表示走了降级分支 */
    public record RagAnswer(String answer, List<String> sources, boolean degraded) {}
}