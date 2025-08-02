package com.example.lambdaclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimpleLambdaClient {
    
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final LambdaConfig config = LambdaConfig.getInstance();

    public static void main(String[] args) {
        printWelcomeMessage();
        
        // Handle command line arguments
        if (args.length > 0) {
            handleCommandLineArgs(args);
            return;
        }
        
        // Interactive mode
        runInteractiveMode();
    }
    
    private static void printWelcomeMessage() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                 Lambda HTTP Client v2.0                     ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Pure Java client - No ports opened, No firewall issues     ║");
        System.out.println("║ Sends HTTP requests directly to AWS Lambda functions        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Lambda URL: " + config.getFunctionUrl());
        System.out.println("Region: " + config.getRegion());
        System.out.println("Timeout: " + config.getTimeoutSeconds() + " seconds");
        System.out.println();
    }
    
    private static void handleCommandLineArgs(String[] args) {
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "get":
                sendGetRequest();
                break;
            case "post":
                if (args.length > 1) {
                    sendPostRequestWithMessage(args[1]);
                } else {
                    sendPostRequest();
                }
                break;
            case "test":
                runHealthCheck();
                break;
            case "config":
                showConfiguration();
                break;
            case "help":
            case "--help":
            case "-h":
                showHelp();
                break;
            default:
                System.err.println("Unknown command: " + command);
                showHelp();
        }
    }
    
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            showMenu();
            System.out.print("Enter choice (1-7): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    runHealthCheck();
                    break;
                case "2":
                    sendGetRequest();
                    break;
                case "3":
                    sendPostRequest();
                    break;
                case "4":
                    sendCustomMessage(scanner);
                    break;
                case "5":
                    configureLambdaUrl(scanner);
                    break;
                case "6":
                    showConfiguration();
                    break;
                case "7":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            System.out.println();
        }
    }
    
    private static void showMenu() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                        MAIN MENU                           │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. Health Check (Test Connection)                          │");
        System.out.println("│ 2. Send GET request to Lambda                              │");
        System.out.println("│ 3. Send POST request with sample data                      │");
        System.out.println("│ 4. Send custom message                                     │");
        System.out.println("│ 5. Configure Lambda URL                                    │");
        System.out.println("│ 6. Show current configuration                              │");
        System.out.println("│ 7. Exit                                                    │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }
    
    private static void runHealthCheck() {
        System.out.println("🔍 Running health check...");
        
        try {
            long startTime = System.currentTimeMillis();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getFunctionUrl()))
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .header("User-Agent", "SimpleLambdaClient/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long responseTime = System.currentTimeMillis() - startTime;
            
            System.out.println("✅ Connection successful!");
            System.out.println("   Status: " + response.statusCode());
            System.out.println("   Response Time: " + responseTime + "ms");
            System.out.println("   Content Length: " + response.body().length() + " characters");
            
        } catch (Exception e) {
            System.err.println("❌ Health check failed: " + e.getMessage());
            System.err.println("   Please check your Lambda function URL and network connection");
        }
    }

    private static void sendGetRequest() {
        try {
            System.out.println("📤 Sending GET request to Lambda...");
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getFunctionUrl()))
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .header("User-Agent", "SimpleLambdaClient/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            printResponse(response);
        } catch (Exception e) {
            System.err.println("❌ Error sending GET request: " + e.getMessage());
        }
    }

    private static void sendPostRequest() {
        try {
            System.out.println("📤 Sending POST request to Lambda...");
            
            // Create sample JSON data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("message", "Hello from Simple Lambda Client!");
            requestData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            requestData.put("source", "SimpleLambdaClient");
            requestData.put("version", "1.0");

            sendPostRequestWithData(requestData);
        } catch (Exception e) {
            System.err.println("❌ Error sending POST request: " + e.getMessage());
        }
    }
    
    private static void sendPostRequestWithMessage(String message) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("message", message);
        requestData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        requestData.put("source", "SimpleLambdaClient-CLI");
        
        sendPostRequestWithData(requestData);
    }

    private static void sendCustomMessage(Scanner scanner) {
        try {
            System.out.print("Enter your message: ");
            String message = scanner.nextLine();
            
            if (message.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
                return;
            }
            
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("message", message);
            requestData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            requestData.put("source", "SimpleLambdaClient-Interactive");

            sendPostRequestWithData(requestData);
        } catch (Exception e) {
            System.err.println("❌ Error sending custom message: " + e.getMessage());
        }
    }
    
    private static void sendPostRequestWithData(Map<String, Object> requestData) {
        try {
            String jsonBody = objectMapper.writeValueAsString(requestData);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getFunctionUrl()))
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "SimpleLambdaClient/1.0")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            System.out.println("📋 Request payload: " + jsonBody);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            printResponse(response);
        } catch (Exception e) {
            System.err.println("❌ Error sending POST request: " + e.getMessage());
        }
    }
    
    private static void printResponse(HttpResponse<String> response) {
        System.out.println("📥 Response received:");
        System.out.println("   Status: " + response.statusCode() + " " + getStatusText(response.statusCode()));
        System.out.println("   Headers: " + response.headers().map());
        System.out.println("   Body: " + formatJsonResponse(response.body()));
    }
    
    private static String getStatusText(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            default -> "Unknown";
        };
    }
    
    private static String formatJsonResponse(String response) {
        try {
            Object json = objectMapper.readValue(response, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return response; // Return as-is if not valid JSON
        }
    }
    
    private static void configureLambdaUrl(Scanner scanner) {
        System.out.println("Current Lambda URL: " + config.getFunctionUrl());
        System.out.print("Enter new Lambda Function URL (or press Enter to keep current): ");
        
        String newUrl = scanner.nextLine().trim();
        if (!newUrl.isEmpty()) {
            config.setFunctionUrl(newUrl);
            System.out.println("✅ Lambda URL updated to: " + newUrl);
        } else {
            System.out.println("Lambda URL unchanged.");
        }
    }
    
    private static void showConfiguration() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                    CURRENT CONFIGURATION                   │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Lambda URL: " + String.format("%-44s", config.getFunctionUrl()) + "│");
        System.out.println("│ Region: " + String.format("%-48s", config.getRegion()) + "│");
        System.out.println("│ Timeout: " + String.format("%-47s", config.getTimeoutSeconds() + " seconds") + "│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }
    
    private static void showHelp() {
        System.out.println("Usage: java -jar lambda-client.jar [command] [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  get                    Send GET request to Lambda");
        System.out.println("  post [message]         Send POST request (with optional message)");
        System.out.println("  test                   Run health check");
        System.out.println("  config                 Show current configuration");
        System.out.println("  help                   Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar lambda-client.jar test");
        System.out.println("  java -jar lambda-client.jar post \"Hello Lambda!\"");
        System.out.println("  java -jar lambda-client.jar get");
        System.out.println();
        System.out.println("Configuration:");
        System.out.println("  Set LAMBDA_FUNCTION_URL environment variable");
        System.out.println("  Or create lambda-client.properties file with:");
        System.out.println("    lambda.function.url=https://your-url.lambda-url.region.on.aws/");
        System.out.println("    lambda.region=us-east-1");
        System.out.println("    lambda.timeout.seconds=30");
    }
}
