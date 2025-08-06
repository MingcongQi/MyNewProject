package com.example.cti.events;

/**
 * Call Delivered Event
 */
public class CallDeliveredEvent extends CTIEvent {
    
    private String alertingDevice;
    
    public CallDeliveredEvent() {
        super();
        this.eventType = "CallDelivered";
    }
    
    public CallDeliveredEvent(String callId, String deviceId, String alertingDevice) {
        super(callId, deviceId, "CallDelivered");
        this.alertingDevice = alertingDevice;
    }
    
    public String getAlertingDevice() {
        return alertingDevice;
    }
    
    public void setAlertingDevice(String alertingDevice) {
        this.alertingDevice = alertingDevice;
    }
}
