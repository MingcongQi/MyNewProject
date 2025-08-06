package com.example.cti.events;

import java.time.Instant;
import java.util.Objects;

/**
 * Agent State Changed Event
 * 
 * Represents an agent state change event in the CTI system.
 * This event is triggered when an agent's state changes (e.g., Available, Busy, Away, etc.)
 */
public class AgentStateChangedEvent extends CTIEvent {
    
    private String agentId;
    private String oldState;
    private String newState;
    private String reasonCode;
    private String workMode;
    private String extension;
    
    public AgentStateChangedEvent() {
        super();
        this.eventType = "AgentStateChanged";
    }
    
    public AgentStateChangedEvent(String agentId, String oldState, String newState) {
        super();
        this.eventType = "AgentStateChanged";
        this.agentId = agentId;
        this.oldState = oldState;
        this.newState = newState;
        this.timestamp = Instant.now();
    }
    
    public AgentStateChangedEvent(String agentId, String deviceId, String oldState, String newState, String reasonCode) {
        super(null, deviceId, "AgentStateChanged");
        this.agentId = agentId;
        this.oldState = oldState;
        this.newState = newState;
        this.reasonCode = reasonCode;
    }
    
    // Getters and Setters
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
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
    
    public String getWorkMode() {
        return workMode;
    }
    
    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AgentStateChangedEvent that = (AgentStateChangedEvent) o;
        return Objects.equals(agentId, that.agentId) &&
               Objects.equals(oldState, that.oldState) &&
               Objects.equals(newState, that.newState) &&
               Objects.equals(reasonCode, that.reasonCode) &&
               Objects.equals(workMode, that.workMode) &&
               Objects.equals(extension, that.extension);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), agentId, oldState, newState, reasonCode, workMode, extension);
    }
    
    @Override
    public String toString() {
        return String.format("AgentStateChangedEvent{agentId='%s', oldState='%s', newState='%s', " +
                           "reasonCode='%s', workMode='%s', extension='%s', timestamp=%s}",
                           agentId, oldState, newState, reasonCode, workMode, extension, timestamp);
    }
}
