package com.example.wchat.service;

import com.example.wchat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {
    private final WebClient webClient;

    public void askToBotServer(ChatMessage message) {
        log.info("봇 서버로 질문 전송: {}", message.getMessage());

        // 비동기 POST 요청 (타 서버의 /api/ask 엔드포인트 호출 가정)
        webClient.post()
                .uri("/api/ask")
                .bodyValue(message)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                    null, 
                    error -> log.error("봇 서버 통신 에러: {}", error.getMessage())
                );
    }
}
