package com.car.gatewayservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway/fallback")
public class FallbackController {

    @GetMapping
    public ResponseEntity<String> myFallback(){
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("系统繁忙。请稍后重试");
    }

}
