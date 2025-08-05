package com.example.cti.dmcc;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Amazon Connect Event Publisher
 * 
 * This class handles publishing CTI events from Avaya DMCC to Amazon Connect
 * using Connect APIs. It maintains the mapping between call IDs and Connect
 * contact IDs as shown in the sequence diagram.
 */
public class ConnectEventPublisher {
    
    private static final Logger logger = Logger.getLogger(ConnectEventPublisher.class.getName());
    
    // Map call IDs to Connect contact IDs
    private final Map<String, String> callToContactMapping = new HashMap<>();
    
    // Connect API client (placeholder - implement with actual Connect SDK)
    private final ConnectApiClient connectClient;
    
    public ConnectEventPublisher(ConnectApiClient connectClient) {
        this.connectClient = connectClient;
    }
    
    /**
     * Process and publish CTI event to Amazon Connect
     */
    public CompletableFuture<Boolean> publishEvent(EventDiscovery.CTIEventResult eventResult, 
                                                   EventDiscovery.CallSession callSession) {
        
        if (!eventResult.shouldPublishToConnect()) {
            logger.fine("Event " + eventResult.getEventType() + " not configured for Connect publishing");
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                switch (eventResult.getKnownEventType()) {
                    case EVENT_RINGING:
                        return handleRingingEvent(eventResult, callSession);
                    case EVENT_QUEUED:
                        return handleQueuedEvent(eventResult, callSession);
                    case EVENT_DIVERTED:
                        return handleDivertedEvent(eventResult, callSession);
                    case EVENT_PARTY_CHANGED:
                        return handlePartyChangedEvent(eventResult, callSession);
                    case EVENT_RELEASED:
                        return handleReleasedEvent(eventResult, callSession);
                    case SIP_INVITE:
                        return handleSipInviteEvent(eventResult, callSession);
                    case SIP_BYE:
                        return handleSipByeEvent(eventResult, callSession);
                    default:
                        return handleGenericEvent(eventResult, callSession);
                }
            } catch (Exception e) {
                logger.severe("Failed to publish event to Connect: " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Handle EventRinging - Call is ringing at destination
     */
    private boolean handleRingingEvent(EventDiscovery.CTIEventResult eventResult, 
                                     EventDiscovery.CallSession callSession) {
        logger.info("📞 RINGING: " + eventResult.getCallId());
        
        // Create or update contact in Connect
        String contactId = getOrCreateConnectContact(callSession);
        
        // Update contact state to RINGING
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("RINGING")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("event_type", eventResult.getEventType());
        
        return connectClient.updateContact(update);
    }
    
    /**
     * Handle EventQueued - Call has been queued
     */
    private boolean handleQueuedEvent(EventDiscovery.CTIEventResult eventResult, 
                                    EventDiscovery.CallSession callSession) {
        logger.info("⏳ QUEUED: " + eventResult.getCallId());
        
        String contactId = getOrCreateConnectContact(callSession);
        
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("QUEUED")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("queue_id", callSession.getMetadata().get("queueId"))
            .addAttribute("event_type", eventResult.getEventType());
        
        return connectClient.updateContact(update);
    }
    
    /**
     * Handle EventDiverted - Call has been diverted to agent
     */
    private boolean handleDivertedEvent(EventDiscovery.CTIEventResult eventResult, 
                                      EventDiscovery.CallSession callSession) {
        logger.info("🎯 DIVERTED: " + eventResult.getCallId());
        
        String contactId = getOrCreateConnectContact(callSession);
        
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("CONNECTED")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("agent_id", callSession.getMetadata().get("agentId"))
            .addAttribute("event_type", eventResult.getEventType());
        
        return connectClient.updateContact(update);
    }
    
    /**
     * Handle EventPartyChanged - Call party information changed
     */
    private boolean handlePartyChangedEvent(EventDiscovery.CTIEventResult eventResult, 
                                          EventDiscovery.CallSession callSession) {
        logger.info("👥 PARTY_CHANGED: " + eventResult.getCallId());
        
        String contactId = getOrCreateConnectContact(callSession);
        
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("PARTY_CHANGED")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("event_type", eventResult.getEventType());
        
        // Add any party-specific metadata
        callSession.getMetadata().forEach(update::addAttribute);
        
        return connectClient.updateContact(update);
    }
    
    /**
     * Handle EventReleased - Call has been released/ended
     */
    private boolean handleReleasedEvent(EventDiscovery.CTIEventResult eventResult, 
                                      EventDiscovery.CallSession callSession) {
        logger.info("📴 RELEASED: " + eventResult.getCallId());
        
        String contactId = getOrCreateConnectContact(callSession);
        
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("ENDED")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("call_duration", calculateCallDuration(callSession))
            .addAttribute("event_type", eventResult.getEventType());
        
        boolean result = connectClient.updateContact(update);
        
        // Clean up mapping for completed call
        callToContactMapping.remove(eventResult.getCallId());
        
        return result;
    }
    
    /**
     * Handle SIP INVITE - For media correlation
     */
    private boolean handleSipInviteEvent(EventDiscovery.CTIEventResult eventResult, 
                                       EventDiscovery.CallSession callSession) {
        logger.info("📡 SIP_INVITE: " + eventResult.getCallId());
        
        String contactId = getOrCreateConnectContact(callSession);
        
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("MEDIA_STARTING")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("media_type", "AUDIO")
            .addAttribute("event_type", eventResult.getEventType());
        
        return connectClient.updateContact(update);
    }
    
    /**
     * Handle SIP BYE - Media session ended
     */
    private boolean handleSipByeEvent(EventDiscovery.CTIEventResult eventResult, 
                                    EventDiscovery.CallSession callSession) {
        logger.info("📡 SIP_BYE: " + eventResult.getCallId());
        
        String contactId = getOrCreateConnectContact(callSession);
        
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("MEDIA_ENDED")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("event_type", eventResult.getEventType());
        
        return connectClient.updateContact(update);
    }
    
    /**
     * Handle generic/unknown events
     */
    private boolean handleGenericEvent(EventDiscovery.CTIEventResult eventResult, 
                                     EventDiscovery.CallSession callSession) {
        logger.info("❓ GENERIC: " + eventResult.getEventType() + " for " + eventResult.getCallId());
        
        String contactId = getOrCreateConnectContact(callSession);
        
        ConnectContactUpdate update = new ConnectContactUpdate()
            .setContactId(contactId)
            .setState("CUSTOM_EVENT")
            .setTimestamp(Instant.now())
            .addAttribute("avaya_call_id", eventResult.getCallId())
            .addAttribute("custom_event_type", eventResult.getEventType())
            .addAttribute("event_type", eventResult.getEventType());
        
        return connectClient.updateContact(update);
    }
    
    /**
     * Get existing Connect contact ID or create new one
     */
    private String getOrCreateConnectContact(EventDiscovery.CallSession callSession) {
        String callId = callSession.getCallId();
        
        // Check if we already have a mapping
        String existingContactId = callToContactMapping.get(callId);
        if (existingContactId != null) {
            return existingContactId;
        }
        
        // Create new contact in Connect
        ConnectContactCreate createRequest = new ConnectContactCreate()
            .addAttribute("avaya_call_id", callId)
            .addAttribute("ani", callSession.getMetadata().get("ani"))
            .addAttribute("dnis", callSession.getMetadata().get("dnis"))
            .addAttribute("ucid", callSession.getMetadata().get("ucid"))
            .setInitiationTimestamp(callSession.getStartTime());
        
        String newContactId = connectClient.createContact(createRequest);
        
        if (newContactId != null) {
            callToContactMapping.put(callId, newContactId);
            callSession.setConnectContactId(newContactId);
            logger.info("🆕 CREATED CONNECT CONTACT: " + newContactId + " for call " + callId);
        }
        
        return newContactId;
    }
    
    /**
     * Calculate call duration
     */
    private String calculateCallDuration(EventDiscovery.CallSession callSession) {
        long seconds = java.time.Duration.between(callSession.getStartTime(), Instant.now()).toSeconds();
        return String.valueOf(seconds);
    }
    
    /**
     * Get current call to contact mappings
     */
    public Map<String, String> getCallToContactMappings() {
        return new HashMap<>(callToContactMapping);
    }
    
    /**
     * Data classes for Connect API interactions
     */
    public static class ConnectContactUpdate {
        private String contactId;
        private String state;
        private Instant timestamp;
        private final Map<String, String> attributes = new HashMap<>();
        
        public ConnectContactUpdate setContactId(String contactId) {
            this.contactId = contactId;
            return this;
        }
        
        public ConnectContactUpdate setState(String state) {
            this.state = state;
            return this;
        }
        
        public ConnectContactUpdate setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public ConnectContactUpdate addAttribute(String key, String value) {
            if (value != null) {
                this.attributes.put(key, value);
            }
            return this;
        }
        
        // Getters
        public String getContactId() { return contactId; }
        public String getState() { return state; }
        public Instant getTimestamp() { return timestamp; }
        public Map<String, String> getAttributes() { return new HashMap<>(attributes); }
    }
    
    public static class ConnectContactCreate {
        private Instant initiationTimestamp;
        private final Map<String, String> attributes = new HashMap<>();
        
        public ConnectContactCreate setInitiationTimestamp(Instant timestamp) {
            this.initiationTimestamp = timestamp;
            return this;
        }
        
        public ConnectContactCreate addAttribute(String key, String value) {
            if (value != null) {
                this.attributes.put(key, value);
            }
            return this;
        }
        
        // Getters
        public Instant getInitiationTimestamp() { return initiationTimestamp; }
        public Map<String, String> getAttributes() { return new HashMap<>(attributes); }
    }
    
    /**
     * Interface for Connect API client (implement with actual Connect SDK)
     */
    public interface ConnectApiClient {
        String createContact(ConnectContactCreate request);
        boolean updateContact(ConnectContactUpdate update);
    }
}
