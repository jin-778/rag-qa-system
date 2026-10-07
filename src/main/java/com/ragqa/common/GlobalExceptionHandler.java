package com.ragqa.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 忽略静态资源 404（如 favicon.ico） */
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNotFound(NoResourceFoundException e) {
        // 什么都不做，避免刷 ERROR 日志
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleAll(Exception e) {
        log.error("系统异常", e);
        return Result.error("服务暂时不可用，请稍后再试");
    }
}