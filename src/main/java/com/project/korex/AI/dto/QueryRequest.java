package com.project.korex.AI.dto;


import lombok.Data;

@Data
public class QueryRequest {
    private String model_name;
    private String prompt;

}
