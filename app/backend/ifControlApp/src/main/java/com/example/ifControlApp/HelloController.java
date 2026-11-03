package com.example.ifControlApp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    // 文字列を返すエンドポイント
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot 3!";
    }

    // JSONオブジェクトを返すエンドポイント
    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
            "status", "UP",
            "message", "Service is running"
        );
    }
}