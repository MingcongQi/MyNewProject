package com.example.lambdaclient.cti;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of a call in the CTI system
 */
public class CallState {
    private String callId;
    private String callingParty;
    private String calledParty;
    private String state;
    private long timestamp;
    private List<Transfer> transfers;
    private List<Conference> conferences;
    
    public CallState(String callId, String callingParty, String calledParty, long timestamp) {
        this.callId = callId;
        this.callingParty = callingParty;
        this.calledParty = calledParty;
        this.timestamp = timestamp;
        this.state = "INITIATED";
        this.transfers = new ArrayList<>();
        this.conferences = new ArrayList<>();
    }
    
    public String getCallId() {
        return callId;
    }
    
    public String getCallingParty() {
        return callingParty;
    }
    
    public String getCalledParty() {
        return calledParty;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public List<Transfer> getTransfers() {
        return transfers;
    }
    
    public void addTransfer(String destination, long timestamp) {
        transfers.add(new Transfer(destination, timestamp));
    }
    
    public List<Conference> getConferences() {
        return conferences;
    }
    
    public void addConference(String participant, long timestamp) {
        conferences.add(new Conference(participant, timestamp));
    }
    
    public void addConferenceParticipant(String participant, long timestamp) {
        conferences.add(new Conference(participant, timestamp));
    }
    
    public List<Conference> getConferenceParticipants() {
        return conferences;
    }
    
    // Inner classes for Transfer and Conference tracking
    public static class Transfer {
        private String destination;
        private long timestamp;
        
        public Transfer(String destination, long timestamp) {
            this.destination = destination;
            this.timestamp = timestamp;
        }
        
        public String getDestination() {
            return destination;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    public static class Conference {
        private String participant;
        private long timestamp;
        
        public Conference(String participant, long timestamp) {
            this.participant = participant;
            this.timestamp = timestamp;
        }
        
        public String getParticipant() {
            return participant;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}
