package com.example.lambdaclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LambdaConfig {
    private static final String CONFIG_FILE = "lambda-client.properties";
    private static LambdaConfig instance;
    
    private String functionUrl;
    private String region;
    private int timeoutSeconds;
    
    private LambdaConfig() {
        loadConfiguration();
    }
    
    public static LambdaConfig getInstance() {
        if (instance == null) {
            instance = new LambdaConfig();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        Properties props = new Properties();
        
        // Try to load from classpath first
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("Could not load configuration from classpath: " + e.getMessage());
        }
        
        // Try to load from current directory
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("./" + CONFIG_FILE)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // Ignore, use defaults or environment variables
        }
        
        // Load configuration with defaults
        this.functionUrl = props.getProperty("lambda.function.url", 
            System.getenv("LAMBDA_FUNCTION_URL"));
        this.region = props.getProperty("lambda.region", 
            System.getenv().getOrDefault("AWS_REGION", "us-east-1"));
        this.timeoutSeconds = Integer.parseInt(props.getProperty("lambda.timeout.seconds", "30"));
        
        // Validate required configuration
        if (this.functionUrl == null || this.functionUrl.trim().isEmpty()) {
            System.err.println("WARNING: Lambda function URL not configured!");
            System.err.println("Please set LAMBDA_FUNCTION_URL environment variable or create lambda-client.properties file");
            this.functionUrl = "https://your-lambda-function-url.lambda-url.us-east-1.on.aws/";
        }
    }
    
    public String getFunctionUrl() {
        return functionUrl;
    }
    
    public String getRegion() {
        return region;
    }
    
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    public void setFunctionUrl(String functionUrl) {
        this.functionUrl = functionUrl;
    }
}
