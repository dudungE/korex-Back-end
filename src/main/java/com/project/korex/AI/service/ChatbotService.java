package com.project.korex.AI.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ChatbotService {

    private final WebClient webClient;

    public ChatbotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8001").build();
    }

    public Mono<String> getChatbotResponse(String prompt) {
        return webClient.post()
                .uri("/api/chatbot")
                .bodyValue(Map.of("prompt", prompt))
                .retrieve()
                .bodyToMono(Map.class)
                .map(responseMap -> (String) responseMap.get("response"));
    }
}
