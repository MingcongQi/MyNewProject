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
