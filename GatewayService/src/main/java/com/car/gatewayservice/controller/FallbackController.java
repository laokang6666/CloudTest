package com.car.gatewayservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 熔断回退：须兼容网关转发时携带的原始 HTTP 方法（GET/POST 等），否则会出现 405。
 */
@RestController
@RequestMapping("/gateway/fallback")
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    /** 不限制 method，避免 default-filters 下任意路由（如 POST /api/borrows）回退时方法不匹配 */
    @RequestMapping
    public ResponseEntity<String> myFallback() {
        log.info("回退时间:{}",new Date());
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("系统繁忙。请稍后重试");
    }
}
