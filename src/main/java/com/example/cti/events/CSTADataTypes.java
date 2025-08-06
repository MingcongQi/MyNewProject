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
