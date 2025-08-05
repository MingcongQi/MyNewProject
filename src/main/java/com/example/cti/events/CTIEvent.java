package com.example.cti.events;

import java.util.Map;

/**
 * Base class for all CTI Events
 */
public abstract class CTIEvent {
    
    protected String callId;
    protected long timestamp;
    protected String deviceId;
    protected String eventType;
    protected Map<String, Object> eventData;
    
    public CTIEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public CTIEvent(String callId, String deviceId, String eventType) {
        this();
        this.callId = callId;
        this.deviceId = deviceId;
        this.eventType = eventType;
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
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Map<String, Object> getEventData() {
        return eventData;
    }
    
    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }
    
    @Override
    public String toString() {
        return "CTIEvent{" +
                "callId='" + callId + '\'' +
                ", timestamp=" + timestamp +
                ", deviceId='" + deviceId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventData=" + eventData +
                '}';
    }
}
