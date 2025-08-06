package com.example.cti.events;

/**
 * Local Connection State
 * From ECMA-269: "LocalConnectionState indicates the state of a connection"
 * 
 * This enum represents the standard CSTA connection states as defined in the
 * ECMA-269 international standard for Computer Supported Telecommunications Applications.
 */
public enum LocalConnectionState {
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
