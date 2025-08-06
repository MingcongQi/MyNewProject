package com.example.cti.events;

/**
 * Call Held Event
 */
public class CallHeldEvent extends CTIEvent {
    
    private String holdingDevice;
    
    public CallHeldEvent() {
        super();
        this.eventType = "CallHeld";
    }
    
    public CallHeldEvent(String callId, String deviceId, String holdingDevice) {
        super(callId, deviceId, "CallHeld");
        this.holdingDevice = holdingDevice;
    }
    
    public String getHoldingDevice() {
        return holdingDevice;
    }
    
    public void setHoldingDevice(String holdingDevice) {
        this.holdingDevice = holdingDevice;
    }
}
