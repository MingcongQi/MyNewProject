package com.example.lambdaclient.cti;

import java.util.ArrayList;
import java.util.List;

/**
 * Call State tracking
 * 
 * This class tracks the state of active calls including transfers, conferences, and timing
 */
public class CallState {
    
    private String callId;
    private String callingParty;
    private String calledParty;
    private String state;
    private long startTime;
    private long endTime;
    private long lastUpdated;
    private String disconnectReason;
    
    // Transfer tracking
    private List<TransferInfo> transfers;
    
    // Conference tracking
    private List<ConferenceParticipant> conferenceParticipants;
    
    public CallState(String callId, String callingParty, String calledParty, long startTime) {
        this.callId = callId;
        this.callingParty = callingParty;
        this.calledParty = calledParty;
        this.startTime = startTime;
        this.lastUpdated = startTime;
        this.state = "INITIATED";
        this.transfers = new ArrayList<>();
        this.conferenceParticipants = new ArrayList<>();
    }
    
    /**
     * Add transfer information
     */
    public void addTransfer(String transferredTo, long transferTime) {
        transfers.add(new TransferInfo(transferredTo, transferTime));
        this.lastUpdated = transferTime;
    }
    
    /**
     * Add conference participant
     */
    public void addConferenceParticipant(String participant, long joinTime) {
        conferenceParticipants.add(new ConferenceParticipant(participant, joinTime));
        this.lastUpdated = joinTime;
    }
    
    /**
     * Get call duration in milliseconds
     */
    public long getDuration() {
        if (endTime > 0) {
            return endTime - startTime;
        } else {
            return System.currentTimeMillis() - startTime;
        }
    }
    
    // Getters and Setters
    public String getCallId() {
        return callId;
    }
    
    public void setCallId(String callId) {
        this.callId = callId;
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
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.lastUpdated = endTime;
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getDisconnectReason() {
        return disconnectReason;
    }
    
    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }
    
    public List<TransferInfo> getTransfers() {
        return new ArrayList<>(transfers);
    }
    
    public List<ConferenceParticipant> getConferenceParticipants() {
        return new ArrayList<>(conferenceParticipants);
    }
    
    @Override
    public String toString() {
        return "CallState{" +
                "callId='" + callId + '\'' +
                ", callingParty='" + callingParty + '\'' +
                ", calledParty='" + calledParty + '\'' +
                ", state='" + state + '\'' +
                ", duration=" + getDuration() +
                ", transfers=" + transfers.size() +
                ", conferenceParticipants=" + conferenceParticipants.size() +
                '}';
    }
    
    /**
     * Transfer Information
     */
    public static class TransferInfo {
        private String transferredTo;
        private long transferTime;
        
        public TransferInfo(String transferredTo, long transferTime) {
            this.transferredTo = transferredTo;
            this.transferTime = transferTime;
        }
        
        public String getTransferredTo() {
            return transferredTo;
        }
        
        public long getTransferTime() {
            return transferTime;
        }
        
        @Override
        public String toString() {
            return "TransferInfo{" +
                    "transferredTo='" + transferredTo + '\'' +
                    ", transferTime=" + transferTime +
                    '}';
        }
    }
    
    /**
     * Conference Participant
     */
    public static class ConferenceParticipant {
        private String participant;
        private long joinTime;
        private long leaveTime;
        
        public ConferenceParticipant(String participant, long joinTime) {
            this.participant = participant;
            this.joinTime = joinTime;
        }
        
        public String getParticipant() {
            return participant;
        }
        
        public long getJoinTime() {
            return joinTime;
        }
        
        public long getLeaveTime() {
            return leaveTime;
        }
        
        public void setLeaveTime(long leaveTime) {
            this.leaveTime = leaveTime;
        }
        
        @Override
        public String toString() {
            return "ConferenceParticipant{" +
                    "participant='" + participant + '\'' +
                    ", joinTime=" + joinTime +
                    ", leaveTime=" + leaveTime +
                    '}';
        }
    }
}
