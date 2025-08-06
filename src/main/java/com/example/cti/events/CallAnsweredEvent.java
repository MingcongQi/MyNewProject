package com.example.cti.events;

/**
 * Call Answered Event
 */
public class CallAnsweredEvent extends CTIEvent {
    
    private String answeringDevice;
    
    public CallAnsweredEvent() {
        super();
        this.eventType = "CallAnswered";
    }
    
    public CallAnsweredEvent(String callId, String deviceId, String answeringDevice) {
        super(callId, deviceId, "CallAnswered");
        this.answeringDevice = answeringDevice;
    }
    
    public String getAnsweringDevice() {
        return answeringDevice;
    }
    
    public void setAnsweringDevice(String answeringDevice) {
        this.answeringDevice = answeringDevice;
    }
}
