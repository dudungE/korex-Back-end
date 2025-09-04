package com.project.korex.auth.controller;

import com.project.korex.auth.dto.request.ImageParsingRequest;
import com.project.korex.auth.dto.response.OcrData;
import com.project.korex.auth.service.ClovaOcrService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
@AllArgsConstructor
public class ClovaOcrController {

    private final ClovaOcrService clovaOcrService;

    // 테스트용
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody ImageParsingRequest request) {
        try {
            // ClovaOCR 서비스 호출
            String ocrResult = ClovaOcrService.execute(request);

            OcrData data = clovaOcrService.parseOcrResult(ocrResult);

            // 결과 반환
            Map<String, String> response = new HashMap<>();
            response.put("ocrResult", ocrResult);
            response.put("name", data.getName());
            response.put("birth", data.getBirth());
            response.put("rrn", data.getRrn());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
