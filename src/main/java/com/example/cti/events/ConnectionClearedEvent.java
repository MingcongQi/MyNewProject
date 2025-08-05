package com.example.cti.events;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * ECMA-269 CSTA Phase III ConnectionClearedEvent
 * 
 * From ECMA-269 Section 17.2.3, Figure 17-32 "Connection Cleared Event":
 * "This event indicates that a specific connection within a call has been cleared.
 * The connection no longer exists, but the call may continue with other connections."
 * 
 * Common situations that generate this event include:
 * - A device disconnects from a multi-party call
 * - A connection is dropped due to network issues
 * - A device is removed from a conference call
 * - A transfer operation completes and the transferring device disconnects
 * 
 * Avaya Documentation Reference:
 * @see <a href="https://support.avaya.com/elmodocs2/cmapi/docs/xml/ch/ecma/csta/binding/ConnectionClearedEvent.html">Avaya ConnectionClearedEvent</a>
 * 
 * ECMA-269 Standard Reference:
 * @see <a href="https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/">ECMA-269 Section 17.2.3</a>
 */
@XmlRootElement(name = "ConnectionClearedEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConnectionClearedEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "droppedConnection",
    "releasingDevice",
    "localConnectionInfo",
    "cause"
})
public class ConnectionClearedEvent extends CSTAEvent {
    
    /**
     * Dropped Connection
     * From ECMA-269: "droppedConnection identifies the connection that has been cleared"
     */
    @XmlElement(name = "droppedConnection", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private ReleasedConnection droppedConnection;
    
    /**
     * Releasing Device
     * From ECMA-269: "releasingDevice identifies the device that released the connection"
     */
    @XmlElement(name = "releasingDevice", namespace = CSTA_NAMESPACE)
    private ReleasingDevice releasingDevice;
    
    /**
     * Local Connection Information
     * From ECMA-269: "localConnectionInfo provides additional information about the connection"
     */
    @XmlElement(name = "localConnectionInfo", namespace = CSTA_NAMESPACE)
    private LocalConnectionState localConnectionInfo;
    
    /**
     * Cause
     * From ECMA-269: "cause indicates the reason for the connection being cleared"
     */
    @XmlElement(name = "cause", namespace = CSTA_NAMESPACE)
    private CSTACause cause;
    
    /**
     * Default constructor for JAXB
     */
    public ConnectionClearedEvent() {
        super();
    }
    
    /**
     * Constructor with required fields
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param droppedConnection Connection that has been cleared
     */
    public ConnectionClearedEvent(String monitorCrossRefID, ReleasedConnection droppedConnection) {
        super(monitorCrossRefID);
        this.droppedConnection = droppedConnection;
    }
    
    /**
     * Constructor with releasing device
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param droppedConnection Connection that has been cleared
     * @param releasingDevice Device that released the connection
     * @param cause Cause of the connection being cleared
     */
    public ConnectionClearedEvent(String monitorCrossRefID, ReleasedConnection droppedConnection,
                                 ReleasingDevice releasingDevice, CSTACause cause) {
        super(monitorCrossRefID);
        this.droppedConnection = droppedConnection;
        this.releasingDevice = releasingDevice;
        this.cause = cause;
    }
    
    /**
     * Full constructor
     * 
     * @param monitorCrossRefID Monitor cross reference ID
     * @param eventTime Event time
     * @param eventSequenceNumber Event sequence number
     * @param droppedConnection Connection that has been cleared
     * @param releasingDevice Device that released the connection
     * @param localConnectionInfo Local connection information
     * @param cause Cause of the connection being cleared
     */
    public ConnectionClearedEvent(String monitorCrossRefID, Instant eventTime, Long eventSequenceNumber,
                                 ReleasedConnection droppedConnection, ReleasingDevice releasingDevice,
                                 LocalConnectionState localConnectionInfo, CSTACause cause) {
        super(monitorCrossRefID, eventTime, eventSequenceNumber);
        this.droppedConnection = droppedConnection;
        this.releasingDevice = releasingDevice;
        this.localConnectionInfo = localConnectionInfo;
        this.cause = cause;
    }
    
    @Override
    public String getCSTAEventType() {
        return "ConnectionClearedEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public int getEventPriority() {
        return 4; // Medium-high priority - connection dropped but call may continue
    }
    
    @Override
    public boolean isValid() {
        return super.isValid() && droppedConnection != null;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<ConnectionClearedEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        
        // Common CSTA event fields
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (getEventSequenceNumber() != null) {
            xml.append("<eventSequenceNumber>").append(getEventSequenceNumber()).append("</eventSequenceNumber>");
        }
        
        // ConnectionClearedEvent specific fields
        if (droppedConnection != null) {
            xml.append("<droppedConnection>");
            xml.append("<callID>").append(droppedConnection.getCallID()).append("</callID>");
            xml.append("<deviceID>").append(droppedConnection.getDeviceID()).append("</deviceID>");
            xml.append("</droppedConnection>");
        }
        
        if (releasingDevice != null) {
            xml.append("<releasingDevice>").append(releasingDevice.getValue()).append("</releasingDevice>");
        }
        
        if (localConnectionInfo != null) {
            xml.append("<localConnectionInfo>").append(localConnectionInfo.name()).append("</localConnectionInfo>");
        }
        
        if (cause != null) {
            xml.append("<cause>").append(cause.name()).append("</cause>");
        }
        
        xml.append("</ConnectionClearedEvent>");
        return xml.toString();
    }
    
    // Getters and Setters
    
    public ReleasedConnection getDroppedConnection() {
        return droppedConnection;
    }
    
    public void setDroppedConnection(ReleasedConnection droppedConnection) {
        this.droppedConnection = droppedConnection;
    }
    
    public ReleasingDevice getReleasingDevice() {
        return releasingDevice;
    }
    
    public void setReleasingDevice(ReleasingDevice releasingDevice) {
        this.releasingDevice = releasingDevice;
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
    
    /**
     * Legacy methods for backward compatibility
     */
    public String getCallId() {
        return droppedConnection != null ? droppedConnection.getCallID() : null;
    }
    
    public String getDeviceId() {
        return droppedConnection != null ? droppedConnection.getDeviceID() : null;
    }
    
    public String getReleasingDeviceId() {
        return releasingDevice != null ? releasingDevice.getValue() : null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConnectionClearedEvent that = (ConnectionClearedEvent) o;
        return Objects.equals(droppedConnection, that.droppedConnection) &&
               Objects.equals(releasingDevice, that.releasingDevice) &&
               localConnectionInfo == that.localConnectionInfo &&
               cause == that.cause;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), droppedConnection, releasingDevice, 
                          localConnectionInfo, cause);
    }
    
    @Override
    public String toString() {
        return String.format("ConnectionClearedEvent{droppedConnection=%s, releasingDevice=%s, " +
                           "localConnectionInfo=%s, cause=%s, eventTime=%s}",
                           droppedConnection, releasingDevice, localConnectionInfo, cause, getEventTime());
    }
}
