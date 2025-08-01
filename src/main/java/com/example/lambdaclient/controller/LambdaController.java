package com.example.lambdaclient.controller;

import com.example.lambdaclient.service.LambdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/lambda")
public class LambdaController {
    
    private final LambdaService lambdaService;
    
    @Autowired
    public LambdaController(LambdaService lambdaService) {
        this.lambdaService = lambdaService;
    }
    
    /**
     * Invoke Lambda function with JSON payload
     */
    @PostMapping("/invoke")
    public Mono<ResponseEntity<String>> invokeLambda(@RequestBody Map<String, Object> payload) {
        return lambdaService.invokeLambdaFunction(payload)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    /**
     * Invoke Lambda function with string payload
     */
    @PostMapping("/invoke-string")
    public Mono<ResponseEntity<String>> invokeLambdaString(@RequestBody String payload) {
        return lambdaService.invokeLambdaFunction(payload)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    /**
     * Invoke Lambda function with GET request
     */
    @GetMapping("/invoke")
    public Mono<ResponseEntity<String>> invokeLambdaGet() {
        return lambdaService.invokeLambdaFunctionGet()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Lambda Client"));
    }
}
