package com.example.cti.events;

/**
 * CSTA Cause
 * From ECMA-269: "Cause indicates the reason for an event"
 * 
 * This enum represents the standard CSTA cause codes as defined in the
 * ECMA-269 international standard for Computer Supported Telecommunications Applications.
 */
public enum CSTACause {
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
