package com.example.cti;

import com.example.cti.events.*;
import com.example.cti.dmcc.*;
import com.example.cti.connect.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.CountDownLatch;

/**
 * CTI Event Monitor Application
 * 
 * Main application for monitoring CTI events from Avaya Aura Contact Center
 * and processing them according to ECMA-269 CSTA Phase III standards.
 * 
 * This application:
 * 1. Connects to Avaya DMCC API
 * 2. Monitors CTI events (calls, agent states, etc.)
 * 3. Processes events according to ECMA-269 CSTA standards
 * 4. Publishes standardized events to external systems
 * 
 * Based on ECMA-269 "Services for Computer Supported Telecommunications Applications (CSTA) Phase III"
 * @see <a href="https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/">ECMA-269 Standard</a>
 */
public class CTIEventMonitorApplication {
    
    private static final Logger logger = Logger.getLogger(CTIEventMonitorApplication.class.getName());
    
    private AvayaCTIEventMonitor ctiEventMonitor;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    
    public static void main(String[] args) {
        CTIEventMonitorApplication app = new CTIEventMonitorApplication();
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received, stopping CTI Event Monitor...");
            app.stop();
        }));
        
        try {
            app.start();
            app.waitForShutdown();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start CTI Event Monitor Application", e);
            System.exit(1);
        }
    }
    
    /**
     * Start the CTI Event Monitor Application
     */
    public void start() {
        try {
            logger.info("🚀 Starting CTI Event Monitor Application...");
            logger.info("📋 ECMA-269 CSTA Phase III Compliant");
            
            // Load configuration
            CTIMonitorConfig config = loadConfiguration();
            
            // Initialize CTI Event Monitor
            ctiEventMonitor = new AvayaCTIEventMonitor(config);
            
            // Start monitoring
            ctiEventMonitor.start();
            
            logger.info("✅ CTI Event Monitor Application started successfully");
            logger.info("🔍 Monitoring CTI events from Avaya Aura Contact Center");
            logger.info("📡 Publishing standardized events according to ECMA-269 CSTA");
            
            // Print status information
            printStatusInformation();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start CTI Event Monitor Application", e);
            throw new RuntimeException("Application startup failed", e);
        }
    }
    
    /**
     * Stop the CTI Event Monitor Application
     */
    public void stop() {
        try {
            logger.info("🛑 Stopping CTI Event Monitor Application...");
            
            if (ctiEventMonitor != null) {
                ctiEventMonitor.stop();
            }
            
            logger.info("✅ CTI Event Monitor Application stopped successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error stopping CTI Event Monitor Application", e);
        } finally {
            shutdownLatch.countDown();
        }
    }
    
    /**
     * Wait for shutdown signal
     */
    private void waitForShutdown() {
        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Application interrupted");
        }
    }
    
    /**
     * Load CTI Monitor configuration
     */
    private CTIMonitorConfig loadConfiguration() {
        logger.info("📋 Loading CTI Monitor configuration...");
        
        // Create default configuration
        CTIMonitorConfig config = new CTIMonitorConfig();
        
        // DMCC Configuration
        CTIMonitorConfig.DMCCConfig dmccConfig = new CTIMonitorConfig.DMCCConfig();
        dmccConfig.setServerHost(getEnvOrDefault("DMCC_HOST", "localhost"));
        dmccConfig.setServerPort(Integer.parseInt(getEnvOrDefault("DMCC_PORT", "4721")));
        dmccConfig.setUsername(getEnvOrDefault("DMCC_USERNAME", "admin"));
        dmccConfig.setPassword(getEnvOrDefault("DMCC_PASSWORD", "password"));
        dmccConfig.setSecure(Boolean.parseBoolean(getEnvOrDefault("DMCC_SECURE", "true")));
        config.setDmccConfig(dmccConfig);
        
        // Connect Configuration (for publishing events)
        CTIMonitorConfig.ConnectConfig connectConfig = new CTIMonitorConfig.ConnectConfig();
        connectConfig.setInstanceId(getEnvOrDefault("CONNECT_INSTANCE_ID", ""));
        connectConfig.setRegion(getEnvOrDefault("CONNECT_REGION", "us-east-1"));
        connectConfig.setAccessKey(getEnvOrDefault("AWS_ACCESS_KEY_ID", ""));
        connectConfig.setSecretKey(getEnvOrDefault("AWS_SECRET_ACCESS_KEY", ""));
        config.setConnectConfig(connectConfig);
        
        // Processing Configuration
        config.setEventProcessingThreads(Integer.parseInt(getEnvOrDefault("EVENT_PROCESSING_THREADS", "4")));
        config.setMaxQueueSize(Integer.parseInt(getEnvOrDefault("MAX_QUEUE_SIZE", "1000")));
        config.setHeartbeatInterval(Integer.parseInt(getEnvOrDefault("HEARTBEAT_INTERVAL", "30")));
        
        logger.info("✅ Configuration loaded successfully");
        return config;
    }
    
    /**
     * Get environment variable or default value
     */
    private String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Print status information
     */
    private void printStatusInformation() {
        logger.info("📊 CTI Event Monitor Status:");
        
        if (ctiEventMonitor != null) {
            MonitorStatus status = ctiEventMonitor.getStatus();
            logger.info("  🔗 DMCC Connection: " + (status.isDmccConnected() ? "✅ Connected" : "❌ Disconnected"));
            logger.info("  📞 Active Calls: " + status.getActiveCalls());
            logger.info("  📤 Events Sent: " + status.getEventsSent());
            logger.info("  💓 Last Heartbeat: " + status.getLastHeartbeat());
        }
        
        logger.info("🎯 Supported CSTA Events:");
        logger.info("  📞 DeliveredEvent (ECMA-269 Section 17.2.5)");
        logger.info("  ✅ EstablishedEvent (ECMA-269 Section 17.2.8)");
        logger.info("  📴 CallClearedEvent (ECMA-269 Section 17.2.2)");
        logger.info("  🔌 ConnectionClearedEvent (ECMA-269 Section 17.2.3)");
        logger.info("  🎯 DivertedEvent, TransferredEvent, ConferencedEvent, QueuedEvent");
        
        logger.info("🌐 XML Namespace: http://www.ecma-international.org/standards/ecma-269/csta");
        logger.info("📚 Documentation: See ECMA_269_CSTA_COMPLIANCE.md");
    }
    
    /**
     * Get current application status
     */
    public ApplicationStatus getStatus() {
        if (ctiEventMonitor == null) {
            return new ApplicationStatus(false, "Not started", null);
        }
        
        MonitorStatus monitorStatus = ctiEventMonitor.getStatus();
        return new ApplicationStatus(
            monitorStatus.isDmccConnected(),
            "Running",
            monitorStatus
        );
    }
    
    /**
     * Application Status class
     */
    public static class ApplicationStatus {
        private final boolean running;
        private final String status;
        private final MonitorStatus monitorStatus;
        
        public ApplicationStatus(boolean running, String status, MonitorStatus monitorStatus) {
            this.running = running;
            this.status = status;
            this.monitorStatus = monitorStatus;
        }
        
        public boolean isRunning() { return running; }
        public String getStatus() { return status; }
        public MonitorStatus getMonitorStatus() { return monitorStatus; }
        
        @Override
        public String toString() {
            return String.format("ApplicationStatus{running=%s, status='%s', monitorStatus=%s}",
                    running, status, monitorStatus);
        }
    }
}
