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
@XmlRootElement(name = "CallClearedEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CallClearedEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "clearedCall",
    "localConnectionInfo",
    "cause"
})
public class CallClearedEvent extends CSTAEvent {
    
    /**
     * Cleared Call
     * From ECMA-269: "clearedCall identifies the call that has been cleared"
     */
    @XmlElement(name = "clearedCall", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private CallID clearedCall;
    
    /**
     * Local Connection Information
     * From ECMA-269: "localConnectionInfo provides additional information about the connection"
     */
    @XmlElement(name = "localConnectionInfo", namespace = CSTA_NAMESPACE)
    private LocalConnectionState localConnectionInfo;
    
    /**
     * Cause
     * From ECMA-269: "cause indicates the reason for the call being cleared"
     */
    @XmlElement(name = "cause", namespace = CSTA_NAMESPACE)
    private CSTACause cause;
    
    // Additional fields for enhanced call tracking (not part of ECMA-269 standard)
    @XmlTransient
    private String disconnectReason;
    
    @XmlTransient
    private long callDuration;
    
    @XmlTransient
    private String releasingDevice;
    
    /**
     * Default constructor for JAXB
     */
    public CallClearedEvent() {
        super();
    }
    
    /**
     * Constructor with required fields
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param clearedCall Call that has been cleared
     */
    public CallClearedEvent(String monitorCrossRefID, CallID clearedCall) {
        super(monitorCrossRefID);
        this.clearedCall = clearedCall;
    }
    
    /**
     * Constructor with cause
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param clearedCall Call that has been cleared
     * @param cause Cause of the call being cleared
     */
    public CallClearedEvent(String monitorCrossRefID, CallID clearedCall, CSTACause cause) {
        super(monitorCrossRefID);
        this.clearedCall = clearedCall;
        this.cause = cause;
    }
    
    /**
     * Full constructor
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param eventTime Event time
     * @param eventSequenceNumber Event sequence number
     * @param clearedCall Call that has been cleared
     * @param localConnectionInfo Local connection information
     * @param cause Cause of the call being cleared
     */
    public CallClearedEvent(String monitorCrossRefID, Instant eventTime, Long eventSequenceNumber,
                           CallID clearedCall, LocalConnectionState localConnectionInfo, CSTACause cause) {
        super(monitorCrossRefID, eventTime, eventSequenceNumber);
        this.clearedCall = clearedCall;
        this.localConnectionInfo = localConnectionInfo;
        this.cause = cause;
    }
    
    /**
     * Legacy constructor for backward compatibility
     * 
     * @param callId Call ID string
     * @param deviceId Device ID string
     * @param disconnectReason Disconnect reason
     */
    public CallClearedEvent(String callId, String deviceId, String disconnectReason) {
        super("legacy-monitor");
        this.clearedCall = new CallID(callId);
        this.releasingDevice = deviceId;
        this.disconnectReason = disconnectReason;
        this.cause = mapDisconnectReasonToCause(disconnectReason);
    }
    
    @Override
    public String getCSTAEventType() {
        return "CallClearedEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public int getEventPriority() {
        return 3; // High priority - call has ended
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && clearedCall != null;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<CallClearedEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        
        // Common CSTA event fields
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (getEventSequenceNumber() != null) {
            xml.append("<eventSequenceNumber>").append(getEventSequenceNumber()).append("</eventSequenceNumber>");
        }
        
        // CallClearedEvent specific fields
        if (clearedCall != null) {
            xml.append("<clearedCall>").append(clearedCall.getValue()).append("</clearedCall>");
        }
        
        if (localConnectionInfo != null) {
            xml.append("<localConnectionInfo>").append(localConnectionInfo.name()).append("</localConnectionInfo>");
        }
        
        if (cause != null) {
            xml.append("<cause>").append(cause.name()).append("</cause>");
        }
        
        xml.append("</CallClearedEvent>");
        return xml.toString();
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
    
    public CallID getClearedCall() {
        return clearedCall;
    }
    
    public void setClearedCall(CallID clearedCall) {
        this.clearedCall = clearedCall;
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
    
    // Legacy getters for backward compatibility
    
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
    
    /**
     * Legacy method for backward compatibility
     */
    public String getCallId() {
        return clearedCall != null ? clearedCall.getValue() : null;
    }
    
    /**
     * Legacy method for backward compatibility
     */
    public Instant getTimestamp() {
        return getEventTime();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CallClearedEvent that = (CallClearedEvent) o;
        return callDuration == that.callDuration &&
               Objects.equals(clearedCall, that.clearedCall) &&
               localConnectionInfo == that.localConnectionInfo &&
               cause == that.cause &&
               Objects.equals(disconnectReason, that.disconnectReason) &&
               Objects.equals(releasingDevice, that.releasingDevice);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), clearedCall, localConnectionInfo, cause, 
                          disconnectReason, callDuration, releasingDevice);
    }
    
    @Override
    public String toString() {
        return String.format("CallClearedEvent{clearedCall=%s, localConnectionInfo=%s, cause=%s, " +
                           "disconnectReason='%s', callDuration=%d, releasingDevice='%s', eventTime=%s}",
                           clearedCall, localConnectionInfo, cause, disconnectReason, callDuration, 
                           releasingDevice, getEventTime());
    }
}
