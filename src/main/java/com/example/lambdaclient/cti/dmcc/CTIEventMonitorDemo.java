package com.example.lambdaclient.cti.dmcc;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * CTI Event Monitor Demo
 * 
 * This class demonstrates the complete CTI Event Monitor system that integrates
 * Avaya DMCC events with Amazon Connect. It shows the full flow from the
 * sequence diagram:
 * 
 * 1. Avaya system generates DMCC events
 * 2. CTI Event Monitor processes and classifies events
 * 3. Relevant events are published to Amazon Connect
 * 4. Connect creates/updates contact records
 */
public class CTIEventMonitorDemo {
    
    private static final Logger logger = Logger.getLogger(CTIEventMonitorDemo.class.getName());
    
    public static void main(String[] args) {
        logger.info("🚀 Starting CTI Event Monitor Demo");
        
        // Initialize the system
        CTIEventMonitor monitor = createCTIEventMonitor();
        
        try {
            // Demonstrate the complete call flow from the sequence diagram
            demonstrateCallFlow(monitor);
            
            // Show batch processing capabilities
            demonstrateBatchProcessing(monitor);
            
            // Display final statistics
            Thread.sleep(2000); // Allow async operations to complete
            monitor.printStatusReport();
            
        } catch (Exception e) {
            logger.severe("Demo error: " + e.getMessage());
        } finally {
            // Clean shutdown
            monitor.shutdown();
        }
        
        logger.info("✅ CTI Event Monitor Demo completed");
    }
    
    /**
     * Create and configure the CTI Event Monitor
     */
    private static CTIEventMonitor createCTIEventMonitor() {
        // Configure the monitor
        CTIEventMonitor.CTIMonitorConfig config = new CTIEventMonitor.CTIMonitorConfig()
            .setConnectIntegrationEnabled(true)
            .setCleanupIntervalMinutes(5)
            .setCallRetentionMinutes(30)
            .setStatusReportIntervalMinutes(10);
        
        // Create Connect API client (using test client for demo)
        ConnectEventPublisher.ConnectApiClient connectClient = 
            ConnectApiClientImpl.createTestClient("connect-instance-demo");
        
        return new CTIEventMonitor(config, connectClient);
    }
    
    /**
     * Demonstrate the complete call flow from the sequence diagram
     */
    private static void demonstrateCallFlow(CTIEventMonitor monitor) {
        logger.info("\n📞 DEMONSTRATING COMPLETE CALL FLOW");
        logger.info("===================================");
        
        String callId = "call-12345-demo";
        
        // Sample DMCC events representing a typical call flow
        String[] callFlowEvents = {
            // 1. Call arrives and starts ringing
            createEventRingingXml(callId, "1234567890", "5551234"),
            
            // 2. Call gets queued
            createEventQueuedXml(callId, "queue-support"),
            
            // 3. SIP INVITE for media
            createSipInviteXml(callId),
            
            // 4. Call diverted to agent
            createEventDivertedXml(callId, "agent-001"),
            
            // 5. Party information changes (maybe transfer)
            createEventPartyChangedXml(callId, "agent-002"),
            
            // 6. SIP BYE (media ends)
            createSipByeXml(callId),
            
            // 7. Call released
            createEventReleasedXml(callId)
        };
        
        // Process each event in the call flow
        for (int i = 0; i < callFlowEvents.length; i++) {
            logger.info(String.format("\n📋 STEP %d: Processing event...", i + 1));
            
            try {
                CompletableFuture<CTIEventMonitor.ProcessingResult> future = 
                    monitor.processEvent(callFlowEvents[i]);
                
                CTIEventMonitor.ProcessingResult result = future.get();
                
                logger.info("   Result: " + (result.isProcessed() ? "✅ SUCCESS" : "❌ FAILED"));
                if (result.getEventResult() != null) {
                    logger.info("   Event: " + result.getEventResult().getEventType());
                    logger.info("   Published: " + (result.isPublishedToConnect() ? "YES" : "NO"));
                }
                
                // Small delay to simulate real-world timing
                Thread.sleep(500);
                
            } catch (Exception e) {
                logger.severe("   Error processing step " + (i + 1) + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Demonstrate batch processing capabilities
     */
    private static void demonstrateBatchProcessing(CTIEventMonitor monitor) {
        logger.info("\n📦 DEMONSTRATING BATCH PROCESSING");
        logger.info("=================================");
        
        // Create a batch of events from multiple calls
        String[] batchEvents = {
            createEventRingingXml("call-batch-001", "1111111111", "5551001"),
            createEventRingingXml("call-batch-002", "2222222222", "5551002"),
            createEventQueuedXml("call-batch-001", "queue-sales"),
            createEventQueuedXml("call-batch-002", "queue-support"),
            createEventDivertedXml("call-batch-001", "agent-101"),
            createEventDivertedXml("call-batch-002", "agent-102"),
            createEventReleasedXml("call-batch-001"),
            createEventReleasedXml("call-batch-002")
        };
        
        try {
            CompletableFuture<CTIEventMonitor.BatchProcessingResult> future = 
                monitor.processEventBatch(batchEvents);
            
            CTIEventMonitor.BatchProcessingResult result = future.get();
            
            logger.info("📊 BATCH PROCESSING RESULTS:");
            logger.info("   Total Events: " + result.getTotalEvents());
            logger.info("   Successful: " + result.getSuccessfulEvents());
            logger.info("   Published: " + result.getPublishedEvents());
            logger.info("   Failed: " + result.getFailedEvents());
            logger.info("   Success Rate: " + String.format("%.1f%%", result.getSuccessRate()));
            
        } catch (Exception e) {
            logger.severe("Batch processing error: " + e.getMessage());
        }
    }
    
    // Sample XML generators for different event types
    
    private static String createEventRingingXml(String callId, String ani, String dnis) {
        return String.format(
            "<EventRinging xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<ani>%s</ani>" +
            "<dnis>%s</dnis>" +
            "<timestamp>%d</timestamp>" +
            "</EventRinging>",
            callId, ani, dnis, System.currentTimeMillis()
        );
    }
    
    private static String createEventQueuedXml(String callId, String queueId) {
        return String.format(
            "<EventQueued xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<queueId>%s</queueId>" +
            "<timestamp>%d</timestamp>" +
            "</EventQueued>",
            callId, queueId, System.currentTimeMillis()
        );
    }
    
    private static String createEventDivertedXml(String callId, String agentId) {
        return String.format(
            "<EventDiverted xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<agentId>%s</agentId>" +
            "<timestamp>%d</timestamp>" +
            "</EventDiverted>",
            callId, agentId, System.currentTimeMillis()
        );
    }
    
    private static String createEventPartyChangedXml(String callId, String newAgentId) {
        return String.format(
            "<EventPartyChanged xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<agentId>%s</agentId>" +
            "<changeType>TRANSFER</changeType>" +
            "<timestamp>%d</timestamp>" +
            "</EventPartyChanged>",
            callId, newAgentId, System.currentTimeMillis()
        );
    }
    
    private static String createEventReleasedXml(String callId) {
        return String.format(
            "<EventReleased xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<reason>NORMAL_CLEARING</reason>" +
            "<timestamp>%d</timestamp>" +
            "</EventReleased>",
            callId, System.currentTimeMillis()
        );
    }
    
    private static String createSipInviteXml(String callId) {
        return String.format(
            "<SipInvite>" +
            "<callId>%s</callId>" +
            "<mediaType>AUDIO</mediaType>" +
            "<codec>G711</codec>" +
            "<timestamp>%d</timestamp>" +
            "</SipInvite>",
            callId, System.currentTimeMillis()
        );
    }
    
    private static String createSipByeXml(String callId) {
        return String.format(
            "<SipBye>" +
            "<callId>%s</callId>" +
            "<reason>NORMAL_CLEARING</reason>" +
            "<timestamp>%d</timestamp>" +
            "</SipBye>",
            callId, System.currentTimeMillis()
        );
    }
}
