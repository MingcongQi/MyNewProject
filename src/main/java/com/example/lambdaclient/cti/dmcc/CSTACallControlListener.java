package com.example.lambdaclient.cti.dmcc;

import java.util.logging.Logger;

/**
 * ECMA CSTA Call Control Listener
 * 
 * This class implements the CallControlListener interface to receive
 * Call Control events that indicate when third-party calls have a
 * change of state. These events are then forwarded to the CTI Event
 * Monitor for processing and potential publishing to Amazon Connect.
 * 
 * Based on ch.ecma.csta.callcontrol.CallControlListener interface
 * from the Avaya DMCC SDK.
 */
public class CSTACallControlListener {
    
    private static final Logger logger = Logger.getLogger(CSTACallControlListener.class.getName());
    
    private final CTIEventMonitor ctiEventMonitor;
    
    public CSTACallControlListener(CTIEventMonitor ctiEventMonitor) {
        this.ctiEventMonitor = ctiEventMonitor;
    }
    
    /**
     * Called when a call is delivered (ringing)
     * Indicates the call state has changed to "ringing" at the destination
     */
    public void callDelivered(Object callDeliveredEvent) {
        try {
            logger.info("📞 CALL DELIVERED (Ringing): " + extractCallId(callDeliveredEvent));
            
            String eventXml = convertToEventXml("EventRinging", callDeliveredEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callDelivered event: " + e.getMessage());
        }
    }
    
    /**
     * Called when a call is established (answered)
     * Indicates the call state has changed to "established/connected"
     */
    public void callEstablished(Object callEstablishedEvent) {
        try {
            logger.info("✅ CALL ESTABLISHED (Connected): " + extractCallId(callEstablishedEvent));
            
            String eventXml = convertToEventXml("EventEstablished", callEstablishedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callEstablished event: " + e.getMessage());
        }
    }
    
    /**
     * Called when a call is cleared (ended)
     * Indicates the call state has changed to "cleared/disconnected"
     */
    public void callCleared(Object callClearedEvent) {
        try {
            logger.info("📴 CALL CLEARED (Ended): " + extractCallId(callClearedEvent));
            
            String eventXml = convertToEventXml("EventReleased", callClearedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callCleared event: " + e.getMessage());
        }
    }
    
    /**
     * Called when a call connection is cleared
     * Indicates a specific connection within a call has been cleared
     */
    public void connectionCleared(Object connectionClearedEvent) {
        try {
            logger.info("🔌 CONNECTION CLEARED: " + extractCallId(connectionClearedEvent));
            
            String eventXml = convertToEventXml("EventConnectionCleared", connectionClearedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing connectionCleared event: " + e.getMessage());
        }
    }
    
    /**
     * Called when a call is diverted (transferred/forwarded)
     * Indicates the call state has changed due to diversion
     */
    public void callDiverted(Object callDivertedEvent) {
        try {
            logger.info("🎯 CALL DIVERTED: " + extractCallId(callDivertedEvent));
            
            String eventXml = convertToEventXml("EventDiverted", callDivertedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callDiverted event: " + e.getMessage());
        }
    }
    
    /**
     * Called when a call is transferred
     * Indicates the call state has changed due to transfer
     */
    public void callTransferred(Object callTransferredEvent) {
        try {
            logger.info("↔️ CALL TRANSFERRED: " + extractCallId(callTransferredEvent));
            
            String eventXml = convertToEventXml("EventTransferred", callTransferredEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callTransferred event: " + e.getMessage());
        }
    }
    
    /**
     * Called when a call is conferenced
     * Indicates the call state has changed due to conference
     */
    public void callConferenced(Object callConferencedEvent) {
        try {
            logger.info("👥 CALL CONFERENCED: " + extractCallId(callConferencedEvent));
            
            String eventXml = convertToEventXml("EventConferenced", callConferencedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callConferenced event: " + e.getMessage());
        }
    }
    
    /**
     * Called when call information changes
     * Indicates the call state or party information has changed
     */
    public void callInformationChanged(Object callInfoChangedEvent) {
        try {
            logger.info("ℹ️ CALL INFO CHANGED: " + extractCallId(callInfoChangedEvent));
            
            String eventXml = convertToEventXml("EventPartyChanged", callInfoChangedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callInformationChanged event: " + e.getMessage());
        }
    }
    
    /**
     * Called when a call is queued
     * Indicates the call state has changed to "queued"
     */
    public void callQueued(Object callQueuedEvent) {
        try {
            logger.info("⏳ CALL QUEUED: " + extractCallId(callQueuedEvent));
            
            String eventXml = convertToEventXml("EventQueued", callQueuedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callQueued event: " + e.getMessage());
        }
    }
    
    /**
     * Called for generic service events
     * Indicates service-level state changes
     */
    public void serviceInitiated(Object serviceInitiatedEvent) {
        try {
            logger.info("🚀 SERVICE INITIATED: " + extractCallId(serviceInitiatedEvent));
            
            String eventXml = convertToEventXml("ServiceInitiated", serviceInitiatedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing serviceInitiated event: " + e.getMessage());
        }
    }
    
    /**
     * Process the call control event through the CTI Event Monitor
     */
    private void processCallControlEvent(String eventXml) {
        ctiEventMonitor.processEvent(eventXml)
            .thenAccept(result -> {
                if (result.isProcessed()) {
                    logger.fine("✅ Call control event processed: " + result.getEventResult().getEventType());
                    if (result.isPublishedToConnect()) {
                        logger.fine("📤 Event published to Connect");
                    }
                } else {
                    logger.warning("❌ Call control event processing failed: " + result.getMessage());
                }
            })
            .exceptionally(throwable -> {
                logger.severe("💥 Call control event processing exception: " + throwable.getMessage());
                return null;
            });
    }
    
    /**
     * Extract call ID from the event object
     * This method would need to be implemented based on the actual
     * DMCC SDK event object structure
     */
    private String extractCallId(Object eventObject) {
        try {
            // TODO: Implement based on actual DMCC SDK event structure
            // Example approaches:
            // - Use reflection to get callId field
            // - Cast to specific event type and call getCallId()
            // - Use toString() and parse with regex
            
            if (eventObject != null) {
                String eventStr = eventObject.toString();
                // Simple regex to extract call ID from string representation
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("callId[=:]\\s*([^,\\s}]+)");
                java.util.regex.Matcher matcher = pattern.matcher(eventStr);
                if (matcher.find()) {
                    return matcher.group(1);
                }
                
                // Fallback: generate a temporary ID based on object hash
                return "call-" + Math.abs(eventObject.hashCode());
            }
            
            return "unknown-call-id";
            
        } catch (Exception e) {
            logger.warning("Failed to extract call ID from event: " + e.getMessage());
            return "error-call-id";
        }
    }
    
    /**
     * Convert DMCC event object to XML format for processing
     * This method creates standardized XML that the EventDiscovery can parse
     */
    private String convertToEventXml(String eventType, Object eventObject) {
        try {
            String callId = extractCallId(eventObject);
            long timestamp = System.currentTimeMillis();
            
            // Extract additional metadata from the event object
            String ani = extractField(eventObject, "ani", "callingNumber", "from");
            String dnis = extractField(eventObject, "dnis", "calledNumber", "to");
            String deviceId = extractField(eventObject, "deviceId", "device", "extension");
            String agentId = extractField(eventObject, "agentId", "agent", "user");
            String queueId = extractField(eventObject, "queueId", "queue", "huntGroup");
            
            // Build standardized XML
            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append("<").append(eventType).append(" xmlns=\"http://www.avaya.com/csta\">");
            xmlBuilder.append("<callId>").append(callId).append("</callId>");
            xmlBuilder.append("<timestamp>").append(timestamp).append("</timestamp>");
            
            if (ani != null) xmlBuilder.append("<ani>").append(ani).append("</ani>");
            if (dnis != null) xmlBuilder.append("<dnis>").append(dnis).append("</dnis>");
            if (deviceId != null) xmlBuilder.append("<deviceId>").append(deviceId).append("</deviceId>");
            if (agentId != null) xmlBuilder.append("<agentId>").append(agentId).append("</agentId>");
            if (queueId != null) xmlBuilder.append("<queueId>").append(queueId).append("</queueId>");
            
            // Add raw event data for debugging
            xmlBuilder.append("<rawEvent><![CDATA[").append(eventObject.toString()).append("]]></rawEvent>");
            
            xmlBuilder.append("</").append(eventType).append(">");
            
            return xmlBuilder.toString();
            
        } catch (Exception e) {
            logger.warning("Failed to convert event to XML: " + e.getMessage());
            
            // Return minimal XML on error
            return "<" + eventType + " xmlns=\"http://www.avaya.com/csta\">" +
                   "<callId>" + extractCallId(eventObject) + "</callId>" +
                   "<timestamp>" + System.currentTimeMillis() + "</timestamp>" +
                   "<error>Failed to parse event details</error>" +
                   "</" + eventType + ">";
        }
    }
    
    /**
     * Extract specific field from event object using multiple possible field names
     */
    private String extractField(Object eventObject, String... fieldNames) {
        try {
            String eventStr = eventObject.toString();
            
            for (String fieldName : fieldNames) {
                // Try different patterns for field extraction
                String[] patterns = {
                    fieldName + "[=:]\\s*([^,\\s}]+)",
                    "\"" + fieldName + "\"\\s*[=:]\\s*\"([^\"]+)\"",
                    fieldName + "\\s*=\\s*'([^']+)'",
                    "<" + fieldName + ">([^<]+)</" + fieldName + ">"
                };
                
                for (String patternStr : patterns) {
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternStr, java.util.regex.Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher matcher = pattern.matcher(eventStr);
                    if (matcher.find()) {
                        return matcher.group(1).trim();
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get statistics about processed call control events
     */
    public CallControlStats getStats() {
        // This would be implemented to track event counts
        return new CallControlStats();
    }
    
    /**
     * Statistics class for call control events
     */
    public static class CallControlStats {
        private long callsDelivered = 0;
        private long callsEstablished = 0;
        private long callsCleared = 0;
        private long callsDiverted = 0;
        private long callsTransferred = 0;
        private long callsConferenced = 0;
        private long callsQueued = 0;
        
        // Getters would be implemented here
        public long getCallsDelivered() { return callsDelivered; }
        public long getCallsEstablished() { return callsEstablished; }
        public long getCallsCleared() { return callsCleared; }
        public long getCallsDiverted() { return callsDiverted; }
        public long getCallsTransferred() { return callsTransferred; }
        public long getCallsConferenced() { return callsConferenced; }
        public long getCallsQueued() { return callsQueued; }
        
        public long getTotalEvents() {
            return callsDelivered + callsEstablished + callsCleared + 
                   callsDiverted + callsTransferred + callsConferenced + callsQueued;
        }
    }
}
