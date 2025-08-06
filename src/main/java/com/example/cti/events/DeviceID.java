package com.example.cti.events;

import java.util.Objects;

/**
 * Device ID
 * From ECMA-269: "DeviceID identifies a device"
 */
public class DeviceID {
    
    private String deviceIdentifier;
    
    public DeviceID() {
    }
    
    public DeviceID(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }
    
    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }
    
    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }
    
    // Alias method for compatibility
    public String getValue() {
        return deviceIdentifier;
    }
    
    public void setValue(String value) {
        this.deviceIdentifier = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceID deviceID = (DeviceID) o;
        return Objects.equals(deviceIdentifier, deviceID.deviceIdentifier);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(deviceIdentifier);
    }
    
    @Override
    public String toString() {
        return String.format("DeviceID{deviceIdentifier='%s'}", deviceIdentifier);
    }
}
