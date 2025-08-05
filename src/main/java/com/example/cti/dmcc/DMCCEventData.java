package com.example.cti.dmcc;

/**
 * DMCC Event Data container
 * 
 * This class holds raw event data received from the DMCC API
 */
public class DMCCEventData {
    
    private String eventType;
    private String rawXml;
    private long timestamp;
    private String callId;
    private String deviceId;
    private String connectionId;
    private String callingParty;
    private String calledParty;
    private String agentId;
    private String state;
    private String reasonCode;
    
    public DMCCEventData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getRawXml() {
        return rawXml;
    }
    
    public void setRawXml(String rawXml) {
        this.rawXml = rawXml;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCallId() {
        return callId;
    }
    
    public void setCallId(String callId) {
        this.callId = callId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
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
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getReasonCode() {
        return reasonCode;
    }
    
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    @Override
    public String toString() {
        return "DMCCEventData{" +
                "eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", callId='" + callId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", connectionId='" + connectionId + '\'' +
                ", callingParty='" + callingParty + '\'' +
                ", calledParty='" + calledParty + '\'' +
                ", agentId='" + agentId + '\'' +
                ", state='" + state + '\'' +
                ", reasonCode='" + reasonCode + '\'' +
                '}';
    }
}
