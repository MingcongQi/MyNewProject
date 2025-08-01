package com.example.lambdaclient.service;

import com.example.lambdaclient.config.LambdaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class LambdaService {
    
    private static final Logger logger = LoggerFactory.getLogger(LambdaService.class);
    
    private final WebClient webClient;
    private final LambdaConfig lambdaConfig;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public LambdaService(LambdaConfig lambdaConfig, ObjectMapper objectMapper) {
        this.lambdaConfig = lambdaConfig;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    /**
     * Invoke Lambda function via Function URL with JSON payload
     */
    public Mono<String> invokeLambdaFunction(Map<String, Object> payload) {
        logger.info("Invoking Lambda function at: {}", lambdaConfig.getFunctionUrl());
        
        return webClient.post()
                .uri(lambdaConfig.getFunctionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(lambdaConfig.getTimeoutSeconds()))
                .doOnSuccess(response -> logger.info("Lambda function invoked successfully"))
                .doOnError(error -> logger.error("Error invoking Lambda function: {}", error.getMessage()));
    }
    
    /**
     * Invoke Lambda function with string payload
     */
    public Mono<String> invokeLambdaFunction(String payload) {
        logger.info("Invoking Lambda function with string payload");
        
        return webClient.post()
                .uri(lambdaConfig.getFunctionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(lambdaConfig.getTimeoutSeconds()))
                .doOnSuccess(response -> logger.info("Lambda function invoked successfully"))
                .doOnError(error -> logger.error("Error invoking Lambda function: {}", error.getMessage()));
    }
    
    /**
     * Invoke Lambda function with GET request
     */
    public Mono<String> invokeLambdaFunctionGet() {
        logger.info("Invoking Lambda function with GET request");
        
        return webClient.get()
                .uri(lambdaConfig.getFunctionUrl())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(lambdaConfig.getTimeoutSeconds()))
                .doOnSuccess(response -> logger.info("Lambda function invoked successfully"))
                .doOnError(error -> logger.error("Error invoking Lambda function: {}", error.getMessage()));
    }
}
