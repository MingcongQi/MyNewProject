package com.example.lambdaclient.cti.events;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * ECMA-269 CSTA Phase III DeliveredEvent
 * 
 * From ECMA-269 Section 17.2.5, Figure 17-36 "Delivered Event":
 * "This event indicates that a call is being presented to a device in either 
 * the Ringing or Entering Distribution modes of the alerting state."
 * 
 * Common situations that generate this event include:
 * - A call has been assigned to a device and that device is alerting
 * - A call has been assigned to a distribution device such as an ACD, 
 *   routing device, or hunt group
 * 
 * Avaya Documentation Reference:
 * @see <a href="https://support.avaya.com/elmodocs2/cmapi/docs/xml/ch/ecma/csta/binding/DeliveredEvent.html">Avaya DeliveredEvent</a>
 * 
 * ECMA-269 Standard Reference:
 * @see <a href="https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/">ECMA-269 Section 17.2.5</a>
 */
@XmlRootElement(name = "DeliveredEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeliveredEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "connection",
    "alertingDevice",
    "callingDevice", 
    "calledDevice",
    "lastRedirectionDevice",
    "localConnectionInfo",
    "cause"
})
public class DeliveredEvent extends CSTAEvent {
    
    /**
     * Connection Information
     * From ECMA-269: "connection identifies the connection that is alerting"
     */
    @XmlElement(name = "connection", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private ConnectionID connection;
    
    /**
     * Alerting Device
     * From ECMA-269: "alertingDevice identifies the device that is alerting"
     */
    @XmlElement(name = "alertingDevice", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private DeviceID alertingDevice;
    
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
    public DeliveredEvent() {
        super();
    }
    
    /**
     * Constructor with required fields
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param connection Connection information
     * @param alertingDevice Device that is alerting
     */
    public DeliveredEvent(String monitorCrossRefID, ConnectionID connection, DeviceID alertingDevice) {
        super(monitorCrossRefID);
        this.connection = connection;
        this.alertingDevice = alertingDevice;
    }
    
    /**
     * Full constructor
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param eventTime Event time
     * @param eventSequenceNumber Event sequence number
     * @param connection Connection information
     * @param alertingDevice Device that is alerting
     * @param callingDevice Device that originated the call
     * @param calledDevice Device that was originally called
     * @param lastRedirectionDevice Device that last redirected the call
     * @param localConnectionInfo Local connection information
     * @param cause Cause of the event
     */
    public DeliveredEvent(String monitorCrossRefID, Instant eventTime, Long eventSequenceNumber,
                         ConnectionID connection, DeviceID alertingDevice, DeviceID callingDevice,
                         DeviceID calledDevice, DeviceID lastRedirectionDevice,
                         LocalConnectionState localConnectionInfo, CSTACause cause) {
        super(monitorCrossRefID, eventTime, eventSequenceNumber);
        this.connection = connection;
        this.alertingDevice = alertingDevice;
        this.callingDevice = callingDevice;
        this.calledDevice = calledDevice;
        this.lastRedirectionDevice = lastRedirectionDevice;
        this.localConnectionInfo = localConnectionInfo;
        this.cause = cause;
    }
    
    @Override
    public String getCSTAEventType() {
        return "DeliveredEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public int getEventPriority() {
        return 2; // High priority - call is alerting
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && connection != null && alertingDevice != null;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<DeliveredEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        
        // Common CSTA event fields
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (getEventSequenceNumber() != null) {
            xml.append("<eventSequenceNumber>").append(getEventSequenceNumber()).append("</eventSequenceNumber>");
        }
        
        // DeliveredEvent specific fields
        if (connection != null) {
            xml.append("<connection>");
            xml.append("<callID>").append(connection.getCallID()).append("</callID>");
            xml.append("<deviceID>").append(connection.getDeviceID()).append("</deviceID>");
            xml.append("</connection>");
        }
        
        if (alertingDevice != null) {
            xml.append("<alertingDevice>").append(alertingDevice.getValue()).append("</alertingDevice>");
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
        
        xml.append("</DeliveredEvent>");
        return xml.toString();
    }
    
    // Getters and Setters
    
    public ConnectionID getConnection() {
        return connection;
    }
    
    public void setConnection(ConnectionID connection) {
        this.connection = connection;
    }
    
    public DeviceID getAlertingDevice() {
        return alertingDevice;
    }
    
    public void setAlertingDevice(DeviceID alertingDevice) {
        this.alertingDevice = alertingDevice;
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
        DeliveredEvent that = (DeliveredEvent) o;
        return Objects.equals(connection, that.connection) &&
               Objects.equals(alertingDevice, that.alertingDevice) &&
               Objects.equals(callingDevice, that.callingDevice) &&
               Objects.equals(calledDevice, that.calledDevice) &&
               Objects.equals(lastRedirectionDevice, that.lastRedirectionDevice) &&
               localConnectionInfo == that.localConnectionInfo &&
               cause == that.cause;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), connection, alertingDevice, callingDevice, 
                          calledDevice, lastRedirectionDevice, localConnectionInfo, cause);
    }
    
    @Override
    public String toString() {
        return String.format("DeliveredEvent{connection=%s, alertingDevice=%s, callingDevice=%s, " +
                           "calledDevice=%s, lastRedirectionDevice=%s, localConnectionInfo=%s, cause=%s}",
                           connection, alertingDevice, callingDevice, calledDevice, 
                           lastRedirectionDevice, localConnectionInfo, cause);
    }
}
