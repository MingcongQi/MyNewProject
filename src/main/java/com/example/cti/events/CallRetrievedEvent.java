package com.example.cti.events;

/**
 * Call Retrieved Event
 */
public class CallRetrievedEvent extends CTIEvent {
    
    private String retrievingDevice;
    
    public CallRetrievedEvent() {
        super();
        this.eventType = "CallRetrieved";
    }
    
    public CallRetrievedEvent(String callId, String deviceId, String retrievingDevice) {
        super(callId, deviceId, "CallRetrieved");
        this.retrievingDevice = retrievingDevice;
    }
    
    public String getRetrievingDevice() {
        return retrievingDevice;
    }
    
    public void setRetrievingDevice(String retrievingDevice) {
        this.retrievingDevice = retrievingDevice;
    }
}
