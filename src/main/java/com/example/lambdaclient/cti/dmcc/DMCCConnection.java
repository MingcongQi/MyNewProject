package com.example.lambdaclient.cti.dmcc;

import com.example.lambdaclient.cti.CTIMonitorConfig.DMCCConfig;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.Socket;
import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DMCC Connection Handler
 * 
 * This class manages the connection to Avaya Aura Application Enablement Services (AES)
 * using the Device, Media, and Call Control (DMCC) API.
 * 
 * Based on the Avaya AES DMCC Java API Programmer's Guide
 */
public class DMCCConnection {
    
    private static final Logger logger = Logger.getLogger(DMCCConnection.class.getName());
    
    private final DMCCConfig config;
    private final ConcurrentHashMap<String, Consumer<Object>> eventHandlers;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ExecutorService eventListenerExecutor;
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread eventListenerThread;
    
    // DMCC Protocol Constants
    private static final String DMCC_VERSION = "7.0";
    private static final String PROTOCOL_VERSION = "1.0";
    
    public DMCCConnection(DMCCConfig config) {
        this.config = config;
        this.eventHandlers = new ConcurrentHashMap<>();
        this.eventListenerExecutor = Executors.newSingleThreadExecutor();
        
        logger.info("DMCC Connection initialized for server: " + config.getServerHost() + ":" + config.getServerPort());
    }
    
    /**
     * Connect to the DMCC server
     */
    public void connect() throws Exception {
        if (connected.get()) {
            logger.warning("Already connected to DMCC server");
            return;
        }
        
        try {
            logger.info("Connecting to DMCC server: " + config.getServerHost() + ":" + config.getServerPort());
            
            // Create socket connection
            if (config.isUseSSL()) {
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = factory.createSocket(config.getServerHost(), config.getServerPort());
            } else {
                socket = new Socket(config.getServerHost(), config.getServerPort());
            }
            
            socket.setSoTimeout(config.getReadTimeoutMs());
            
            // Initialize streams
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            // Perform DMCC handshake
            performHandshake();
            
            // Start event listener
            startEventListener();
            
            connected.set(true);
            logger.info("Successfully connected to DMCC server");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to DMCC server", e);
            cleanup();
            throw e;
        }
    }
    
    /**
     * Disconnect from the DMCC server
     */
    public void disconnect() {
        if (!connected.get()) {
            return;
        }
        
        try {
            logger.info("Disconnecting from DMCC server");
            
            // Send stop application request
            sendStopApplicationRequest();
            
            connected.set(false);
            
            // Stop event listener
            if (eventListenerThread != null) {
                eventListenerThread.interrupt();
            }
            
            cleanup();
            
            logger.info("Disconnected from DMCC server");
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during disconnect", e);
        }
    }
    
    /**
     * Reconnect to the DMCC server
     */
    public void reconnect() throws Exception {
        disconnect();
        Thread.sleep(config.getRetryDelayMs());
        connect();
    }
    
    /**
     * Check if connected to DMCC server
     */
    public boolean isConnected() {
        return connected.get() && socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    /**
     * Register event handler for specific event types
     */
    public void registerEventHandler(String eventType, Consumer<Object> handler) {
        eventHandlers.put(eventType, handler);
        logger.info("Registered event handler for: " + eventType);
    }
    
    /**
     * Perform DMCC handshake and authentication
     */
    private void performHandshake() throws Exception {
        logger.info("Performing DMCC handshake");
        
        // Send start application request
        String startAppRequest = buildStartApplicationRequest();
        sendRequest(startAppRequest);
        
        // Read and validate response
        String response = readResponse();
        validateStartApplicationResponse(response);
        
        // Register device if specified
        if (config.getDeviceName() != null && !config.getDeviceName().isEmpty()) {
            registerDevice();
        }
        
        // Start monitoring
        startMonitoring();
        
        logger.info("DMCC handshake completed successfully");
    }
    
    /**
     * Build start application request
     */
    private String buildStartApplicationRequest() {
        StringBuilder request = new StringBuilder();
        request.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        request.append("<StartApplicationSession xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">\n");
        request.append("  <applicationInfo>\n");
        request.append("    <applicationID>").append(config.getApplicationName()).append("</applicationID>\n");
        request.append("    <applicationVersion>").append(DMCC_VERSION).append("</applicationVersion>\n");
        request.append("    <protocolVersion>").append(PROTOCOL_VERSION).append("</protocolVersion>\n");
        request.append("  </applicationInfo>\n");
        request.append("  <requestedProtocolVersions>\n");
        request.append("    <protocolVersion>").append(PROTOCOL_VERSION).append("</protocolVersion>\n");
        request.append("  </requestedProtocolVersions>\n");
        request.append("  <userName>").append(config.getUsername()).append("</userName>\n");
        request.append("  <password>").append(config.getPassword()).append("</password>\n");
        request.append("  <sessionCleanupDelay>180</sessionCleanupDelay>\n");
        request.append("</StartApplicationSession>\n");
        
        return request.toString();
    }
    
    /**
     * Register device for monitoring
     */
    private void registerDevice() throws Exception {
        logger.info("Registering device: " + config.getDeviceName());
        
        String registerRequest = buildRegisterDeviceRequest();
        sendRequest(registerRequest);
        
        String response = readResponse();
        validateRegisterDeviceResponse(response);
        
        logger.info("Device registered successfully");
    }
    
    /**
     * Build register device request
     */
    private String buildRegisterDeviceRequest() {
        StringBuilder request = new StringBuilder();
        request.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        request.append("<RegisterTerminalRequest xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">\n");
        request.append("  <device>").append(config.getDeviceName()).append("</device>\n");
        request.append("</RegisterTerminalRequest>\n");
        
        return request.toString();
    }
    
    /**
     * Start monitoring for events
     */
    private void startMonitoring() throws Exception {
        logger.info("Starting event monitoring");
        
        String monitorRequest = buildMonitorStartRequest();
        sendRequest(monitorRequest);
        
        String response = readResponse();
        validateMonitorStartResponse(response);
        
        logger.info("Event monitoring started successfully");
    }
    
    /**
     * Build monitor start request
     */
    private String buildMonitorStartRequest() {
        StringBuilder request = new StringBuilder();
        request.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        request.append("<MonitorStart xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">\n");
        request.append("  <monitorObject>\n");
        if (config.getDeviceName() != null && !config.getDeviceName().isEmpty()) {
            request.append("    <deviceObject>").append(config.getDeviceName()).append("</deviceObject>\n");
        } else {
            request.append("    <deviceObject>*</deviceObject>\n");
        }
        request.append("  </monitorObject>\n");
        request.append("  <requestedMonitorFilter>\n");
        request.append("    <callcontrol>true</callcontrol>\n");
        request.append("    <physical>true</physical>\n");
        request.append("    <logical>true</logical>\n");
        request.append("    <maintenance>true</maintenance>\n");
        request.append("  </requestedMonitorFilter>\n");
        request.append("</MonitorStart>\n");
        
        return request.toString();
    }
    
    /**
     * Start event listener thread
     */
    private void startEventListener() {
        eventListenerThread = new Thread(() -> {
            logger.info("Event listener thread started");
            
            while (connected.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    String event = readResponse();
                    if (event != null && !event.trim().isEmpty()) {
                        processEvent(event);
                    }
                } catch (InterruptedException e) {
                    logger.info("Event listener thread interrupted");
                    break;
                } catch (Exception e) {
                    if (connected.get()) {
                        logger.log(Level.WARNING, "Error reading event", e);
                        // Try to reconnect on error
                        try {
                            Thread.sleep(1000);
                            if (connected.get()) {
                                reconnect();
                            }
                        } catch (Exception reconnectError) {
                            logger.log(Level.SEVERE, "Failed to reconnect", reconnectError);
                            connected.set(false);
                        }
                    }
                }
            }
            
            logger.info("Event listener thread stopped");
        });
        
        eventListenerThread.setDaemon(true);
        eventListenerThread.start();
    }
    
    /**
     * Process incoming events
     */
    private void processEvent(String eventXml) {
        eventListenerExecutor.submit(() -> {
            try {
                // Parse event type from XML
                String eventType = extractEventType(eventXml);
                
                if (eventType != null) {
                    Consumer<Object> handler = eventHandlers.get(eventType);
                    if (handler != null) {
                        // Convert XML to event object and pass to handler
                        Object eventData = parseEventData(eventXml, eventType);
                        handler.accept(eventData);
                    } else {
                        logger.fine("No handler registered for event type: " + eventType);
                    }
                }
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error processing event", e);
            }
        });
    }
    
    // Add event discovery
    private final EventDiscovery eventDiscovery = new EventDiscovery();
    
    /**
     * Extract event type from XML using discovery
     */
    private String extractEventType(String eventXml) {
        // Use event discovery to find actual event types
        String eventType = eventDiscovery.discoverEventType(eventXml);
        
        // Log discovery progress every 50 events
        if (eventDiscovery.getDiscoveredEvents().size() % 50 == 0) {
            eventDiscovery.printDiscoveredEvents();
        }
        
        return eventType;
    }
    
    /**
     * Parse event data from XML
     */
    private Object parseEventData(String eventXml, String eventType) {
        // Simple event data extraction
        // In a production system, you would use a proper XML parser and create typed objects
        
        DMCCEventData eventData = new DMCCEventData();
        eventData.setEventType(eventType);
        eventData.setRawXml(eventXml);
        eventData.setTimestamp(System.currentTimeMillis());
        
        // Extract common fields
        eventData.setCallId(extractXmlValue(eventXml, "callID"));
        eventData.setDeviceId(extractXmlValue(eventXml, "deviceID"));
        eventData.setConnectionId(extractXmlValue(eventXml, "connectionID"));
        
        return eventData;
    }
    
    /**
     * Extract value from XML (simple implementation)
     */
    private String extractXmlValue(String xml, String tagName) {
        String startTag = "<" + tagName + ">";
        String endTag = "</" + tagName + ">";
        
        int startIndex = xml.indexOf(startTag);
        if (startIndex == -1) return null;
        
        startIndex += startTag.length();
        int endIndex = xml.indexOf(endTag, startIndex);
        if (endIndex == -1) return null;
        
        return xml.substring(startIndex, endIndex).trim();
    }
    
    /**
     * Send request to DMCC server
     */
    private void sendRequest(String request) throws Exception {
        if (writer == null) {
            throw new IllegalStateException("Not connected to DMCC server");
        }
        
        logger.fine("Sending DMCC request: " + request);
        writer.println(request);
        writer.flush();
    }
    
    /**
     * Read response from DMCC server
     */
    private String readResponse() throws Exception {
        if (reader == null) {
            throw new IllegalStateException("Not connected to DMCC server");
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
            
            // Check for end of message (simple implementation)
            if (line.trim().isEmpty() || line.contains("</")) {
                break;
            }
        }
        
        String responseStr = response.toString();
        logger.fine("Received DMCC response: " + responseStr);
        
        return responseStr;
    }
    
    /**
     * Validate start application response
     */
    private void validateStartApplicationResponse(String response) throws Exception {
        if (response.contains("StartApplicationSessionPosResponse")) {
            logger.info("Start application session successful");
        } else if (response.contains("StartApplicationSessionNegResponse")) {
            throw new Exception("Start application session failed: " + response);
        } else {
            throw new Exception("Unexpected response to start application session: " + response);
        }
    }
    
    /**
     * Validate register device response
     */
    private void validateRegisterDeviceResponse(String response) throws Exception {
        if (response.contains("RegisterTerminalResponse")) {
            logger.info("Register device successful");
        } else {
            throw new Exception("Register device failed: " + response);
        }
    }
    
    /**
     * Validate monitor start response
     */
    private void validateMonitorStartResponse(String response) throws Exception {
        if (response.contains("MonitorStartResponse")) {
            logger.info("Monitor start successful");
        } else {
            throw new Exception("Monitor start failed: " + response);
        }
    }
    
    /**
     * Send stop application request
     */
    private void sendStopApplicationRequest() {
        try {
            String stopRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<StopApplicationSession xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\"/>\n";
            sendRequest(stopRequest);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error sending stop application request", e);
        }
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error closing reader", e);
        }
        
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error closing writer", e);
        }
        
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error closing socket", e);
        }
        
        eventListenerExecutor.shutdown();
    }
}
