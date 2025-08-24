package com.project.korex.AI.service;

import com.project.korex.AI.dto.QueryRequest;
import com.project.korex.AI.dto.QueryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OllamaService {

    private final WebClient webClient;

    public OllamaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8000").build();
    }

    public Mono<String> queryLLM(String modelName, String prompt) {
        QueryRequest request = new QueryRequest();
        request.setModel_name(modelName);
        request.setPrompt(prompt);

        return webClient.post()
                .uri("/query")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(QueryResponse.class)
                .map(QueryResponse::getResponse);
    }
}
