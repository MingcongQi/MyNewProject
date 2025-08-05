package com.example.lambdaclient.cti.dmcc;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CTI Event Discovery and Handler System
 * 
 * This class discovers and handles DMCC events from Avaya systems for integration
 * with Amazon Connect. Based on the CTI Event Monitor architecture that captures
 * vendor-specific CTI events and publishes them to Connect using Connect APIs.
 * 
 * Supports the complete call flow from initial contact through transfers, queuing,
 * and final disposition as outlined in the sequence diagram.
 */
public class EventDiscovery {
    
    private static final Logger logger = Logger.getLogger(EventDiscovery.class.getName());
    
    // Track discovered event types and their metadata
    private final Map<String, EventMetadata> discoveredEvents = new ConcurrentHashMap<>();
    
    // Track active call sessions for correlation
    private final Map<String, CallSession> activeCalls = new ConcurrentHashMap<>();
    
    // Known DMCC event types from the sequence diagram
    public enum KnownEventType {
        // Call establishment events
        EVENT_RINGING("EventRinging", "Call is ringing at destination"),
        EVENT_QUEUED("EventQueued", "Call has been queued"),
        EVENT_DIVERTED("EventDiverted", "Call has been diverted to agent"),
        EVENT_PARTY_CHANGED("EventPartyChanged", "Call party information changed"),
        EVENT_RELEASED("EventReleased", "Call has been released/ended"),
        
        // SIP/Media events
        SIP_INVITE("SipInvite", "SIP INVITE received"),
        SIP_BYE("SipBye", "SIP BYE received"),
        SIP_EVENT("SipEvent", "Generic SIP event"),
        
        // Connect integration events
        CONTACT_CREATED("ContactCreated", "New contact created in Connect"),
        CONTACT_STATE_UPDATED("ContactStateUpdated", "Contact state updated"),
        CONTACT_MAPPING_CREATED("ContactMappingCreated", "Call mapping created"),
        
        // Unknown/Generic
        UNKNOWN_EVENT("UnknownEvent", "Event type not recognized");
        
        private final String eventName;
        private final String description;
        
        KnownEventType(String eventName, String description) {
            this.eventName = eventName;
            this.description = description;
        }
        
        public String getEventName() { return eventName; }
        public String getDescription() { return description; }
        
        public static KnownEventType fromString(String eventName) {
            for (KnownEventType type : values()) {
                if (type.eventName.equalsIgnoreCase(eventName)) {
                    return type;
                }
            }
            return UNKNOWN_EVENT;
        }
    }
    
    // Enhanced patterns for DMCC event detection
    private static final Pattern[] EVENT_PATTERNS = {
        Pattern.compile("<([A-Za-z]+Event)\\s"),                    // <SomeEvent ...>
        Pattern.compile("<([A-Za-z]+)\\s.*Event"),                 // <Something ...Event>
        Pattern.compile("eventType=\"([^\"]+)\""),                  // eventType="EventName"
        Pattern.compile("<eventName>([^<]+)</eventName>"),          // <eventName>EventName</eventName>
        Pattern.compile("xmlns.*#([A-Za-z]+Event)"),                // namespace#EventName
        Pattern.compile("<csta:([A-Za-z]+Event)"),                  // CSTA namespace events
        Pattern.compile("Event=\"([^\"]+)\""),                      // Event="EventName"
        Pattern.compile("<([A-Za-z]*(?:Ringing|Queued|Diverted|Released|PartyChanged))"), // Specific events
    };
    
    // Call identifier patterns
    private static final Pattern[] CALL_ID_PATTERNS = {
        Pattern.compile("callId=\"([^\"]+)\""),
        Pattern.compile("<callId>([^<]+)</callId>"),
        Pattern.compile("connectionId=\"([^\"]+)\""),
        Pattern.compile("<connectionId>([^<]+)</connectionId>"),
        Pattern.compile("ucid=\"([^\"]+)\""),  // Universal Call ID
        Pattern.compile("<ucid>([^<]+)</ucid>"),
    };
    
    /**
     * Metadata for discovered events
     */
    public static class EventMetadata {
        private final String eventType;
        private final Instant firstSeen;
        private final Instant lastSeen;
        private int occurrenceCount;
        private final Set<String> associatedCallIds;
        private String sampleXml;
        
        public EventMetadata(String eventType, String sampleXml) {
            this.eventType = eventType;
            this.firstSeen = Instant.now();
            this.lastSeen = Instant.now();
            this.occurrenceCount = 1;
            this.associatedCallIds = ConcurrentHashMap.newKeySet();
            this.sampleXml = sampleXml;
        }
        
        public void recordOccurrence(String callId, String xml) {
            this.occurrenceCount++;
            if (callId != null) {
                this.associatedCallIds.add(callId);
            }
            // Keep the most recent sample
            this.sampleXml = xml;
        }
        
        // Getters
        public String getEventType() { return eventType; }
        public Instant getFirstSeen() { return firstSeen; }
        public Instant getLastSeen() { return lastSeen; }
        public int getOccurrenceCount() { return occurrenceCount; }
        public Set<String> getAssociatedCallIds() { return Set.copyOf(associatedCallIds); }
        public String getSampleXml() { return sampleXml; }
    }
    
    /**
     * Call session tracking for correlation
     */
    public static class CallSession {
        private final String callId;
        private final Instant startTime;
        private String currentState;
        private final List<String> eventHistory;
        private String connectContactId;
        private Map<String, String> metadata;
        
        public CallSession(String callId) {
            this.callId = callId;
            this.startTime = Instant.now();
            this.currentState = "INITIATED";
            this.eventHistory = new ArrayList<>();
            this.metadata = new HashMap<>();
        }
        
        public void addEvent(String eventType) {
            this.eventHistory.add(eventType + "@" + Instant.now());
            this.currentState = eventType;
        }
        
        public void setConnectContactId(String contactId) {
            this.connectContactId = contactId;
        }
        
        public void addMetadata(String key, String value) {
            this.metadata.put(key, value);
        }
        
        // Getters
        public String getCallId() { return callId; }
        public Instant getStartTime() { return startTime; }
        public String getCurrentState() { return currentState; }
        public List<String> getEventHistory() { return new ArrayList<>(eventHistory); }
        public String getConnectContactId() { return connectContactId; }
        public Map<String, String> getMetadata() { return new HashMap<>(metadata); }
    }
    
    /**
     * Analyze incoming XML to discover and classify event types
     */
    public CTIEventResult processEvent(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return new CTIEventResult(null, null, null, false);
        }
        
        String eventType = discoverEventType(xmlContent);
        String callId = extractCallId(xmlContent);
        KnownEventType knownType = KnownEventType.fromString(eventType);
        
        // Update event metadata
        updateEventMetadata(eventType, callId, xmlContent);
        
        // Update call session if we have a call ID
        if (callId != null) {
            updateCallSession(callId, eventType, xmlContent);
        }
        
        // Determine if this event should be published to Connect
        boolean shouldPublish = shouldPublishToConnect(knownType, xmlContent);
        
        logger.info(String.format("🎯 PROCESSED EVENT: %s (CallID: %s, Publish: %s)", 
                                eventType, callId != null ? callId : "N/A", shouldPublish));
        
        return new CTIEventResult(eventType, callId, knownType, shouldPublish);
    }
    
    /**
     * Extract event type from XML content
     */
    private String discoverEventType(String xmlContent) {
        // Try each pattern to extract event type
        for (Pattern pattern : EVENT_PATTERNS) {
            Matcher matcher = pattern.matcher(xmlContent);
            if (matcher.find()) {
                String eventType = matcher.group(1);
                return eventType;
            }
        }
        
        // Log unknown event format for analysis
        logger.warning("❓ UNKNOWN EVENT FORMAT - Please analyze:");
        logger.warning("XML: " + xmlContent.substring(0, Math.min(500, xmlContent.length())));
        
        return "UnknownEvent";
    }
    
    /**
     * Extract call identifier from XML content
     */
    private String extractCallId(String xmlContent) {
        for (Pattern pattern : CALL_ID_PATTERNS) {
            Matcher matcher = pattern.matcher(xmlContent);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
    
    /**
     * Update event metadata tracking
     */
    private void updateEventMetadata(String eventType, String callId, String xmlContent) {
        EventMetadata metadata = discoveredEvents.get(eventType);
        if (metadata == null) {
            metadata = new EventMetadata(eventType, xmlContent.substring(0, Math.min(500, xmlContent.length())));
            discoveredEvents.put(eventType, metadata);
            logger.info("🔍 DISCOVERED NEW EVENT TYPE: " + eventType);
        } else {
            metadata.recordOccurrence(callId, xmlContent.substring(0, Math.min(500, xmlContent.length())));
        }
    }
    
    /**
     * Update call session tracking
     */
    private void updateCallSession(String callId, String eventType, String xmlContent) {
        CallSession session = activeCalls.computeIfAbsent(callId, CallSession::new);
        session.addEvent(eventType);
        
        // Extract additional metadata from XML
        extractAndStoreMetadata(session, xmlContent);
        
        // Clean up completed calls
        if (isCallEndEvent(eventType)) {
            // Keep for a short time for correlation, then remove
            // In production, you might want to persist this data
            logger.info("📞 CALL COMPLETED: " + callId + " (Duration: " + 
                       java.time.Duration.between(session.getStartTime(), Instant.now()).toSeconds() + "s)");
        }
    }
    
    /**
     * Extract metadata from XML and store in call session
     */
    private void extractAndStoreMetadata(CallSession session, String xmlContent) {
        // Extract common metadata fields
        extractField(session, xmlContent, "agentId", Pattern.compile("agentId=\"([^\"]+)\""));
        extractField(session, xmlContent, "queueId", Pattern.compile("queueId=\"([^\"]+)\""));
        extractField(session, xmlContent, "ani", Pattern.compile("ani=\"([^\"]+)\""));
        extractField(session, xmlContent, "dnis", Pattern.compile("dnis=\"([^\"]+)\""));
        extractField(session, xmlContent, "ucid", Pattern.compile("ucid=\"([^\"]+)\""));
    }
    
    /**
     * Helper method to extract and store field values
     */
    private void extractField(CallSession session, String xmlContent, String fieldName, Pattern pattern) {
        Matcher matcher = pattern.matcher(xmlContent);
        if (matcher.find()) {
            session.addMetadata(fieldName, matcher.group(1));
        }
    }
    
    /**
     * Determine if this event should be published to Amazon Connect
     */
    private boolean shouldPublishToConnect(KnownEventType eventType, String xmlContent) {
        switch (eventType) {
            case EVENT_RINGING:
            case EVENT_QUEUED:
            case EVENT_DIVERTED:
            case EVENT_PARTY_CHANGED:
            case EVENT_RELEASED:
                return true; // Core call events should always be published
            case SIP_INVITE:
            case SIP_BYE:
                return true; // SIP events for media correlation
            case CONTACT_CREATED:
            case CONTACT_STATE_UPDATED:
                return false; // These are Connect-generated events
            default:
                // For unknown events, check if they contain call-related information
                return containsCallInformation(xmlContent);
        }
    }
    
    /**
     * Check if XML contains call-related information
     */
    private boolean containsCallInformation(String xmlContent) {
        String lower = xmlContent.toLowerCase();
        return lower.contains("callid") || 
               lower.contains("connectionid") ||
               lower.contains("ucid") ||
               lower.contains("ani") ||
               lower.contains("dnis");
    }
    
    /**
     * Check if this is a call termination event
     */
    private boolean isCallEndEvent(String eventType) {
        String lower = eventType.toLowerCase();
        return lower.contains("released") || 
               lower.contains("cleared") ||
               lower.contains("disconnected") ||
               lower.contains("bye");
    }
    
    /**
     * Get all discovered event types with metadata
     */
    public Map<String, EventMetadata> getDiscoveredEventsWithMetadata() {
        return new HashMap<>(discoveredEvents);
    }
    
    /**
     * Get active call sessions
     */
    public Map<String, CallSession> getActiveCallSessions() {
        return new HashMap<>(activeCalls);
    }
    
    /**
     * Print comprehensive event discovery summary
     */
    public void printEventSummary() {
        logger.info("📊 CTI EVENT DISCOVERY SUMMARY");
        logger.info("================================");
        logger.info("Total discovered event types: " + discoveredEvents.size());
        logger.info("Active call sessions: " + activeCalls.size());
        
        logger.info("\n📋 DISCOVERED EVENT TYPES:");
        discoveredEvents.entrySet().stream()
            .sorted(Map.Entry.<String, EventMetadata>comparingByValue(
                (a, b) -> Integer.compare(b.getOccurrenceCount(), a.getOccurrenceCount())))
            .forEach(entry -> {
                EventMetadata meta = entry.getValue();
                KnownEventType knownType = KnownEventType.fromString(entry.getKey());
                logger.info(String.format("  ✓ %s (Count: %d, Calls: %d) - %s", 
                    entry.getKey(), 
                    meta.getOccurrenceCount(),
                    meta.getAssociatedCallIds().size(),
                    knownType.getDescription()));
            });
        
        logger.info("\n📞 ACTIVE CALL SESSIONS:");
        activeCalls.values().stream()
            .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
            .forEach(session -> {
                logger.info(String.format("  📱 %s: %s (Events: %d)", 
                    session.getCallId(),
                    session.getCurrentState(),
                    session.getEventHistory().size()));
            });
    }
    
    /**
     * Result object for event processing
     */
    public static class CTIEventResult {
        private final String eventType;
        private final String callId;
        private final KnownEventType knownEventType;
        private final boolean shouldPublishToConnect;
        
        public CTIEventResult(String eventType, String callId, KnownEventType knownEventType, boolean shouldPublishToConnect) {
            this.eventType = eventType;
            this.callId = callId;
            this.knownEventType = knownEventType;
            this.shouldPublishToConnect = shouldPublishToConnect;
        }
        
        // Getters
        public String getEventType() { return eventType; }
        public String getCallId() { return callId; }
        public KnownEventType getKnownEventType() { return knownEventType; }
        public boolean shouldPublishToConnect() { return shouldPublishToConnect; }
        
        @Override
        public String toString() {
            return String.format("CTIEventResult{eventType='%s', callId='%s', knownType=%s, publish=%s}", 
                               eventType, callId, knownEventType, shouldPublishToConnect);
        }
    }
    
    /**
     * Generate handler method name for event type
     */
    public String suggestHandlerName(String eventType) {
        if (eventType == null) return "handleUnknownEvent";
        
        // Convert EventName to handleEventName
        return "handle" + eventType;
    }
    
    /**
     * Clean up completed call sessions (call periodically)
     */
    public void cleanupCompletedCalls(int maxAgeMinutes) {
        Instant cutoff = Instant.now().minusSeconds(maxAgeMinutes * 60L);
        activeCalls.entrySet().removeIf(entry -> {
            CallSession session = entry.getValue();
            boolean isOld = session.getStartTime().isBefore(cutoff);
            boolean isCompleted = isCallEndEvent(session.getCurrentState());
            
            if (isOld && isCompleted) {
                logger.info("🧹 CLEANED UP COMPLETED CALL: " + entry.getKey());
                return true;
            }
            return false;
        });
    }
}
