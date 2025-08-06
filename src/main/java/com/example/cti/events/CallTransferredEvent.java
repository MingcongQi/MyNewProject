package com.example.cti.events;

/**
 * Call Transferred Event
 */
public class CallTransferredEvent extends CTIEvent {
    
    private String transferredTo;
    private String transferredFrom;
    private String transferType;
    
    public CallTransferredEvent() {
        super();
        this.eventType = "CallTransferred";
    }
    
    public String getTransferredTo() {
        return transferredTo;
    }
    
    public void setTransferredTo(String transferredTo) {
        this.transferredTo = transferredTo;
    }
    
    public String getTransferredFrom() {
        return transferredFrom;
    }
    
    public void setTransferredFrom(String transferredFrom) {
        this.transferredFrom = transferredFrom;
    }
    
    public String getTransferType() {
        return transferType;
    }
    
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}

/**
 * Call Conferenced Event
 */
class CallConferencedEvent extends CTIEvent {
    
    private String conferenceParticipant;
    private String conferenceId;
    
    public CallConferencedEvent() {
        super();
        this.eventType = "CallConferenced";
    }
    
    public String getConferenceParticipant() {
        return conferenceParticipant;
    }
    
    public void setConferenceParticipant(String conferenceParticipant) {
        this.conferenceParticipant = conferenceParticipant;
    }
    
    public String getConferenceId() {
        return conferenceId;
    }
    
    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }
}

/**
 * Call Held Event
 */
class CallHeldEvent extends CTIEvent {
    
    private String holdingDevice;
    
    public CallHeldEvent() {
        super();
        this.eventType = "CallHeld";
    }
    
    public String getHoldingDevice() {
        return holdingDevice;
    }
    
    public void setHoldingDevice(String holdingDevice) {
        this.holdingDevice = holdingDevice;
    }
}

/**
 * Call Retrieved Event
 */
class CallRetrievedEvent extends CTIEvent {
    
    private String retrievingDevice;
    
    public CallRetrievedEvent() {
        super();
        this.eventType = "CallRetrieved";
    }
    
    public String getRetrievingDevice() {
        return retrievingDevice;
    }
    
    public void setRetrievingDevice(String retrievingDevice) {
        this.retrievingDevice = retrievingDevice;
    }
}

/**
 * Call Delivered Event
 */
class CallDeliveredEvent extends CTIEvent {
    
    private String alertingDevice;
    
    public CallDeliveredEvent() {
        super();
        this.eventType = "CallDelivered";
    }
    
    public String getAlertingDevice() {
        return alertingDevice;
    }
    
    public void setAlertingDevice(String alertingDevice) {
        this.alertingDevice = alertingDevice;
    }
}

/**
 * Call Answered Event
 */
class CallAnsweredEvent extends CTIEvent {
    
    private String answeringDevice;
    
    public CallAnsweredEvent() {
        super();
        this.eventType = "CallAnswered";
    }
    
    public String getAnsweringDevice() {
        return answeringDevice;
    }
    
    public void setAnsweringDevice(String answeringDevice) {
        this.answeringDevice = answeringDevice;
    }
}
