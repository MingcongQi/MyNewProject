package com.example.lambdaclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class LambdaClientConsoleApplication implements CommandLineRunner {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String LAMBDA_URL = "https://your-lambda-function-url.lambda-url.us-east-1.on.aws/";

    public LambdaClientConsoleApplication(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        // Disable web environment
        System.setProperty("spring.main.web-application-type", "none");
        SpringApplication.run(LambdaClientConsoleApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== AWS Lambda HTTP Client ===");
        System.out.println("This client sends HTTP requests to AWS Lambda");
        System.out.println("Lambda URL: " + LAMBDA_URL);
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Send GET request to Lambda");
            System.out.println("2. Send POST request with JSON data");
            System.out.println("3. Send custom message");
            System.out.println("4. Exit");
            System.out.print("Enter choice (1-4): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    sendGetRequest();
                    break;
                case "2":
                    sendPostRequest();
                    break;
                case "3":
                    sendCustomMessage(scanner);
                    break;
                case "4":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }

    private void sendGetRequest() {
        try {
            System.out.println("Sending GET request to Lambda...");
            ResponseEntity<String> response = restTemplate.getForEntity(LAMBDA_URL, String.class);
            
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending GET request: " + e.getMessage());
        }
    }

    private void sendPostRequest() {
        try {
            System.out.println("Sending POST request to Lambda...");
            
            // Create sample JSON data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("message", "Hello from Lambda Client!");
            requestData.put("timestamp", System.currentTimeMillis());
            requestData.put("source", "Console Client");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(LAMBDA_URL, request, String.class);
            
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending POST request: " + e.getMessage());
        }
    }

    private void sendCustomMessage(Scanner scanner) {
        try {
            System.out.print("Enter your message: ");
            String message = scanner.nextLine();
            
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("message", message);
            requestData.put("timestamp", System.currentTimeMillis());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
            
            System.out.println("Sending custom message to Lambda...");
            ResponseEntity<String> response = restTemplate.postForEntity(LAMBDA_URL, request, String.class);
            
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending custom message: " + e.getMessage());
        }
    }
}
