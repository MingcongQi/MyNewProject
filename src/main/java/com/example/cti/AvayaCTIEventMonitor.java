package com.example.cti;

import com.example.cti.events.*;
import com.example.cti.dmcc.DMCCConnection;
import com.example.cti.connect.ConnectEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Avaya CTI Event Monitor
 * 
 * This class serves as the main CTI Event Monitor component that:
 * 1. Connects to Avaya Aura Contact Center via DMCC API
 * 2. Captures vendor-specific CTI events
 * 3. Translates events into standardized formats
 * 4. Publishes events to Amazon Connect
 * 
 * Based on the architecture described in the Avaya Aura Contact Center Notes
 */
public class AvayaCTIEventMonitor {
    
    private static final Logger logger = Logger.getLogger(AvayaCTIEventMonitor.class.getName());
    
    private final DMCCConnection dmccConnection;
    private final ConnectEventPublisher connectPublisher;
    private final ObjectMapper objectMapper;
    private final ExecutorService eventProcessingExecutor;
    private final ScheduledExecutorService heartbeatExecutor;
    
    // Track active calls and their states
    private final ConcurrentHashMap<String, CallState> activeCalls;
    
    // Configuration
    private final CTIMonitorConfig config;
    
    public AvayaCTIEventMonitor(CTIMonitorConfig config) {
        this.config = config;
        this.dmccConnection = new DMCCConnection(config.getDmccConfig());
        this.connectPublisher = new ConnectEventPublisher(config.getConnectConfig());
        this.objectMapper = new ObjectMapper();
        this.eventProcessingExecutor = Executors.newFixedThreadPool(config.getEventProcessingThreads());
        this.heartbeatExecutor = Executors.newScheduledThreadPool(1);
        this.activeCalls = new ConcurrentHashMap<>();
        
        logger.info("CTI Event Monitor initialized with config: " + config);
    }
    
    /**
     * Start the CTI Event Monitor
     */
    public void start() {
        try {
            logger.info("Starting Avaya CTI Event Monitor...");
            
            // Initialize DMCC connection
            dmccConnection.connect();
            
            // Start event listeners
            startEventListeners();
            
            // Start heartbeat monitoring
            startHeartbeatMonitoring();
            
            logger.info("CTI Event Monitor started successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start CTI Event Monitor", e);
            throw new RuntimeException("Failed to start CTI Event Monitor", e);
        }
    }
    
    /**
     * Stop the CTI Event Monitor
     */
    public void stop() {
        try {
            logger.info("Stopping CTI Event Monitor...");
            
            // Stop executors
            eventProcessingExecutor.shutdown();
            heartbeatExecutor.shutdown();
            
            // Wait for tasks to complete
            if (!eventProcessingExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                eventProcessingExecutor.shutdownNow();
            }
            
            if (!heartbeatExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                heartbeatExecutor.shutdownNow();
            }
            
            // Disconnect from DMCC
            dmccConnection.disconnect();
            
            logger.info("CTI Event Monitor stopped successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error stopping CTI Event Monitor", e);
        }
    }
    
    /**
     * Start event listeners for different CTI event types
     * Uses dynamic discovery instead of hardcoded event names
     */
    private void startEventListeners() {
        // Register a generic event handler that discovers event types dynamically
        dmccConnection.registerEventHandler("*", this::handleGenericEvent);
        
        logger.info("Generic event listener registered - will discover actual event types from your Avaya system");
    }
    
    /**
     * Handle any event type dynamically
     */
    private void handleGenericEvent(Object eventData) {
        eventProcessingExecutor.submit(() -> {
            try {
                // Extract event type from the actual data
                String eventType = extractEventTypeFromData(eventData);
                
                if (eventType != null) {
                    // Route to appropriate handler based on discovered event type
                    routeEventByType(eventType, eventData);
                } else {
                    logger.warning("Could not determine event type from: " + eventData);
                }
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing generic event", e);
            }
        });
    }
    
    /**
     * Route events to appropriate handlers based on discovered type
     */
    private void routeEventByType(String eventType, Object eventData) {
        // Convert to lowercase for comparison
        String lowerEventType = eventType.toLowerCase();
        
        try {
            if (lowerEventType.contains("established") || lowerEventType.contains("originated")) {
                handleCallEstablished(eventData);
            } else if (lowerEventType.contains("cleared") || lowerEventType.contains("disconnected")) {
                handleCallCleared(eventData);
            } else if (lowerEventType.contains("transferred")) {
                handleCallTransferred(eventData);
            } else if (lowerEventType.contains("conferenced") || lowerEventType.contains("conference")) {
                handleCallConferenced(eventData);
            } else if (lowerEventType.contains("held") || lowerEventType.contains("hold")) {
                handleCallHeld(eventData);
            } else if (lowerEventType.contains("retrieved") || lowerEventType.contains("unhold")) {
                handleCallRetrieved(eventData);
            } else if (lowerEventType.contains("delivered") || lowerEventType.contains("alerting")) {
                handleCallDelivered(eventData);
            } else if (lowerEventType.contains("answered") || lowerEventType.contains("connected")) {
                handleCallAnswered(eventData);
            } else if (lowerEventType.contains("agent") || lowerEventType.contains("state")) {
                handleAgentStateChanged(eventData);
            } else {
                // Log unknown event for future implementation
                logger.info("📝 UNHANDLED EVENT TYPE: " + eventType + " - Consider adding handler");
                logger.fine("Event data: " + eventData);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error routing event type: " + eventType, e);
        }
    }
    
    /**
     * Extract event type from event data
     */
    private String extractEventTypeFromData(Object eventData) {
        if (eventData instanceof DMCCEventData) {
            return ((DMCCEventData) eventData).getEventType();
        } else if (eventData instanceof String) {
            // Try to parse XML string
            return parseEventTypeFromXml((String) eventData);
        }
        return null;
    }
    
    /**
     * Parse event type from XML string
     */
    private String parseEventTypeFromXml(String xml) {
        // Simple regex to find event type in XML
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<([A-Za-z]+Event)\\s");
        java.util.regex.Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * Start heartbeat monitoring to ensure connection health
     */
    private void startHeartbeatMonitoring() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!dmccConnection.isConnected()) {
                    logger.warning("DMCC connection lost, attempting to reconnect...");
                    dmccConnection.reconnect();
                }
                
                // Send heartbeat to Connect
                connectPublisher.sendHeartbeat();
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "Heartbeat monitoring error", e);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
    
    /**
     * Handle Call Established event
     */
    private void handleCallEstablished(Object eventData) {
        eventProcessingExecutor.submit(() -> {
            try {
                CallEstablishedEvent event = parseEvent(eventData, CallEstablishedEvent.class);
                
                // Create call state tracking
                CallState callState = new CallState(
                    event.getCallId(),
                    event.getCallingParty(),
                    event.getCalledParty(),
                    System.currentTimeMillis()
                );
                activeCalls.put(event.getCallId(), callState);
                
                // Translate to Connect format and publish
                ConnectCallEvent connectEvent = translateToConnectEvent(event);
                connectPublisher.publishEvent(connectEvent);
                
                logger.info("Processed CallEstablished event for call: " + event.getCallId());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing CallEstablished event", e);
            }
        });
    }
    
    /**
     * Handle Call Cleared event
     */
    private void handleCallCleared(Object eventData) {
        eventProcessingExecutor.submit(() -> {
            try {
                CallClearedEvent event = parseEvent(eventData, CallClearedEvent.class);
                
                // Update call state
                CallState callState = activeCalls.get(event.getCallId());
                if (callState != null) {
                    callState.setEndTime(System.currentTimeMillis());
                    callState.setDisconnectReason(event.getDisconnectReason());
                }
                
                // Translate to Connect format and publish
                ConnectCallEvent connectEvent = translateToConnectEvent(event);
                connectPublisher.publishEvent(connectEvent);
                
                // Remove from active calls
                activeCalls.remove(event.getCallId());
                
                logger.info("Processed CallCleared event for call: " + event.getCallId());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing CallCleared event", e);
            }
        });
    }
    
    /**
     * Handle Call Transferred event
     */
    private void handleCallTransferred(Object eventData) {
        eventProcessingExecutor.submit(() -> {
            try {
                CallTransferredEvent event = parseEvent(eventData, CallTransferredEvent.class);
                
                // Update call state
                CallState callState = activeCalls.get(event.getCallId());
                if (callState != null) {
                    callState.addTransfer(event.getTransferredTo(), System.currentTimeMillis());
                }
                
                // Translate to Connect format and publish
                ConnectCallEvent connectEvent = translateToConnectEvent(event);
                connectPublisher.publishEvent(connectEvent);
                
                logger.info("Processed CallTransferred event for call: " + event.getCallId());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing CallTransferred event", e);
            }
        });
    }
    
    /**
     * Handle Call Conferenced event
     */
    private void handleCallConferenced(Object eventData) {
        eventProcessingExecutor.submit(() -> {
            try {
                CallConferencedEvent event = parseEvent(eventData, CallConferencedEvent.class);
                
                // Update call state
                CallState callState = activeCalls.get(event.getCallId());
                if (callState != null) {
                    callState.addConferenceParticipant(event.getConferenceParticipant(), System.currentTimeMillis());
                }
                
                // Translate to Connect format and publish
                ConnectCallEvent connectEvent = translateToConnectEvent(event);
                connectPublisher.publishEvent(connectEvent);
                
                logger.info("Processed CallConferenced event for call: " + event.getCallId());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing CallConferenced event", e);
            }
        });
    }
    
    /**
     * Handle Call Hold/Retrieve events
     */
    private void handleCallHeld(Object eventData) {
        processCallStateChange(eventData, CallHeldEvent.class, "HELD");
    }
    
    private void handleCallRetrieved(Object eventData) {
        processCallStateChange(eventData, CallRetrievedEvent.class, "ACTIVE");
    }
    
    private void handleCallDelivered(Object eventData) {
        processCallStateChange(eventData, CallDeliveredEvent.class, "DELIVERED");
    }
    
    private void handleCallAnswered(Object eventData) {
        processCallStateChange(eventData, CallAnsweredEvent.class, "ANSWERED");
    }
    
    /**
     * Handle Agent State Changed event
     */
    private void handleAgentStateChanged(Object eventData) {
        eventProcessingExecutor.submit(() -> {
            try {
                AgentStateChangedEvent event = parseEvent(eventData, AgentStateChangedEvent.class);
                
                // Translate to Connect format and publish
                ConnectAgentEvent connectEvent = translateToConnectAgentEvent(event);
                connectPublisher.publishEvent(connectEvent);
                
                logger.info("Processed AgentStateChanged event for agent: " + event.getAgentId());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing AgentStateChanged event", e);
            }
        });
    }
    
    /**
     * Generic method to process call state changes
     */
    private void processCallStateChange(Object eventData, Class<? extends CTIEvent> eventClass, String newState) {
        eventProcessingExecutor.submit(() -> {
            try {
                CTIEvent event = parseEvent(eventData, eventClass);
                
                // Update call state
                CallState callState = activeCalls.get(event.getCallId());
                if (callState != null) {
                    callState.setState(newState);
                    callState.setLastUpdated(System.currentTimeMillis());
                }
                
                // Translate to Connect format and publish
                ConnectCallEvent connectEvent = translateToConnectEvent(event);
                connectPublisher.publishEvent(connectEvent);
                
                logger.info("Processed " + eventClass.getSimpleName() + " event for call: " + event.getCallId());
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing " + eventClass.getSimpleName() + " event", e);
            }
        });
    }
    
    /**
     * Parse raw event data into typed event objects
     */
    private <T extends CTIEvent> T parseEvent(Object eventData, Class<T> eventClass) throws Exception {
        if (eventData instanceof String) {
            return objectMapper.readValue((String) eventData, eventClass);
        } else {
            // Convert object to JSON string then parse
            String jsonString = objectMapper.writeValueAsString(eventData);
            return objectMapper.readValue(jsonString, eventClass);
        }
    }
    
    /**
     * Translate CTI events to Connect format
     */
    private ConnectCallEvent translateToConnectEvent(CTIEvent ctiEvent) {
        ConnectCallEvent connectEvent = new ConnectCallEvent();
        
        // Map common fields
        connectEvent.setCallId(ctiEvent.getCallId());
        connectEvent.setTimestamp(ctiEvent.getTimestamp());
        connectEvent.setEventType(mapEventType(ctiEvent.getClass().getSimpleName()));
        
        // Add call state information if available
        CallState callState = activeCalls.get(ctiEvent.getCallId());
        if (callState != null) {
            connectEvent.setCallingParty(callState.getCallingParty());
            connectEvent.setCalledParty(callState.getCalledParty());
            connectEvent.setState(callState.getState());
            connectEvent.setDuration(callState.getDuration());
        }
        
        // Add event-specific data
        connectEvent.setEventData(ctiEvent.getEventData());
        
        return connectEvent;
    }
    
    /**
     * Translate Agent events to Connect format
     */
    private ConnectAgentEvent translateToConnectAgentEvent(AgentStateChangedEvent agentEvent) {
        ConnectAgentEvent connectEvent = new ConnectAgentEvent();
        
        connectEvent.setAgentId(agentEvent.getAgentId());
        connectEvent.setTimestamp(agentEvent.getTimestamp());
        connectEvent.setEventType("AGENT_STATE_CHANGE");
        connectEvent.setOldState(agentEvent.getOldState());
        connectEvent.setNewState(agentEvent.getNewState());
        connectEvent.setReasonCode(agentEvent.getReasonCode());
        
        return connectEvent;
    }
    
    /**
     * Map CTI event types to Connect event types
     */
    private String mapEventType(String ctiEventType) {
        return switch (ctiEventType) {
            case "CallEstablishedEvent" -> "CALL_INITIATED";
            case "CallClearedEvent" -> "CALL_DISCONNECTED";
            case "CallTransferredEvent" -> "CALL_TRANSFERRED";
            case "CallConferencedEvent" -> "CALL_CONFERENCED";
            case "CallHeldEvent" -> "CALL_HELD";
            case "CallRetrievedEvent" -> "CALL_RETRIEVED";
            case "CallDeliveredEvent" -> "CALL_DELIVERED";
            case "CallAnsweredEvent" -> "CALL_ANSWERED";
            default -> "UNKNOWN_EVENT";
        };
    }
    
    /**
     * Get current status of the monitor
     */
    public MonitorStatus getStatus() {
        return new MonitorStatus(
            dmccConnection.isConnected(),
            activeCalls.size(),
            connectPublisher.getEventsSent(),
            connectPublisher.getLastHeartbeat()
        );
    }
    
    /**
     * Get active calls information
     */
    public ConcurrentHashMap<String, CallState> getActiveCalls() {
        return new ConcurrentHashMap<>(activeCalls);
    }
}
