package com.project.korex.AI.controller;

import com.project.korex.AI.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping(value = "/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Response> getResponse(@RequestBody Request request) {
        return chatbotService.getChatbotResponse(request.getPrompt())
                .map(Response::new);
    }

    // 요청 DTO
    public static class Request {
        private String prompt;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }

    // 응답 DTO
    public static class Response {
        private String response;

        public Response(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }
}
