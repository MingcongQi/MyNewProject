package com.example.lambdaclient.cti.events;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * ECMA-269 CSTA Phase III EstablishedEvent
 * 
 * From ECMA-269 Section 17.2.8, Figure 17-40 "Established Event":
 * "This event indicates that a device has answered or has been connected to a call."
 * 
 * Common situations that generate this event include:
 * - A call has been answered at a device (user has manually gone off-hook)
 * - The AnswerCall service has been successfully invoked
 * - A call has been picked up by another device
 * - A device has been added to an existing call
 * 
 * Avaya Documentation Reference:
 * @see <a href="https://support.avaya.com/elmodocs2/cmapi/docs/xml/ch/ecma/csta/binding/EstablishedEvent.html">Avaya EstablishedEvent</a>
 * 
 * ECMA-269 Standard Reference:
 * @see <a href="https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/">ECMA-269 Section 17.2.8</a>
 */
@XmlRootElement(name = "EstablishedEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EstablishedEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "establishedConnection",
    "answeringDevice",
    "callingDevice",
    "calledDevice",
    "lastRedirectionDevice",
    "localConnectionInfo",
    "cause"
})
public class EstablishedEvent extends CSTAEvent {
    
    /**
     * Established Connection
     * From ECMA-269: "establishedConnection identifies the connection that has been established"
     */
    @XmlElement(name = "establishedConnection", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private ConnectionID establishedConnection;
    
    /**
     * Answering Device
     * From ECMA-269: "answeringDevice identifies the device that answered the call"
     */
    @XmlElement(name = "answeringDevice", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private DeviceID answeringDevice;
    
    /**
     * Calling Device
     * From ECMA-269: "callingDevice identifies the device that originated the call"
     */
    @XmlElement(name = "callingDevice", namespace = CSTA_NAMESPACE)
    private DeviceID callingDevice;
    
    /**
     * Called Device
     * From ECMA-269: "calledDevice identifies the device that was originally called"
     */
    @XmlElement(name = "calledDevice", namespace = CSTA_NAMESPACE)
    private DeviceID calledDevice;
    
    /**
     * Last Redirection Device
     * From ECMA-269: "lastRedirectionDevice identifies the device that last redirected the call"
     */
    @XmlElement(name = "lastRedirectionDevice", namespace = CSTA_NAMESPACE)
    private DeviceID lastRedirectionDevice;
    
    /**
     * Local Connection Information
     * From ECMA-269: "localConnectionInfo provides additional information about the connection"
     */
    @XmlElement(name = "localConnectionInfo", namespace = CSTA_NAMESPACE)
    private LocalConnectionState localConnectionInfo;
    
    /**
     * Cause
     * From ECMA-269: "cause indicates the reason for the event"
     */
    @XmlElement(name = "cause", namespace = CSTA_NAMESPACE)
    private CSTACause cause;
    
    /**
     * Default constructor for JAXB
     */
    public EstablishedEvent() {
        super();
    }
    
    /**
     * Constructor with required fields
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param establishedConnection Connection that has been established
     * @param answeringDevice Device that answered the call
     */
    public EstablishedEvent(String monitorCrossRefID, ConnectionID establishedConnection, DeviceID answeringDevice) {
        super(monitorCrossRefID);
        this.establishedConnection = establishedConnection;
        this.answeringDevice = answeringDevice;
    }
    
    /**
     * Full constructor
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param eventTime Event time
     * @param eventSequenceNumber Event sequence number
     * @param establishedConnection Connection that has been established
     * @param answeringDevice Device that answered the call
     * @param callingDevice Device that originated the call
     * @param calledDevice Device that was originally called
     * @param lastRedirectionDevice Device that last redirected the call
     * @param localConnectionInfo Local connection information
     * @param cause Cause of the event
     */
    public EstablishedEvent(String monitorCrossRefID, Instant eventTime, Long eventSequenceNumber,
                           ConnectionID establishedConnection, DeviceID answeringDevice, DeviceID callingDevice,
                           DeviceID calledDevice, DeviceID lastRedirectionDevice,
                           LocalConnectionState localConnectionInfo, CSTACause cause) {
        super(monitorCrossRefID, eventTime, eventSequenceNumber);
        this.establishedConnection = establishedConnection;
        this.answeringDevice = answeringDevice;
        this.callingDevice = callingDevice;
        this.calledDevice = calledDevice;
        this.lastRedirectionDevice = lastRedirectionDevice;
        this.localConnectionInfo = localConnectionInfo;
        this.cause = cause;
    }
    
    @Override
    public String getCSTAEventType() {
        return "EstablishedEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public int getEventPriority() {
        return 1; // Highest priority - call is connected
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && establishedConnection != null && answeringDevice != null;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<EstablishedEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        
        // Common CSTA event fields
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (getEventSequenceNumber() != null) {
            xml.append("<eventSequenceNumber>").append(getEventSequenceNumber()).append("</eventSequenceNumber>");
        }
        
        // EstablishedEvent specific fields
        if (establishedConnection != null) {
            xml.append("<establishedConnection>");
            xml.append("<callID>").append(establishedConnection.getCallID()).append("</callID>");
            xml.append("<deviceID>").append(establishedConnection.getDeviceID()).append("</deviceID>");
            xml.append("</establishedConnection>");
        }
        
        if (answeringDevice != null) {
            xml.append("<answeringDevice>").append(answeringDevice.getValue()).append("</answeringDevice>");
        }
        
        if (callingDevice != null) {
            xml.append("<callingDevice>").append(callingDevice.getValue()).append("</callingDevice>");
        }
        
        if (calledDevice != null) {
            xml.append("<calledDevice>").append(calledDevice.getValue()).append("</calledDevice>");
        }
        
        if (lastRedirectionDevice != null) {
            xml.append("<lastRedirectionDevice>").append(lastRedirectionDevice.getValue()).append("</lastRedirectionDevice>");
        }
        
        if (localConnectionInfo != null) {
            xml.append("<localConnectionInfo>").append(localConnectionInfo.name()).append("</localConnectionInfo>");
        }
        
        if (cause != null) {
            xml.append("<cause>").append(cause.name()).append("</cause>");
        }
        
        xml.append("</EstablishedEvent>");
        return xml.toString();
    }
    
    // Getters and Setters
    
    public ConnectionID getEstablishedConnection() {
        return establishedConnection;
    }
    
    public void setEstablishedConnection(ConnectionID establishedConnection) {
        this.establishedConnection = establishedConnection;
    }
    
    public DeviceID getAnsweringDevice() {
        return answeringDevice;
    }
    
    public void setAnsweringDevice(DeviceID answeringDevice) {
        this.answeringDevice = answeringDevice;
    }
    
    public DeviceID getCallingDevice() {
        return callingDevice;
    }
    
    public void setCallingDevice(DeviceID callingDevice) {
        this.callingDevice = callingDevice;
    }
    
    public DeviceID getCalledDevice() {
        return calledDevice;
    }
    
    public void setCalledDevice(DeviceID calledDevice) {
        this.calledDevice = calledDevice;
    }
    
    public DeviceID getLastRedirectionDevice() {
        return lastRedirectionDevice;
    }
    
    public void setLastRedirectionDevice(DeviceID lastRedirectionDevice) {
        this.lastRedirectionDevice = lastRedirectionDevice;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EstablishedEvent that = (EstablishedEvent) o;
        return Objects.equals(establishedConnection, that.establishedConnection) &&
               Objects.equals(answeringDevice, that.answeringDevice) &&
               Objects.equals(callingDevice, that.callingDevice) &&
               Objects.equals(calledDevice, that.calledDevice) &&
               Objects.equals(lastRedirectionDevice, that.lastRedirectionDevice) &&
               localConnectionInfo == that.localConnectionInfo &&
               cause == that.cause;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), establishedConnection, answeringDevice, callingDevice,
                          calledDevice, lastRedirectionDevice, localConnectionInfo, cause);
    }
    
    @Override
    public String toString() {
        return String.format("EstablishedEvent{establishedConnection=%s, answeringDevice=%s, callingDevice=%s, " +
                           "calledDevice=%s, lastRedirectionDevice=%s, localConnectionInfo=%s, cause=%s}",
                           establishedConnection, answeringDevice, callingDevice, calledDevice,
                           lastRedirectionDevice, localConnectionInfo, cause);
    }
}
