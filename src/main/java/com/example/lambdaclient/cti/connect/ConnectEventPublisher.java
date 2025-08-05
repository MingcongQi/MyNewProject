package com.example.lambdaclient.cti.connect;

import com.example.lambdaclient.cti.CTIMonitorConfig.ConnectConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Connect Event Publisher
 * 
 * This class publishes CTI events to Amazon Connect using Lambda Function URLs
 * or Connect APIs based on the configuration.
 */
public class ConnectEventPublisher {
    
    private static final Logger logger = Logger.getLogger(ConnectEventPublisher.class.getName());
    
    private final ConnectConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AtomicLong eventsSent = new AtomicLong(0);
    private volatile long lastHeartbeat = 0;
    
    public ConnectEventPublisher(ConnectConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getConnectionTimeoutMs()))
                .build();
        
        logger.info("Connect Event Publisher initialized for: " + config.getLambdaFunctionUrl());
    }
    
    /**
     * Publish a call event to Connect
     */
    public void publishEvent(ConnectCallEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getLambdaFunctionUrl()))
                    .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "CTI-Event-Monitor/1.0")
                    .header("X-Event-Type", "CALL_EVENT")
                    .header("X-Call-ID", event.getCallId())
                    .POST(HttpRequest.BodyPublishers.ofString(eventJson))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                eventsSent.incrementAndGet();
                logger.fine("Successfully published call event: " + event.getCallId());
            } else {
                logger.warning("Failed to publish call event. Status: " + response.statusCode() + 
                             ", Response: " + response.body());
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error publishing call event: " + event.getCallId(), e);
            
            // Retry logic could be implemented here
            retryPublishEvent(event, 1);
        }
    }
    
    /**
     * Publish an agent event to Connect
     */
    public void publishEvent(ConnectAgentEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getLambdaFunctionUrl()))
                    .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "CTI-Event-Monitor/1.0")
                    .header("X-Event-Type", "AGENT_EVENT")
                    .header("X-Agent-ID", event.getAgentId())
                    .POST(HttpRequest.BodyPublishers.ofString(eventJson))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                eventsSent.incrementAndGet();
                logger.fine("Successfully published agent event: " + event.getAgentId());
            } else {
                logger.warning("Failed to publish agent event. Status: " + response.statusCode() + 
                             ", Response: " + response.body());
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error publishing agent event: " + event.getAgentId(), e);
            
            // Retry logic could be implemented here
            retryPublishAgentEvent(event, 1);
        }
    }
    
    /**
     * Send heartbeat to Connect
     */
    public void sendHeartbeat() {
        try {
            ConnectHeartbeatEvent heartbeat = new ConnectHeartbeatEvent();
            heartbeat.setTimestamp(System.currentTimeMillis());
            heartbeat.setMonitorStatus("ACTIVE");
            heartbeat.setEventsSent(eventsSent.get());
            
            String heartbeatJson = objectMapper.writeValueAsString(heartbeat);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getLambdaFunctionUrl()))
                    .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "CTI-Event-Monitor/1.0")
                    .header("X-Event-Type", "HEARTBEAT")
                    .POST(HttpRequest.BodyPublishers.ofString(heartbeatJson))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                lastHeartbeat = System.currentTimeMillis();
                logger.fine("Heartbeat sent successfully");
            } else {
                logger.warning("Failed to send heartbeat. Status: " + response.statusCode());
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error sending heartbeat", e);
        }
    }
    
    /**
     * Retry publishing call event
     */
    private void retryPublishEvent(ConnectCallEvent event, int attempt) {
        if (attempt > config.getMaxRetryAttempts()) {
            logger.severe("Max retry attempts reached for call event: " + event.getCallId());
            return;
        }
        
        try {
            Thread.sleep(config.getRetryDelayMs() * attempt);
            
            logger.info("Retrying publish call event (attempt " + attempt + "): " + event.getCallId());
            publishEvent(event);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Retry interrupted for call event: " + event.getCallId());
        }
    }
    
    /**
     * Retry publishing agent event
     */
    private void retryPublishAgentEvent(ConnectAgentEvent event, int attempt) {
        if (attempt > config.getMaxRetryAttempts()) {
            logger.severe("Max retry attempts reached for agent event: " + event.getAgentId());
            return;
        }
        
        try {
            Thread.sleep(config.getRetryDelayMs() * attempt);
            
            logger.info("Retrying publish agent event (attempt " + attempt + "): " + event.getAgentId());
            publishEvent(event);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Retry interrupted for agent event: " + event.getAgentId());
        }
    }
    
    /**
     * Get number of events sent
     */
    public long getEventsSent() {
        return eventsSent.get();
    }
    
    /**
     * Get last heartbeat timestamp
     */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }
}
