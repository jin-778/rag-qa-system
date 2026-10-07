# RAG 智能知识库问答系统

基于 SpringBoot + Spring AI + RAG 的智能问答系统。

## ✨ 功能

- 📄 多格式文档解析（PDF/Word/TXT）
- ✂️ 基于 Token 的滑动窗口切片（500 Token + 100 重叠）
- 🔍 向量语义检索（通义千问 Embedding）
- 🤖 AI 问答（DeepSeek Chat，带引用来源）
- 💾 Redis 缓存（响应从秒级降至毫秒级）
- 🛡️ 三层容错（超时 / 重试 / 降级）

## 🛠️ 技术栈

Java 17 / SpringBoot 3.3 / Spring AI 1.0 / Tika / Redis / DeepSeek / 通义千问

## 🚀 快速开始

```bash
# 1. 配置环境变量
export BAILIAN_API_KEY=sk-xxx
export DEEPSEEK_API_KEY=sk-xxx

# 2. 启动 Redis
redis-server

# 3. 启动应用
./mvnw spring-boot:run
