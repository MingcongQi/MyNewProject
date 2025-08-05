package com.example.lambdaclient.cti.dmcc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * CTI Event Monitor
 * 
 * Main orchestration class that implements the CTI Event Monitor component
 * from the architecture diagram. This class:
 * 
 * 1. Receives DMCC events from Avaya systems
 * 2. Discovers and classifies event types
 * 3. Tracks call sessions and correlations
 * 4. Publishes relevant events to Amazon Connect
 * 
 * Based on the sequence diagram showing the flow from Avaya DMCC events
 * through the CTI Event Monitor to Amazon Connect.
 */
public class CTIEventMonitor {
    
    private static final Logger logger = Logger.getLogger(CTIEventMonitor.class.getName());
    
    private final EventDiscovery eventDiscovery;
    private final ConnectEventPublisher connectPublisher;
    private final ScheduledExecutorService scheduler;
    
    // Configuration
    private final CTIMonitorConfig config;
    
    // Statistics
    private long totalEventsProcessed = 0;
    private long eventsPublishedToConnect = 0;
    private long eventProcessingErrors = 0;
    
    public CTIEventMonitor(CTIMonitorConfig config, 
                          ConnectEventPublisher.ConnectApiClient connectClient) {
        this.config = config;
        this.eventDiscovery = new EventDiscovery();
        this.connectPublisher = new ConnectEventPublisher(connectClient);
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        // Start periodic cleanup tasks
        startPeriodicTasks();
        
        logger.info("🚀 CTI EVENT MONITOR STARTED");
        logger.info("   - Connect Integration: " + (connectClient != null ? "ENABLED" : "DISABLED"));
        logger.info("   - Cleanup Interval: " + config.getCleanupIntervalMinutes() + " minutes");
    }
    
    /**
     * Main entry point for processing DMCC events
     * This method implements the core logic shown in the sequence diagram
     */
    public CompletableFuture<ProcessingResult> processEvent(String xmlContent) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                totalEventsProcessed++;
                
                // Step 1: Discover and classify the event
                EventDiscovery.CTIEventResult eventResult = eventDiscovery.processEvent(xmlContent);
                
                if (eventResult.getEventType() == null) {
                    logger.warning("⚠️ Failed to identify event type from XML");
                    return new ProcessingResult(false, false, "Failed to identify event type", null);
                }
                
                // Step 2: Get call session information
                EventDiscovery.CallSession callSession = null;
                if (eventResult.getCallId() != null) {
                    callSession = eventDiscovery.getActiveCallSessions().get(eventResult.getCallId());
                }
                
                // Step 3: Determine if we should publish to Connect
                boolean shouldPublish = eventResult.shouldPublishToConnect() && config.isConnectIntegrationEnabled();
                
                // Step 4: Publish to Connect if needed
                boolean publishSuccess = false;
                if (shouldPublish && callSession != null) {
                    try {
                        publishSuccess = connectPublisher.publishEvent(eventResult, callSession).get();
                        if (publishSuccess) {
                            eventsPublishedToConnect++;
                        }
                    } catch (Exception e) {
                        logger.severe("Failed to publish to Connect: " + e.getMessage());
                        eventProcessingErrors++;
                    }
                }
                
                // Log processing result
                logProcessingResult(eventResult, shouldPublish, publishSuccess);
                
                return new ProcessingResult(true, publishSuccess, "Success", eventResult);
                
            } catch (Exception e) {
                eventProcessingErrors++;
                logger.severe("Error processing CTI event: " + e.getMessage());
                return new ProcessingResult(false, false, "Processing error: " + e.getMessage(), null);
            }
        });
    }
    
    /**
     * Process multiple events in batch
     */
    public CompletableFuture<BatchProcessingResult> processEventBatch(String[] xmlEvents) {
        return CompletableFuture.supplyAsync(() -> {
            int successful = 0;
            int published = 0;
            int failed = 0;
            
            for (String xmlEvent : xmlEvents) {
                try {
                    ProcessingResult result = processEvent(xmlEvent).get();
                    if (result.isProcessed()) {
                        successful++;
                        if (result.isPublishedToConnect()) {
                            published++;
                        }
                    } else {
                        failed++;
                    }
                } catch (Exception e) {
                    failed++;
                    logger.warning("Batch processing error: " + e.getMessage());
                }
            }
            
            return new BatchProcessingResult(xmlEvents.length, successful, published, failed);
        });
    }
    
    /**
     * Get current monitoring statistics
     */
    public MonitoringStats getStats() {
        return new MonitoringStats(
            totalEventsProcessed,
            eventsPublishedToConnect,
            eventProcessingErrors,
            eventDiscovery.getDiscoveredEventsWithMetadata().size(),
            eventDiscovery.getActiveCallSessions().size(),
            connectPublisher.getCallToContactMappings().size()
        );
    }
    
    /**
     * Print comprehensive status report
     */
    public void printStatusReport() {
        logger.info("📊 CTI EVENT MONITOR STATUS REPORT");
        logger.info("=====================================");
        
        MonitoringStats stats = getStats();
        logger.info("📈 PROCESSING STATISTICS:");
        logger.info("   Total Events Processed: " + stats.getTotalEventsProcessed());
        logger.info("   Events Published to Connect: " + stats.getEventsPublishedToConnect());
        logger.info("   Processing Errors: " + stats.getEventProcessingErrors());
        logger.info("   Success Rate: " + String.format("%.2f%%", stats.getSuccessRate()));
        
        logger.info("\n🔍 DISCOVERY STATISTICS:");
        logger.info("   Discovered Event Types: " + stats.getDiscoveredEventTypes());
        logger.info("   Active Call Sessions: " + stats.getActiveCallSessions());
        logger.info("   Connect Contact Mappings: " + stats.getConnectContactMappings());
        
        // Print detailed event discovery information
        eventDiscovery.printEventSummary();
    }
    
    /**
     * Start periodic maintenance tasks
     */
    private void startPeriodicTasks() {
        // Cleanup completed calls every configured interval
        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    eventDiscovery.cleanupCompletedCalls(config.getCallRetentionMinutes());
                    logger.fine("🧹 Periodic cleanup completed");
                } catch (Exception e) {
                    logger.warning("Cleanup task error: " + e.getMessage());
                }
            },
            config.getCleanupIntervalMinutes(),
            config.getCleanupIntervalMinutes(),
            TimeUnit.MINUTES
        );
        
        // Print status report periodically
        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    printStatusReport();
                } catch (Exception e) {
                    logger.warning("Status report error: " + e.getMessage());
                }
            },
            config.getStatusReportIntervalMinutes(),
            config.getStatusReportIntervalMinutes(),
            TimeUnit.MINUTES
        );
    }
    
    /**
     * Log processing result
     */
    private void logProcessingResult(EventDiscovery.CTIEventResult eventResult, 
                                   boolean shouldPublish, boolean publishSuccess) {
        String status = shouldPublish ? 
            (publishSuccess ? "✅ PUBLISHED" : "❌ PUBLISH_FAILED") : 
            "ℹ️ NOT_PUBLISHED";
            
        logger.info(String.format("🎯 PROCESSED: %s [%s] CallID: %s - %s", 
            eventResult.getEventType(),
            eventResult.getKnownEventType().name(),
            eventResult.getCallId() != null ? eventResult.getCallId() : "N/A",
            status));
    }
    
    /**
     * Shutdown the monitor gracefully
     */
    public void shutdown() {
        logger.info("🛑 Shutting down CTI Event Monitor...");
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Print final status report
        printStatusReport();
        
        logger.info("✅ CTI Event Monitor shutdown complete");
    }
    
    /**
     * Configuration class for CTI Monitor
     */
    public static class CTIMonitorConfig {
        private boolean connectIntegrationEnabled = true;
        private int cleanupIntervalMinutes = 15;
        private int callRetentionMinutes = 60;
        private int statusReportIntervalMinutes = 30;
        
        // Getters and setters
        public boolean isConnectIntegrationEnabled() { return connectIntegrationEnabled; }
        public CTIMonitorConfig setConnectIntegrationEnabled(boolean enabled) { 
            this.connectIntegrationEnabled = enabled; return this; 
        }
        
        public int getCleanupIntervalMinutes() { return cleanupIntervalMinutes; }
        public CTIMonitorConfig setCleanupIntervalMinutes(int minutes) { 
            this.cleanupIntervalMinutes = minutes; return this; 
        }
        
        public int getCallRetentionMinutes() { return callRetentionMinutes; }
        public CTIMonitorConfig setCallRetentionMinutes(int minutes) { 
            this.callRetentionMinutes = minutes; return this; 
        }
        
        public int getStatusReportIntervalMinutes() { return statusReportIntervalMinutes; }
        public CTIMonitorConfig setStatusReportIntervalMinutes(int minutes) { 
            this.statusReportIntervalMinutes = minutes; return this; 
        }
    }
    
    /**
     * Result classes
     */
    public static class ProcessingResult {
        private final boolean processed;
        private final boolean publishedToConnect;
        private final String message;
        private final EventDiscovery.CTIEventResult eventResult;
        
        public ProcessingResult(boolean processed, boolean publishedToConnect, 
                              String message, EventDiscovery.CTIEventResult eventResult) {
            this.processed = processed;
            this.publishedToConnect = publishedToConnect;
            this.message = message;
            this.eventResult = eventResult;
        }
        
        // Getters
        public boolean isProcessed() { return processed; }
        public boolean isPublishedToConnect() { return publishedToConnect; }
        public String getMessage() { return message; }
        public EventDiscovery.CTIEventResult getEventResult() { return eventResult; }
    }
    
    public static class BatchProcessingResult {
        private final int totalEvents;
        private final int successfulEvents;
        private final int publishedEvents;
        private final int failedEvents;
        
        public BatchProcessingResult(int total, int successful, int published, int failed) {
            this.totalEvents = total;
            this.successfulEvents = successful;
            this.publishedEvents = published;
            this.failedEvents = failed;
        }
        
        // Getters
        public int getTotalEvents() { return totalEvents; }
        public int getSuccessfulEvents() { return successfulEvents; }
        public int getPublishedEvents() { return publishedEvents; }
        public int getFailedEvents() { return failedEvents; }
        public double getSuccessRate() { 
            return totalEvents > 0 ? (double) successfulEvents / totalEvents * 100 : 0; 
        }
    }
    
    public static class MonitoringStats {
        private final long totalEventsProcessed;
        private final long eventsPublishedToConnect;
        private final long eventProcessingErrors;
        private final int discoveredEventTypes;
        private final int activeCallSessions;
        private final int connectContactMappings;
        
        public MonitoringStats(long totalEventsProcessed, long eventsPublishedToConnect,
                             long eventProcessingErrors, int discoveredEventTypes,
                             int activeCallSessions, int connectContactMappings) {
            this.totalEventsProcessed = totalEventsProcessed;
            this.eventsPublishedToConnect = eventsPublishedToConnect;
            this.eventProcessingErrors = eventProcessingErrors;
            this.discoveredEventTypes = discoveredEventTypes;
            this.activeCallSessions = activeCallSessions;
            this.connectContactMappings = connectContactMappings;
        }
        
        // Getters
        public long getTotalEventsProcessed() { return totalEventsProcessed; }
        public long getEventsPublishedToConnect() { return eventsPublishedToConnect; }
        public long getEventProcessingErrors() { return eventProcessingErrors; }
        public int getDiscoveredEventTypes() { return discoveredEventTypes; }
        public int getActiveCallSessions() { return activeCallSessions; }
        public int getConnectContactMappings() { return connectContactMappings; }
        
        public double getSuccessRate() {
            return totalEventsProcessed > 0 ? 
                (double) (totalEventsProcessed - eventProcessingErrors) / totalEventsProcessed * 100 : 0;
        }
    }
}
