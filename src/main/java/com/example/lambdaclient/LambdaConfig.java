package com.example.lambdaclient;

/**
 * Configuration class for Lambda client settings
 */
public class LambdaConfig {
    private static LambdaConfig instance;
    private String functionUrl = "https://default-lambda-url.lambda-url.us-east-1.on.aws/";
    private String region = "us-east-1";
    private int timeoutSeconds = 30;
    
    private LambdaConfig() {
        // Private constructor for singleton pattern
    }
    
    public static synchronized LambdaConfig getInstance() {
        if (instance == null) {
            instance = new LambdaConfig();
        }
        return instance;
    }
    
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
