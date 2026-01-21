package com.example.wchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // 봇 서버의 기본 URL을 설정할 수 있습니다.
    	// http://localhost:3000/api/ask
    	// http://bot-server-url.com").build();
        return builder.baseUrl("http://localhost:3000").build();
    }
}
