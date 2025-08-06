package com.example.cti.events;

import java.util.Objects;

/**
 * Call ID
 * From ECMA-269: "CallID identifies a call"
 */
public class CallID {
    
    private String callIdentifier;
    
    public CallID() {
    }
    
    public CallID(String callIdentifier) {
        this.callIdentifier = callIdentifier;
    }
    
    public String getCallIdentifier() {
        return callIdentifier;
    }
    
    public void setCallIdentifier(String callIdentifier) {
        this.callIdentifier = callIdentifier;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallID callID = (CallID) o;
        return Objects.equals(callIdentifier, callID.callIdentifier);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(callIdentifier);
    }
    
    @Override
    public String toString() {
        return String.format("CallID{callIdentifier='%s'}", callIdentifier);
    }
}
