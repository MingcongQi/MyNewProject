package com.example.lambdaclient.cti.events;

/**
 * Call Cleared Event
 * 
 * Represents a call that has been cleared/disconnected
 */
public class CallClearedEvent extends CTIEvent {
    
    private String disconnectReason;
    private String releasingDevice;
    private long callDuration;
    
    public CallClearedEvent() {
        super();
        this.eventType = "CallCleared";
    }
    
    public CallClearedEvent(String callId, String deviceId, String disconnectReason) {
        super(callId, deviceId, "CallCleared");
        this.disconnectReason = disconnectReason;
    }
    
    // Getters and Setters
    public String getDisconnectReason() {
        return disconnectReason;
    }
    
    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }
    
    public String getReleasingDevice() {
        return releasingDevice;
    }
    
    public void setReleasingDevice(String releasingDevice) {
        this.releasingDevice = releasingDevice;
    }
    
    public long getCallDuration() {
        return callDuration;
    }
    
    public void setCallDuration(long callDuration) {
        this.callDuration = callDuration;
    }
    
    @Override
    public String toString() {
        return "CallClearedEvent{" +
                "callId='" + callId + '\'' +
                ", disconnectReason='" + disconnectReason + '\'' +
                ", releasingDevice='" + releasingDevice + '\'' +
                ", callDuration=" + callDuration +
                ", timestamp=" + timestamp +
                '}';
    }
}
