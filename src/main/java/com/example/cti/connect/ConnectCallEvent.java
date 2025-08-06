package com.example.cti.connect;

import java.util.Map;

/**
 * Connect Call Event
 * 
 * Represents a call event in Amazon Connect format
 */
public class ConnectCallEvent {
    
    private String callId;
    private long timestamp;
    private String eventType;
    private String callingParty;
    private String calledParty;
    private String state;
    private long duration;
    private Map<String, Object> eventData;
    private String instanceId;
    private String contactId;
    
    public ConnectCallEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getCallId() {
        return callId;
    }
    
    public void setCallId(String callId) {
        this.callId = callId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getCallingParty() {
        return callingParty;
    }
    
    public void setCallingParty(String callingParty) {
        this.callingParty = callingParty;
    }
    
    public String getCalledParty() {
        return calledParty;
    }
    
    public void setCalledParty(String calledParty) {
        this.calledParty = calledParty;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public Map<String, Object> getEventData() {
        return eventData;
    }
    
    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getContactId() {
        return contactId;
    }
    
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
    
    @Override
    public String toString() {
        return "ConnectCallEvent{" +
                "callId='" + callId + '\'' +
                ", timestamp=" + timestamp +
                ", eventType='" + eventType + '\'' +
                ", callingParty='" + callingParty + '\'' +
                ", calledParty='" + calledParty + '\'' +
                ", state='" + state + '\'' +
                ", duration=" + duration +
                ", instanceId='" + instanceId + '\'' +
                ", contactId='" + contactId + '\'' +
                '}';
    }
}

/**
 * Connect Heartbeat Event
 */
class ConnectHeartbeatEvent {
    
    private long timestamp;
    private String monitorStatus;
    private long eventsSent;
    
    public ConnectHeartbeatEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMonitorStatus() {
        return monitorStatus;
    }
    
    public void setMonitorStatus(String monitorStatus) {
        this.monitorStatus = monitorStatus;
    }
    
    public long getEventsSent() {
        return eventsSent;
    }
    
    public void setEventsSent(long eventsSent) {
        this.eventsSent = eventsSent;
    }
}
