package com.example.lambdaclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.lambda")
public class LambdaConfig {
    
    private String functionUrl;
    private String region = "us-east-1";
    private int timeoutSeconds = 30;
    
    // Getters and Setters
    public String getFunctionUrl() {
        return functionUrl;
    }
    
    public void setFunctionUrl(String functionUrl) {
        this.functionUrl = functionUrl;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
