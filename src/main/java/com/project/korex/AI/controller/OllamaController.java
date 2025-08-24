package com.project.korex.AI.controller;

import com.project.korex.AI.dto.QueryRequest;
import com.project.korex.AI.service.OllamaService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/llm")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/query")
    public Mono<String> query(@RequestBody QueryRequest request) {
        return ollamaService.queryLLM(request.getModel_name(), request.getPrompt());
    }
}
