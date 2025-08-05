package com.example.cti.events;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * ECMA-269 CSTA Phase III Data Types
 * 
 * This file contains the core data types used throughout CSTA events
 * as defined in ECMA-269 standard.
 * 
 * @see <a href="https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/">ECMA-269 Standard</a>
 */
public class CSTADataTypes {
    
    // Private constructor to prevent instantiation
    private CSTADataTypes() {}
}

/**
 * Connection ID
 * From ECMA-269: "ConnectionID identifies a connection within a call"
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConnectionIDType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "callID",
    "deviceID"
})
class ConnectionID {
    
    @XmlElement(name = "callID", namespace = CSTAEvent.CSTA_NAMESPACE, required = true)
    @NotNull
    private String callID;
    
    @XmlElement(name = "deviceID", namespace = CSTAEvent.CSTA_NAMESPACE, required = true)
    @NotNull
    private String deviceID;
    
    public ConnectionID() {}
    
    public ConnectionID(String callID, String deviceID) {
        this.callID = callID;
        this.deviceID = deviceID;
    }
    
    public String getCallID() {
        return callID;
    }
    
    public void setCallID(String callID) {
        this.callID = callID;
    }
    
    public String getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionID that = (ConnectionID) o;
        return Objects.equals(callID, that.callID) && Objects.equals(deviceID, that.deviceID);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(callID, deviceID);
    }
    
    @Override
    public String toString() {
        return String.format("ConnectionID{callID='%s', deviceID='%s'}", callID, deviceID);
    }
}

/**
 * Device ID
 * From ECMA-269: "DeviceID identifies a device within the switching sub-domain"
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeviceIDType", namespace = CSTAEvent.CSTA_NAMESPACE)
class DeviceID {
    
    @XmlValue
    private String value;
    
    public DeviceID() {}
    
    public DeviceID(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceID deviceID = (DeviceID) o;
        return Objects.equals(value, deviceID.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return String.format("DeviceID{value='%s'}", value);
    }
}

/**
 * Call ID
 * From ECMA-269: "CallID identifies a call within the switching sub-domain"
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CallIDType", namespace = CSTAEvent.CSTA_NAMESPACE)
class CallID {
    
    @XmlValue
    private String value;
    
    public CallID() {}
    
    public CallID(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallID callID = (CallID) o;
        return Objects.equals(value, callID.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return String.format("CallID{value='%s'}", value);
    }
}

/**
 * Local Connection State
 * From ECMA-269: "LocalConnectionState indicates the state of a connection"
 */
enum LocalConnectionState {
    /**
     * The connection is in the null state
     */
    NULL("null"),
    
    /**
     * The connection is being initiated
     */
    INITIATE("initiate"),
    
    /**
     * The connection is alerting (ringing)
     */
    ALERTING("alerting"),
    
    /**
     * The connection is connected (active)
     */
    CONNECTED("connected"),
    
    /**
     * The connection is on hold
     */
    HOLD("hold"),
    
    /**
     * The connection is queued
     */
    QUEUED("queued"),
    
    /**
     * The connection has failed
     */
    FAIL("fail");
    
    private final String value;
    
    LocalConnectionState(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static LocalConnectionState fromValue(String value) {
        for (LocalConnectionState state : LocalConnectionState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown LocalConnectionState: " + value);
    }
}

/**
 * CSTA Cause
 * From ECMA-269: "Cause indicates the reason for an event"
 */
enum CSTACause {
    /**
     * Normal call clearing
     */
    NORMAL("normal"),
    
    /**
     * User busy
     */
    BUSY("busy"),
    
    /**
     * No answer from user
     */
    NO_ANSWER("noAnswer"),
    
    /**
     * Call rejected by user
     */
    CALL_REJECTED("callRejected"),
    
    /**
     * Number changed
     */
    NUMBER_CHANGED("numberChanged"),
    
    /**
     * Destination out of order
     */
    DESTINATION_OUT_OF_ORDER("destinationOutOfOrder"),
    
    /**
     * Invalid number format
     */
    INVALID_NUMBER_FORMAT("invalidNumberFormat"),
    
    /**
     * Network out of order
     */
    NETWORK_OUT_OF_ORDER("networkOutOfOrder"),
    
    /**
     * Temporary failure
     */
    TEMPORARY_FAILURE("temporaryFailure"),
    
    /**
     * Switching equipment congestion
     */
    SWITCHING_EQUIPMENT_CONGESTION("switchingEquipmentCongestion"),
    
    /**
     * Access information discarded
     */
    ACCESS_INFORMATION_DISCARDED("accessInformationDiscarded"),
    
    /**
     * Requested channel not available
     */
    REQUESTED_CHANNEL_NOT_AVAILABLE("requestedChannelNotAvailable"),
    
    /**
     * Pre-emption
     */
    PREEMPTION("preemption"),
    
    /**
     * Precedence call blocked
     */
    PRECEDENCE_CALL_BLOCKED("precedenceCallBlocked"),
    
    /**
     * Resource unavailable
     */
    RESOURCE_UNAVAILABLE("resourceUnavailable");
    
    private final String value;
    
    CSTACause(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static CSTACause fromValue(String value) {
        for (CSTACause cause : CSTACause.values()) {
            if (cause.value.equals(value)) {
                return cause;
            }
        }
        throw new IllegalArgumentException("Unknown CSTACause: " + value);
    }
}

/**
 * Connection Cleared Event specific data types
 */

/**
 * Released Connection
 * From ECMA-269: "ReleasedConnection identifies the connection that has been released"
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReleasedConnectionType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "callID",
    "deviceID"
})
class ReleasedConnection {
    
    @XmlElement(name = "callID", namespace = CSTAEvent.CSTA_NAMESPACE, required = true)
    @NotNull
    private String callID;
    
    @XmlElement(name = "deviceID", namespace = CSTAEvent.CSTA_NAMESPACE, required = true)
    @NotNull
    private String deviceID;
    
    public ReleasedConnection() {}
    
    public ReleasedConnection(String callID, String deviceID) {
        this.callID = callID;
        this.deviceID = deviceID;
    }
    
    public String getCallID() {
        return callID;
    }
    
    public void setCallID(String callID) {
        this.callID = callID;
    }
    
    public String getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleasedConnection that = (ReleasedConnection) o;
        return Objects.equals(callID, that.callID) && Objects.equals(deviceID, that.deviceID);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(callID, deviceID);
    }
    
    @Override
    public String toString() {
        return String.format("ReleasedConnection{callID='%s', deviceID='%s'}", callID, deviceID);
    }
}

/**
 * Releasing Device
 * From ECMA-269: "ReleasingDevice identifies the device that released the connection"
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReleasingDeviceType", namespace = CSTAEvent.CSTA_NAMESPACE)
class ReleasingDevice {
    
    @XmlValue
    private String value;
    
    public ReleasingDevice() {}
    
    public ReleasingDevice(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleasingDevice that = (ReleasingDevice) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return String.format("ReleasingDevice{value='%s'}", value);
    }
}
