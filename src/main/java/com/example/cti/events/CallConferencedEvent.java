package com.example.cti.events;

import java.time.Instant;
import java.util.Objects;

/**
 * Call Conferenced Event
 * 
 * Represents a call that has been conferenced with other parties.
 * This event is triggered when a call is joined with other calls to form a conference.
 */
public class CallConferencedEvent extends CTIEvent {
    
    private String conferenceParticipant;
    private String conferencingDevice;
    private String conferenceId;
    private int participantCount;
    
    public CallConferencedEvent() {
        super();
        this.eventType = "CallConferenced";
    }
    
    public CallConferencedEvent(String callId, String deviceId, String conferenceParticipant) {
        super(callId, deviceId, "CallConferenced");
        this.conferenceParticipant = conferenceParticipant;
        this.timestamp = Instant.now();
    }
    
    public CallConferencedEvent(String callId, String deviceId, String conferenceParticipant, String conferenceId) {
        super(callId, deviceId, "CallConferenced");
        this.conferenceParticipant = conferenceParticipant;
        this.conferenceId = conferenceId;
        this.timestamp = Instant.now();
    }
    
    // Getters and Setters
    
    public String getConferenceParticipant() {
        return conferenceParticipant;
    }
    
    public void setConferenceParticipant(String conferenceParticipant) {
        this.conferenceParticipant = conferenceParticipant;
    }
    
    public String getConferencingDevice() {
        return conferencingDevice;
    }
    
    public void setConferencingDevice(String conferencingDevice) {
        this.conferencingDevice = conferencingDevice;
    }
    
    public String getConferenceId() {
        return conferenceId;
    }
    
    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }
    
    public int getParticipantCount() {
        return participantCount;
    }
    
    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CallConferencedEvent that = (CallConferencedEvent) o;
        return participantCount == that.participantCount &&
               Objects.equals(conferenceParticipant, that.conferenceParticipant) &&
               Objects.equals(conferencingDevice, that.conferencingDevice) &&
               Objects.equals(conferenceId, that.conferenceId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), conferenceParticipant, conferencingDevice, 
                          conferenceId, participantCount);
    }
    
    @Override
    public String toString() {
        return String.format("CallConferencedEvent{callId='%s', conferenceParticipant='%s', " +
                           "conferencingDevice='%s', conferenceId='%s', participantCount=%d, timestamp=%s}",
                           callId, conferenceParticipant, conferencingDevice, conferenceId, 
                           participantCount, timestamp);
    }
}
