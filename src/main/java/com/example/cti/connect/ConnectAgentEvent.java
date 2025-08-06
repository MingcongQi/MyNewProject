package com.example.cti.connect;

import java.time.Instant;
import java.util.Objects;

/**
 * Connect Agent Event
 * 
 * Represents an agent state change event for publishing to external systems
 * like Amazon Connect.
 */
public class ConnectAgentEvent {
    
    private String agentId;
    private Instant timestamp;
    private String eventType;
    private String oldState;
    private String newState;
    private String reasonCode;
    
    public ConnectAgentEvent() {
        this.timestamp = Instant.now();
    }
    
    public ConnectAgentEvent(String agentId, String eventType) {
        this();
        this.agentId = agentId;
        this.eventType = eventType;
    }
    
    // Getters and Setters
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getOldState() {
        return oldState;
    }
    
    public void setOldState(String oldState) {
        this.oldState = oldState;
    }
    
    public String getNewState() {
        return newState;
    }
    
    public void setNewState(String newState) {
        this.newState = newState;
    }
    
    public String getReasonCode() {
        return reasonCode;
    }
    
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectAgentEvent that = (ConnectAgentEvent) o;
        return Objects.equals(agentId, that.agentId) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(oldState, that.oldState) &&
               Objects.equals(newState, that.newState) &&
               Objects.equals(reasonCode, that.reasonCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(agentId, timestamp, eventType, oldState, newState, reasonCode);
    }
    
    @Override
    public String toString() {
        return String.format("ConnectAgentEvent{agentId='%s', eventType='%s', oldState='%s', " +
                           "newState='%s', reasonCode='%s', timestamp=%s}",
                           agentId, eventType, oldState, newState, reasonCode, timestamp);
    }
}
