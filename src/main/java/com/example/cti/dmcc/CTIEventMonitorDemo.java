package com.example.cti.dmcc;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * CTI Event Monitor Demo with DMCC CallControlListener Integration
 * 
 * This enhanced demo shows the complete integration including:
 * 1. DMCC Session Management with CallControlListener
 * 2. Call Control events that indicate third-party call state changes
 * 3. Event processing through the CTI Event Monitor
 * 4. Publishing to Amazon Connect
 * 
 * Based on ch.ecma.csta.callcontrol.CallControlListener pattern
 */
public class CTIEventMonitorDemo {
    
    private static final Logger logger = Logger.getLogger(CTIEventMonitorDemo.class.getName());
    
    public static void main(String[] args) {
        logger.info("🚀 Starting Enhanced CTI Event Monitor Demo");
        logger.info("   📡 Includes DMCC CallControlListener Integration");
        
        // Initialize the complete system
        CTIEventMonitor monitor = createCTIEventMonitor();
        DMCCSessionManager dmccSession = createDMCCSession(monitor);
        
        try {
            // Step 1: Connect to DMCC and register CallControlListener
            demonstrateDMCCConnection(dmccSession);
            
            // Step 2: Show CallControlListener receiving events
            demonstrateCallControlEvents(dmccSession);
            
            // Step 3: Demonstrate manual event processing
            demonstrateManualEventProcessing(monitor);
            
            // Step 4: Show batch processing capabilities
            demonstrateBatchProcessing(monitor);
            
            // Step 5: Display comprehensive statistics
            Thread.sleep(3000); // Allow async operations to complete
            displayFinalStatistics(monitor, dmccSession);
            
        } catch (Exception e) {
            logger.severe("Demo error: " + e.getMessage());
        } finally {
            // Clean shutdown
            cleanupResources(monitor, dmccSession);
        }
        
        logger.info("✅ Enhanced CTI Event Monitor Demo completed");
    }
    
    /**
     * Create and configure the CTI Event Monitor
     */
    private static CTIEventMonitor createCTIEventMonitor() {
        logger.info("\n🏗️ INITIALIZING CTI EVENT MONITOR");
        logger.info("==================================");
        
        // Configure the monitor
        CTIEventMonitor.CTIMonitorConfig config = new CTIEventMonitor.CTIMonitorConfig()
            .setConnectIntegrationEnabled(true)
            .setCleanupIntervalMinutes(5)
            .setCallRetentionMinutes(30)
            .setStatusReportIntervalMinutes(10);
        
        // Create Connect API client (using test client for demo)
        ConnectEventPublisher.ConnectApiClient connectClient = 
            ConnectApiClientImpl.createTestClient("connect-instance-demo");
        
        CTIEventMonitor monitor = new CTIEventMonitor(config, connectClient);
        
        logger.info("✅ CTI Event Monitor initialized");
        return monitor;
    }
    
    /**
     * Create DMCC Session Manager with CallControlListener
     */
    private static DMCCSessionManager createDMCCSession(CTIEventMonitor monitor) {
        logger.info("\n📡 INITIALIZING DMCC SESSION MANAGER");
        logger.info("====================================");
        
        // Create DMCC session manager
        DMCCSessionManager dmccSession = DMCCSessionManager.create(
            monitor,
            "avaya-aes-server.company.com", // Replace with actual server
            "dmcc-user",                    // Replace with actual username
            "dmcc-password"                 // Replace with actual password
        );
        
        logger.info("✅ DMCC Session Manager created");
        return dmccSession;
    }
    
    /**
     * Demonstrate DMCC connection and CallControlListener registration
     */
    private static void demonstrateDMCCConnection(DMCCSessionManager dmccSession) {
        logger.info("\n🔌 DEMONSTRATING DMCC CONNECTION");
        logger.info("================================");
        
        try {
            // Connect to DMCC server and register CallControlListener
            boolean connected = dmccSession.connect();
            
            if (connected) {
                logger.info("✅ DMCC Connection Status: CONNECTED");
                logger.info("   📋 CallControlListener registered for events:");
                logger.info("      • callDelivered (call ringing)");
                logger.info("      • callEstablished (call answered)");
                logger.info("      • callCleared (call ended)");
                logger.info("      • callDiverted (call transferred/forwarded)");
                logger.info("      • callTransferred (call transferred)");
                logger.info("      • callConferenced (call conferenced)");
                logger.info("      • callQueued (call queued)");
                logger.info("      • connectionCleared (connection ended)");
                logger.info("      • serviceInitiated (service started)");
            } else {
                logger.warning("❌ DMCC Connection Status: FAILED");
            }
            
        } catch (Exception e) {
            logger.severe("DMCC connection error: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate CallControlListener receiving call state change events
     */
    private static void demonstrateCallControlEvents(DMCCSessionManager dmccSession) {
        logger.info("\n📞 DEMONSTRATING CALL CONTROL EVENTS");
        logger.info("====================================");
        logger.info("Simulating call state changes that would be received");
        logger.info("from the DMCC CallControlListener...");
        
        try {
            // This simulates what would happen when actual DMCC events are received
            dmccSession.simulateCallControlEvents();
            
            // Allow time for event processing
            Thread.sleep(2000);
            
        } catch (Exception e) {
            logger.severe("Call control event simulation error: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate manual event processing (for comparison)
     */
    private static void demonstrateManualEventProcessing(CTIEventMonitor monitor) {
        logger.info("\n🔧 DEMONSTRATING MANUAL EVENT PROCESSING");
        logger.info("=========================================");
        logger.info("Processing events manually (for comparison with CallControlListener)");
        
        String callId = "call-manual-demo";
        
        // Sample events that would normally come from CallControlListener
        String[] manualEvents = {
            createCallDeliveredXml(callId, "9876543210", "5552000"),
            createCallEstablishedXml(callId, "agent-005"),
            createCallTransferredXml(callId, "agent-006"),
            createCallClearedXml(callId)
        };
        
        // Process each event
        for (int i = 0; i < manualEvents.length; i++) {
            logger.info(String.format("\n📋 MANUAL STEP %d: Processing event...", i + 1));
            
            try {
                CompletableFuture<CTIEventMonitor.ProcessingResult> future = 
                    monitor.processEvent(manualEvents[i]);
                
                CTIEventMonitor.ProcessingResult result = future.get();
                
                logger.info("   Result: " + (result.isProcessed() ? "✅ SUCCESS" : "❌ FAILED"));
                if (result.getEventResult() != null) {
                    logger.info("   Event: " + result.getEventResult().getEventType());
                    logger.info("   Published: " + (result.isPublishedToConnect() ? "YES" : "NO"));
                }
                
                Thread.sleep(500);
                
            } catch (Exception e) {
                logger.severe("   Error processing manual step " + (i + 1) + ": " + e.getMessage());
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
            createCallDeliveredXml("call-batch-001", "1111111111", "5551001"),
            createCallDeliveredXml("call-batch-002", "2222222222", "5551002"),
            createCallEstablishedXml("call-batch-001", "agent-101"),
            createCallEstablishedXml("call-batch-002", "agent-102"),
            createCallClearedXml("call-batch-001"),
            createCallClearedXml("call-batch-002")
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
    
    /**
     * Display comprehensive final statistics
     */
    private static void displayFinalStatistics(CTIEventMonitor monitor, DMCCSessionManager dmccSession) {
        logger.info("\n📊 FINAL SYSTEM STATISTICS");
        logger.info("==========================");
        
        // CTI Event Monitor statistics
        monitor.printStatusReport();
        
        // DMCC Session status
        logger.info("\n📡 DMCC SESSION STATUS:");
        logger.info("   Connection: " + (dmccSession.isConnected() ? "✅ CONNECTED" : "❌ DISCONNECTED"));
        
        // CallControlListener statistics would be shown here
        logger.info("\n🎧 CALL CONTROL LISTENER STATISTICS:");
        logger.info("   (Statistics would be available from actual CallControlListener)");
    }
    
    /**
     * Clean up resources
     */
    private static void cleanupResources(CTIEventMonitor monitor, DMCCSessionManager dmccSession) {
        logger.info("\n🧹 CLEANING UP RESOURCES");
        logger.info("========================");
        
        try {
            // Disconnect DMCC session
            dmccSession.disconnect();
            
            // Shutdown CTI Event Monitor
            monitor.shutdown();
            
            logger.info("✅ All resources cleaned up successfully");
            
        } catch (Exception e) {
            logger.severe("Error during cleanup: " + e.getMessage());
        }
    }
    
    // XML generators for different call control event types
    
    private static String createCallDeliveredXml(String callId, String ani, String dnis) {
        return String.format(
            "<EventRinging xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<ani>%s</ani>" +
            "<dnis>%s</dnis>" +
            "<timestamp>%d</timestamp>" +
            "<eventSource>CallControlListener</eventSource>" +
            "</EventRinging>",
            callId, ani, dnis, System.currentTimeMillis()
        );
    }
    
    private static String createCallEstablishedXml(String callId, String agentId) {
        return String.format(
            "<EventEstablished xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<agentId>%s</agentId>" +
            "<timestamp>%d</timestamp>" +
            "<eventSource>CallControlListener</eventSource>" +
            "</EventEstablished>",
            callId, agentId, System.currentTimeMillis()
        );
    }
    
    private static String createCallTransferredXml(String callId, String newAgentId) {
        return String.format(
            "<EventTransferred xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<agentId>%s</agentId>" +
            "<transferType>BLIND</transferType>" +
            "<timestamp>%d</timestamp>" +
            "<eventSource>CallControlListener</eventSource>" +
            "</EventTransferred>",
            callId, newAgentId, System.currentTimeMillis()
        );
    }
    
    private static String createCallClearedXml(String callId) {
        return String.format(
            "<EventReleased xmlns=\"http://www.avaya.com/csta\">" +
            "<callId>%s</callId>" +
            "<reason>NORMAL_CLEARING</reason>" +
            "<timestamp>%d</timestamp>" +
            "<eventSource>CallControlListener</eventSource>" +
            "</EventReleased>",
            callId, System.currentTimeMillis()
        );
    }
}
