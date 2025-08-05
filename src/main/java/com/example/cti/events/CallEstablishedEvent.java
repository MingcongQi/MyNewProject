package com.example.cti.events;

/**
 * Call Established Event
 * 
 * Represents a call that has been established between parties
 */
public class CallEstablishedEvent extends CTIEvent {
    
    private String callingParty;
    private String calledParty;
    private String connectionId;
    private String callType;
    private String direction;
    
    public CallEstablishedEvent() {
        super();
        this.eventType = "CallEstablished";
    }
    
    public CallEstablishedEvent(String callId, String deviceId, String callingParty, String calledParty) {
        super(callId, deviceId, "CallEstablished");
        this.callingParty = callingParty;
        this.calledParty = calledParty;
    }
    
    // Getters and Setters
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
    
    public String getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getCallType() {
        return callType;
    }
    
    public void setCallType(String callType) {
        this.callType = callType;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    @Override
    public String toString() {
        return "CallEstablishedEvent{" +
                "callId='" + callId + '\'' +
                ", callingParty='" + callingParty + '\'' +
                ", calledParty='" + calledParty + '\'' +
                ", connectionId='" + connectionId + '\'' +
                ", callType='" + callType + '\'' +
                ", direction='" + direction + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
