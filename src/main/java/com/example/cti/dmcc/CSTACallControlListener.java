package com.example.cti.dmcc;

import com.example.cti.events.*;
import java.util.logging.Logger;
import java.time.Instant;

// Import official DMCC event classes (these would be from actual DMCC SDK)
// import ch.ecma.csta.binding.DeliveredEvent;
// import ch.ecma.csta.binding.EstablishedEvent;
// import ch.ecma.csta.binding.CallClearedEvent;
// import ch.ecma.csta.binding.ConnectionClearedEvent;
// import ch.ecma.csta.binding.DivertedEvent;
// import ch.ecma.csta.binding.TransferredEvent;
// import ch.ecma.csta.binding.ConferencedEvent;
// import ch.ecma.csta.binding.QueuedEvent;
// import ch.ecma.csta.callcontrol.CallControlListener;

/**
 * ECMA CSTA Call Control Listener
 * 
 * This class implements the official CallControlListener interface from
 * ch.ecma.csta.callcontrol.CallControlListener to receive Call Control events
 * that indicate when third-party calls have a change of state.
 * 
 * Based on official Avaya DMCC SDK documentation:
 * - CallControlListener: https://support.avaya.com/elmodocs2/cmapi/docs/api/ch/ecma/csta/callcontrol/CallControlListener.html
 * - DeliveredEvent: https://support.avaya.com/elmodocs2/cmapi/docs/xml/ch/ecma/csta/binding/DeliveredEvent.html
 * - EstablishedEvent: https://support.avaya.com/elmodocs2/cmapi/docs/xml/ch/ecma/csta/binding/EstablishedEvent.html
 * - CallClearedEvent: https://support.avaya.com/elmodocs2/cmapi/docs/xml/ch/ecma/csta/binding/CallClearedEvent.html
 * 
 * ECMA-269 Standard References:
 * - DeliveredEvent: Section 17.2.5, Figure 17-36 "Delivered Event"
 * - EstablishedEvent: Section 17.2.8, Figure 17-40 "Established Event"  
 * - CallClearedEvent: Section 17.2.2, Figure 17-31 "Call Cleared Event"
 */
public class CSTACallControlListener /* implements CallControlListener */ {
    
    private static final Logger logger = Logger.getLogger(CSTACallControlListener.class.getName());
    
    private final CTIEventMonitor ctiEventMonitor;
    
    public CSTACallControlListener(CTIEventMonitor ctiEventMonitor) {
        this.ctiEventMonitor = ctiEventMonitor;
    }
    
    /**
     * Official DMCC SDK Method: delivered()
     * 
     * Called when a DeliveredEvent occurs - indicates that a call is being 
     * presented to a device in either the Ringing or Entering Distribution 
     * modes of the alerting state.
     * 
     * From ECMA-269: "Common situations that generate this event include:
     * - A call has been assigned to a device and that device is alerting
     * - A call has been assigned to a distribution device such as an ACD, 
     *   routing device, or hunt group"
     */
    public void delivered(Object deliveredEvent /* DeliveredEvent event */) {
        try {
            logger.info("📞 DELIVERED EVENT (Call Ringing): " + extractCallId(deliveredEvent));
            
            // Create ECMA-269 compliant DeliveredEvent
            DeliveredEvent cstaEvent = createDeliveredEvent(deliveredEvent);
            
            // Process through CTI Event Monitor
            processCallControlEvent(cstaEvent);
            
        } catch (Exception e) {
            logger.severe("Error processing delivered event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: established()
     * 
     * Called when an EstablishedEvent occurs - indicates that a device has 
     * answered or has been connected to a call.
     * 
     * From ECMA-269: "Common situations that generate this event include:
     * - A call has been answered at a device (user has manually gone off-hook)
     * - The AnswerCall service has been successfully invoked
     * - A call has been picked up by another device"
     */
    public void established(Object establishedEvent /* EstablishedEvent event */) {
        try {
            logger.info("✅ ESTABLISHED EVENT (Call Connected): " + extractCallId(establishedEvent));
            
            // Create ECMA-269 compliant EstablishedEvent
            EstablishedEvent cstaEvent = createEstablishedEvent(establishedEvent);
            
            // Process through CTI Event Monitor
            processCallControlEvent(cstaEvent);
            
        } catch (Exception e) {
            logger.severe("Error processing established event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: callCleared()
     * 
     * Called when a CallClearedEvent occurs - indicates that all devices have 
     * been removed from an existing call. The call no longer exists within the 
     * switching sub-domain.
     * 
     * From ECMA-269: "Common situations that generate this event include:
     * - After the last remaining device disconnects from the call
     * - All devices in a call are immediately disconnected
     * - The computing function issues a successful ClearCall service request"
     */
    public void callCleared(Object callClearedEvent /* CallClearedEvent event */) {
        try {
            logger.info("📴 CALL CLEARED EVENT (Call Ended): " + extractCallId(callClearedEvent));
            
            // Create ECMA-269 compliant CallClearedEvent
            CallClearedEvent cstaEvent = createCallClearedEvent(callClearedEvent);
            
            // Process through CTI Event Monitor
            String eventXml = convertToEventXml("CallClearedEvent", cstaEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing callCleared event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: connectionCleared()
     * 
     * Called when a ConnectionClearedEvent occurs - indicates that a specific 
     * connection within a call has been cleared.
     */
    public void connectionCleared(Object connectionClearedEvent /* ConnectionClearedEvent event */) {
        try {
            logger.info("🔌 CONNECTION CLEARED EVENT: " + extractCallId(connectionClearedEvent));
            
            String eventXml = convertToEventXml("ConnectionClearedEvent", connectionClearedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing connectionCleared event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: diverted()
     * 
     * Called when a DivertedEvent occurs - indicates that a call has been 
     * diverted (transferred/forwarded) from one device to another.
     */
    public void diverted(Object divertedEvent /* DivertedEvent event */) {
        try {
            logger.info("🎯 DIVERTED EVENT (Call Diverted): " + extractCallId(divertedEvent));
            
            String eventXml = convertToEventXml("DivertedEvent", divertedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing diverted event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: transferred()
     * 
     * Called when a TransferredEvent occurs - indicates that a call has been 
     * transferred from one device to another.
     * 
     * Note: Official SDK uses "TransferedEvent" (with one 'r') in the interface
     */
    public void transferred(Object transferredEvent /* TransferedEvent event */) {
        try {
            logger.info("↔️ TRANSFERRED EVENT (Call Transferred): " + extractCallId(transferredEvent));
            
            String eventXml = convertToEventXml("TransferredEvent", transferredEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing transferred event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: conferenced()
     * 
     * Called when a ConferencedEvent occurs - indicates that a call has been 
     * conferenced with other parties.
     */
    public void conferenced(Object conferencedEvent /* ConferencedEvent event */) {
        try {
            logger.info("👥 CONFERENCED EVENT (Call Conferenced): " + extractCallId(conferencedEvent));
            
            String eventXml = convertToEventXml("ConferencedEvent", conferencedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing conferenced event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: queued()
     * 
     * Called when a QueuedEvent occurs - indicates that a call has been 
     * placed in a queue (ACD queue, hunt group, etc.).
     */
    public void queued(Object queuedEvent /* QueuedEvent event */) {
        try {
            logger.info("⏳ QUEUED EVENT (Call Queued): " + extractCallId(queuedEvent));
            
            String eventXml = convertToEventXml("QueuedEvent", queuedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing queued event: " + e.getMessage());
        }
    }
    
    /**
     * Official DMCC SDK Method: serviceInitiated()
     * 
     * Called when a ServiceInitiatedEvent occurs - indicates that a service 
     * has been initiated for the call.
     */
    public void serviceInitiated(Object serviceInitiatedEvent /* ServiceInitiatedEvent event */) {
        try {
            logger.info("🚀 SERVICE INITIATED EVENT: " + extractCallId(serviceInitiatedEvent));
            
            String eventXml = convertToEventXml("ServiceInitiatedEvent", serviceInitiatedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing serviceInitiated event: " + e.getMessage());
        }
    }
    
    /**
     * Additional DMCC SDK Methods (from official interface)
     * These are included for completeness based on the official CallControlListener
     */
    
    public void bridged(Object bridgedEvent /* BridgedEvent event */) {
        try {
            logger.info("🌉 BRIDGED EVENT: " + extractCallId(bridgedEvent));
            String eventXml = convertToEventXml("BridgedEvent", bridgedEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing bridged event: " + e.getMessage());
        }
    }
    
    public void digitsDialed(Object digitsDialedEvent /* DigitsDialedEvent event */) {
        try {
            logger.info("🔢 DIGITS DIALED EVENT: " + extractCallId(digitsDialedEvent));
            String eventXml = convertToEventXml("DigitsDialedEvent", digitsDialedEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing digitsDialed event: " + e.getMessage());
        }
    }
    
    public void failed(Object failedEvent /* FailedEvent event */) {
        try {
            logger.info("❌ FAILED EVENT: " + extractCallId(failedEvent));
            String eventXml = convertToEventXml("FailedEvent", failedEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing failed event: " + e.getMessage());
        }
    }
    
    public void held(Object heldEvent /* HeldEvent event */) {
        try {
            logger.info("⏸️ HELD EVENT: " + extractCallId(heldEvent));
            String eventXml = convertToEventXml("HeldEvent", heldEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing held event: " + e.getMessage());
        }
    }
    
    public void networkCapabilitiesChanged(Object networkCapabilitiesChangedEvent /* NetworkCapabilitiesChangedEvent event */) {
        try {
            logger.info("🌐 NETWORK CAPABILITIES CHANGED EVENT: " + extractCallId(networkCapabilitiesChangedEvent));
            String eventXml = convertToEventXml("NetworkCapabilitiesChangedEvent", networkCapabilitiesChangedEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing networkCapabilitiesChanged event: " + e.getMessage());
        }
    }
    
    public void networkReached(Object networkReachedEvent /* NetworkReachedEvent event */) {
        try {
            logger.info("🌐 NETWORK REACHED EVENT: " + extractCallId(networkReachedEvent));
            String eventXml = convertToEventXml("NetworkReachedEvent", networkReachedEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing networkReached event: " + e.getMessage());
        }
    }
    
    public void offered(Object offeredEvent /* OfferedEvent event */) {
        try {
            logger.info("📋 OFFERED EVENT: " + extractCallId(offeredEvent));
            String eventXml = convertToEventXml("OfferedEvent", offeredEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing offered event: " + e.getMessage());
        }
    }
    
    public void originated(Object originatedEvent /* OriginatedEvent event */) {
        try {
            logger.info("📤 ORIGINATED EVENT: " + extractCallId(originatedEvent));
            String eventXml = convertToEventXml("OriginatedEvent", originatedEvent);
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing originated event: " + e.getMessage());
        }
    }
    
    public void retrieved(Object retrievedEvent /* RetrievedEvent event */) {
        try {
            logger.info("▶️ RETRIEVED EVENT: " + extractCallId(retrievedEvent));
            
            // Create ECMA-269 compliant event (placeholder implementation)
            String eventXml = convertToEventXml("RetrievedEvent", retrievedEvent);
            processCallControlEvent(eventXml);
            
        } catch (Exception e) {
            logger.severe("Error processing retrieved event: " + e.getMessage());
        }
    }
    
    /**
     * Additional methods for compatibility with DMCCSessionManager
     */
    public void callDelivered(Object deliveredEvent) {
        delivered(deliveredEvent);
    }
    
    public void callEstablished(Object establishedEvent) {
        established(establishedEvent);
    }
    
    public void callTransferred(Object transferredEvent) {
        transferred(transferredEvent);
    }
    
    /**
     * Process the call control event through the CTI Event Monitor
     */
    private void processCallControlEvent(CSTAEvent cstaEvent) {
        try {
            // Convert to XML for processing
            String eventXml = cstaEvent.toXML();
            processCallControlEvent(eventXml);
        } catch (Exception e) {
            logger.severe("Error processing CSTA event: " + e.getMessage());
        }
    }
    
    /**
     * Process the call control event through the CTI Event Monitor (XML version)
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
     * Create ECMA-269 compliant DeliveredEvent from raw DMCC data
     */
    private DeliveredEvent createDeliveredEvent(Object rawEvent) {
        try {
            String callId = extractCallId(rawEvent);
            String deviceId = extractFieldFromEvent(rawEvent, "alertingDevice", "device");
            String callingDevice = extractFieldFromEvent(rawEvent, "callingDevice", "ani", "callingNumber");
            String calledDevice = extractFieldFromEvent(rawEvent, "calledDevice", "dnis", "calledNumber");
            
            // Create CSTA data types
            ConnectionID connection = new ConnectionID(callId, deviceId != null ? deviceId : "unknown");
            DeviceID alertingDevice = new DeviceID(deviceId != null ? deviceId : "unknown");
            DeviceID calling = callingDevice != null ? new DeviceID(callingDevice) : null;
            DeviceID called = calledDevice != null ? new DeviceID(calledDevice) : null;
            
            // Create the event
            DeliveredEvent event = new DeliveredEvent("monitor-" + System.currentTimeMillis(), connection, alertingDevice);
            event.setCallingDevice(calling);
            event.setCalledDevice(called);
            event.setLocalConnectionInfo(LocalConnectionState.ALERTING);
            event.setCause(CSTACause.NORMAL);
            
            return event;
            
        } catch (Exception e) {
            logger.warning("Failed to create DeliveredEvent: " + e.getMessage());
            // Return minimal event
            String callId = extractCallId(rawEvent);
            ConnectionID connection = new ConnectionID(callId, "unknown");
            DeviceID alertingDevice = new DeviceID("unknown");
            return new DeliveredEvent("monitor-error", connection, alertingDevice);
        }
    }
    
    /**
     * Create ECMA-269 compliant EstablishedEvent from raw DMCC data
     */
    private EstablishedEvent createEstablishedEvent(Object rawEvent) {
        try {
            String callId = extractCallId(rawEvent);
            String deviceId = extractFieldFromEvent(rawEvent, "answeringDevice", "device");
            String callingDevice = extractFieldFromEvent(rawEvent, "callingDevice", "ani", "callingNumber");
            String calledDevice = extractFieldFromEvent(rawEvent, "calledDevice", "dnis", "calledNumber");
            
            // Create CSTA data types
            ConnectionID connection = new ConnectionID(callId, deviceId != null ? deviceId : "unknown");
            DeviceID answeringDevice = new DeviceID(deviceId != null ? deviceId : "unknown");
            DeviceID calling = callingDevice != null ? new DeviceID(callingDevice) : null;
            DeviceID called = calledDevice != null ? new DeviceID(calledDevice) : null;
            
            // Create the event
            EstablishedEvent event = new EstablishedEvent("monitor-" + System.currentTimeMillis(), connection, answeringDevice);
            event.setCallingDevice(calling);
            event.setCalledDevice(called);
            event.setLocalConnectionInfo(LocalConnectionState.CONNECTED);
            event.setCause(CSTACause.NORMAL);
            
            return event;
            
        } catch (Exception e) {
            logger.warning("Failed to create EstablishedEvent: " + e.getMessage());
            // Return minimal event
            String callId = extractCallId(rawEvent);
            ConnectionID connection = new ConnectionID(callId, "unknown");
            DeviceID answeringDevice = new DeviceID("unknown");
            return new EstablishedEvent("monitor-error", connection, answeringDevice);
        }
    }
    
    /**
     * Create ECMA-269 compliant CallClearedEvent from raw DMCC data
     */
    private CallClearedEvent createCallClearedEvent(Object rawEvent) {
        try {
            String callId = extractCallId(rawEvent);
            String disconnectReason = extractFieldFromEvent(rawEvent, "cause", "reason", "disconnectReason");
            
            // Create CSTA data types
            CallID clearedCall = new CallID(callId);
            CSTACause cause = mapDisconnectReasonToCause(disconnectReason);
            
            // Create the event
            CallClearedEvent event = new CallClearedEvent("monitor-" + System.currentTimeMillis(), clearedCall.getCallIdentifier(), cause);
            event.setLocalConnectionInfo(LocalConnectionState.NULL);
            
            return event;
            
        } catch (Exception e) {
            logger.warning("Failed to create CallClearedEvent: " + e.getMessage());
            // Return minimal event
            String callId = extractCallId(rawEvent);
            CallID clearedCall = new CallID(callId);
            return new CallClearedEvent("monitor-error", clearedCall.getCallIdentifier());
        }
    }
    
    /**
     * Map disconnect reason to CSTA cause
     */
    private CSTACause mapDisconnectReasonToCause(String disconnectReason) {
        if (disconnectReason == null) return CSTACause.NORMAL;
        
        return switch (disconnectReason.toLowerCase()) {
            case "normal", "user_disconnect", "hangup" -> CSTACause.NORMAL;
            case "busy" -> CSTACause.BUSY;
            case "no_answer", "timeout" -> CSTACause.NO_ANSWER;
            case "network_error", "network_failure" -> CSTACause.NETWORK_OUT_OF_ORDER;
            case "rejected", "call_rejected" -> CSTACause.CALL_REJECTED;
            default -> CSTACause.NORMAL;
        };
    }
    
    /**
     * Extract call ID from the event object using official DMCC event structure
     * 
     * In the actual DMCC SDK, you would use methods like:
     * - DeliveredEvent.getConnection().getCallID()
     * - EstablishedEvent.getEstablishedConnection().getCallID()
     * - CallClearedEvent.getClearedCall().getCallID()
     */
    private String extractCallId(Object eventObject) {
        try {
            if (eventObject != null) {
                // TODO: Replace with actual DMCC SDK method calls
                /*
                // Example for DeliveredEvent:
                if (eventObject instanceof DeliveredEvent) {
                    DeliveredEvent event = (DeliveredEvent) eventObject;
                    return event.getConnection().getCallID().getValue();
                }
                
                // Example for EstablishedEvent:
                if (eventObject instanceof EstablishedEvent) {
                    EstablishedEvent event = (EstablishedEvent) eventObject;
                    return event.getEstablishedConnection().getCallID().getValue();
                }
                
                // Example for CallClearedEvent:
                if (eventObject instanceof CallClearedEvent) {
                    CallClearedEvent event = (CallClearedEvent) eventObject;
                    return event.getClearedCall().getCallID().getValue();
                }
                */
                
                // Fallback: Use string parsing for demo
                String eventStr = eventObject.toString();
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("callId[=:]\\s*([^,\\s}]+)");
                java.util.regex.Matcher matcher = pattern.matcher(eventStr);
                if (matcher.find()) {
                    return matcher.group(1);
                }
                
                // Generate temporary ID based on object hash
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
     * Uses official DMCC event class names and structure
     */
    private String convertToEventXml(String eventType, Object eventObject) {
        try {
            String callId = extractCallId(eventObject);
            long timestamp = System.currentTimeMillis();
            
            // Extract metadata using official DMCC event methods
            String ani = extractFieldFromEvent(eventObject, "callingDevice", "ani", "callingNumber");
            String dnis = extractFieldFromEvent(eventObject, "calledDevice", "dnis", "calledNumber");
            String deviceId = extractFieldFromEvent(eventObject, "alertingDevice", "answeringDevice", "device");
            String agentId = extractFieldFromEvent(eventObject, "agentId", "agent", "user");
            String queueId = extractFieldFromEvent(eventObject, "queueId", "queue", "huntGroup");
            
            // Build standardized XML using official event type names
            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append("<").append(eventType).append(" xmlns=\"http://www.ecma-international.org/csta\">");
            xmlBuilder.append("<callId>").append(callId).append("</callId>");
            xmlBuilder.append("<timestamp>").append(timestamp).append("</timestamp>");
            
            if (ani != null) xmlBuilder.append("<callingDevice>").append(ani).append("</callingDevice>");
            if (dnis != null) xmlBuilder.append("<calledDevice>").append(dnis).append("</calledDevice>");
            if (deviceId != null) xmlBuilder.append("<alertingDevice>").append(deviceId).append("</alertingDevice>");
            if (agentId != null) xmlBuilder.append("<agentId>").append(agentId).append("</agentId>");
            if (queueId != null) xmlBuilder.append("<queueId>").append(queueId).append("</queueId>");
            
            // Add event source for tracking
            xmlBuilder.append("<eventSource>CallControlListener</eventSource>");
            
            // Add raw event data for debugging
            xmlBuilder.append("<rawEvent><![CDATA[").append(eventObject.toString()).append("]]></rawEvent>");
            
            xmlBuilder.append("</").append(eventType).append(">");
            
            return xmlBuilder.toString();
            
        } catch (Exception e) {
            logger.warning("Failed to convert event to XML: " + e.getMessage());
            
            // Return minimal XML on error
            return "<" + eventType + " xmlns=\"http://www.ecma-international.org/csta\">" +
                   "<callId>" + extractCallId(eventObject) + "</callId>" +
                   "<timestamp>" + System.currentTimeMillis() + "</timestamp>" +
                   "<error>Failed to parse event details</error>" +
                   "</" + eventType + ">";
        }
    }
    
    /**
     * Extract specific field from DMCC event object using official method names
     */
    private String extractFieldFromEvent(Object eventObject, String... fieldNames) {
        try {
            // TODO: Replace with actual DMCC SDK method calls
            /*
            // Example for DeliveredEvent:
            if (eventObject instanceof DeliveredEvent) {
                DeliveredEvent event = (DeliveredEvent) eventObject;
                if (Arrays.asList(fieldNames).contains("callingDevice")) {
                    return event.getCallingDevice().getValue();
                }
                if (Arrays.asList(fieldNames).contains("calledDevice")) {
                    return event.getCalledDevice().getValue();
                }
                if (Arrays.asList(fieldNames).contains("alertingDevice")) {
                    return event.getAlertingDevice().getValue();
                }
            }
            */
            
            // Fallback: Use string parsing for demo
            String eventStr = eventObject.toString();
            
            for (String fieldName : fieldNames) {
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
        // This would be implemented to track event counts by type
        return new CallControlStats();
    }
    
    /**
     * Statistics class for call control events
     */
    public static class CallControlStats {
        private long deliveredEvents = 0;
        private long establishedEvents = 0;
        private long callClearedEvents = 0;
        private long connectionClearedEvents = 0;
        private long divertedEvents = 0;
        private long transferredEvents = 0;
        private long conferencedEvents = 0;
        private long queuedEvents = 0;
        
        // Getters
        public long getDeliveredEvents() { return deliveredEvents; }
        public long getEstablishedEvents() { return establishedEvents; }
        public long getCallClearedEvents() { return callClearedEvents; }
        public long getConnectionClearedEvents() { return connectionClearedEvents; }
        public long getDivertedEvents() { return divertedEvents; }
        public long getTransferredEvents() { return transferredEvents; }
        public long getConferencedEvents() { return conferencedEvents; }
        public long getQueuedEvents() { return queuedEvents; }
        
        public long getTotalEvents() {
            return deliveredEvents + establishedEvents + callClearedEvents + 
                   connectionClearedEvents + divertedEvents + transferredEvents + 
                   conferencedEvents + queuedEvents;
        }
    }
}
