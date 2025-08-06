package com.example.cti.events;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * ECMA-269 CSTA Phase III CallClearedEvent
 * 
 * From ECMA-269 Section 17.2.2, Figure 17-31 "Call Cleared Event":
 * "This event indicates that all devices have been removed from an existing call. 
 * The call no longer exists within the switching sub-domain."
 * 
 * Common situations that generate this event include:
 * - After the last remaining device disconnects from the call
 * - All devices in a call are immediately disconnected
 * - The computing function issues a successful ClearCall service request
 * 
 * Avaya Documentation Reference:
 * @see <a href="https://support.avaya.com/elmodocs2/cmapi/docs/xml/ch/ecma/csta/binding/CallClearedEvent.html">Avaya CallClearedEvent</a>
 * 
 * ECMA-269 Standard Reference:
 * @see <a href="https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/">ECMA-269 Section 17.2.2</a>
 */
public class CallClearedEvent extends CTIEvent {
    
    /**
     * Cleared Call ID (CSTA compliant)
     */
    private String clearedCallId;
    
    /**
     * Local Connection Information
     */
    private LocalConnectionState localConnectionInfo;
    
    /**
     * Cause
     */
    private CSTACause cause;
    
    // Additional fields for enhanced call tracking
    private String disconnectReason;
    private long callDuration;
    private String releasingDevice;
    
    /**
     * Default constructor
     */
    public CallClearedEvent() {
        super();
        this.eventType = "CallCleared";
    }
    
    /**
     * Constructor with call ID and disconnect reason
     * 
     * @param callId Call ID
     * @param disconnectReason Disconnect reason
     */
    public CallClearedEvent(String callId, String disconnectReason) {
        super();
        this.callId = callId;
        this.eventType = "CallCleared";
        this.clearedCallId = callId;
        this.disconnectReason = disconnectReason;
        this.cause = mapDisconnectReasonToCause(disconnectReason);
    }
    
    /**
     * Constructor with call ID, device ID, and disconnect reason
     * 
     * @param callId Call ID
     * @param deviceId Device ID
     * @param disconnectReason Disconnect reason
     */
    public CallClearedEvent(String callId, String deviceId, String disconnectReason) {
        super(callId, deviceId, "CallCleared");
        this.clearedCallId = callId;
        this.releasingDevice = deviceId;
        this.disconnectReason = disconnectReason;
        this.cause = mapDisconnectReasonToCause(disconnectReason);
    }
    
    /**
     * CSTA compliant constructor
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param clearedCallId Call that has been cleared
     * @param cause Cause of the call being cleared
     */
    public CallClearedEvent(String monitorCrossRefID, String clearedCallId, CSTACause cause) {
        super();
        this.callId = clearedCallId;
        this.eventType = "CallCleared";
        this.clearedCallId = clearedCallId;
        this.cause = cause;
    }
    
    /**
     * Map legacy disconnect reason to CSTA cause
     */
    private CSTACause mapDisconnectReasonToCause(String disconnectReason) {
        if (disconnectReason == null) return CSTACause.NORMAL;
        
        return switch (disconnectReason.toLowerCase()) {
            case "normal", "user_disconnect", "hangup" -> CSTACause.NORMAL;
            case "busy" -> CSTACause.BUSY;
            case "no_answer", "timeout" -> CSTACause.NO_ANSWER;
            case "network_error", "network_failure" -> CSTACause.NETWORK_OUT_OF_ORDER;
            case "rejected", "call_rejected" -> CSTACause.CALL_REJECTED;
            default -> CSTACause.NORMAL;
        };
    }
    
    // Getters and Setters
    
    public String getClearedCallId() {
        return clearedCallId;
    }
    
    public void setClearedCallId(String clearedCallId) {
        this.clearedCallId = clearedCallId;
    }
    
    public LocalConnectionState getLocalConnectionInfo() {
        return localConnectionInfo;
    }
    
    public void setLocalConnectionInfo(LocalConnectionState localConnectionInfo) {
        this.localConnectionInfo = localConnectionInfo;
    }
    
    public CSTACause getCause() {
        return cause;
    }
    
    public void setCause(CSTACause cause) {
        this.cause = cause;
    }
    
    public String getDisconnectReason() {
        return disconnectReason != null ? disconnectReason : 
               (cause != null ? cause.name() : "UNKNOWN");
    }
    
    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
        this.cause = mapDisconnectReasonToCause(disconnectReason);
    }
    
    public long getCallDuration() {
        return callDuration;
    }
    
    public void setCallDuration(long callDuration) {
        this.callDuration = callDuration;
    }
    
    public String getReleasingDevice() {
        return releasingDevice;
    }
    
    public void setReleasingDevice(String releasingDevice) {
        this.releasingDevice = releasingDevice;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CallClearedEvent that = (CallClearedEvent) o;
        return callDuration == that.callDuration &&
               Objects.equals(clearedCallId, that.clearedCallId) &&
               localConnectionInfo == that.localConnectionInfo &&
               cause == that.cause &&
               Objects.equals(disconnectReason, that.disconnectReason) &&
               Objects.equals(releasingDevice, that.releasingDevice);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), clearedCallId, localConnectionInfo, cause, 
                          disconnectReason, callDuration, releasingDevice);
    }
    
    @Override
    public String toString() {
        return String.format("CallClearedEvent{callId='%s', clearedCallId='%s', localConnectionInfo=%s, cause=%s, " +
                           "disconnectReason='%s', callDuration=%d, releasingDevice='%s', timestamp=%s}",
                           callId, clearedCallId, localConnectionInfo, cause, disconnectReason, callDuration, 
                           releasingDevice, timestamp);
    }
}
