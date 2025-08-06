package com.example.cti.events;

import java.util.Objects;

/**
 * Connection ID
 * From ECMA-269: "ConnectionID identifies a connection"
 */
public class ConnectionID {
    
    private String callID;
    private String deviceID;
    
    public ConnectionID() {
    }
    
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
        return Objects.equals(callID, that.callID) &&
               Objects.equals(deviceID, that.deviceID);
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
