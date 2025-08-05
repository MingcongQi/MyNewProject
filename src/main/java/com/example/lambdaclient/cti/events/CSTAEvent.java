package com.example.lambdaclient.cti.events;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * ECMA-269 CSTA Phase III Base Event Class
 * 
 * This class represents the base structure for all CSTA events as defined in:
 * - ECMA-269: "Services for Computer Supported Telecommunications Applications (CSTA) Phase III"
 * - Section 17: "Call Control Services and Events"
 * 
 * All CSTA events share common elements:
 * - monitorCrossRefID: Cross-reference identifier for the monitor
 * - eventTime: Time when the event occurred
 * - eventSequenceNumber: Sequence number for event ordering
 * 
 * XML Namespace: http://www.ecma-international.org/standards/ecma-269/csta
 * 
 * @see <a href="https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/">ECMA-269 Standard</a>
 */
@XmlRootElement(namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
    DeliveredEvent.class,
    EstablishedEvent.class,
    CallClearedEvent.class,
    ConnectionClearedEvent.class,
    DivertedEvent.class,
    TransferredEvent.class,
    ConferencedEvent.class,
    QueuedEvent.class
})
public abstract class CSTAEvent {
    
    /**
     * ECMA-269 CSTA Phase III XML Namespace
     */
    public static final String CSTA_NAMESPACE = "http://www.ecma-international.org/standards/ecma-269/csta";
    
    /**
     * Monitor Cross Reference ID
     * From ECMA-269 Section 17.1: "monitorCrossRefID identifies the monitor 
     * that detected the event"
     */
    @XmlElement(name = "monitorCrossRefID", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private String monitorCrossRefID;
    
    /**
     * Event Time
     * From ECMA-269: "eventTime indicates when the event occurred"
     */
    @XmlElement(name = "eventTime", namespace = CSTA_NAMESPACE)
    private Instant eventTime;
    
    /**
     * Event Sequence Number
     * From ECMA-269: "eventSequenceNumber provides a sequence number for events"
     */
    @XmlElement(name = "eventSequenceNumber", namespace = CSTA_NAMESPACE)
    private Long eventSequenceNumber;
    
    /**
     * Event Class (for internal processing)
     */
    @XmlTransient
    private String eventClass;
    
    /**
     * Default constructor required for JAXB
     */
    protected CSTAEvent() {
        this.eventTime = Instant.now();
    }
    
    /**
     * Constructor with monitor cross reference ID
     * 
     * @param monitorCrossRefID The monitor cross reference identifier
     */
    protected CSTAEvent(String monitorCrossRefID) {
        this();
        this.monitorCrossRefID = monitorCrossRefID;
        this.eventClass = this.getClass().getSimpleName();
    }
    
    /**
     * Constructor with all common fields
     * 
     * @param monitorCrossRefID The monitor cross reference identifier
     * @param eventTime The time when the event occurred
     * @param eventSequenceNumber The sequence number for event ordering
     */
    protected CSTAEvent(String monitorCrossRefID, Instant eventTime, Long eventSequenceNumber) {
        this.monitorCrossRefID = monitorCrossRefID;
        this.eventTime = eventTime != null ? eventTime : Instant.now();
        this.eventSequenceNumber = eventSequenceNumber;
        this.eventClass = this.getClass().getSimpleName();
    }
    
    // Getters and Setters
    
    public String getMonitorCrossRefID() {
        return monitorCrossRefID;
    }
    
    public void setMonitorCrossRefID(String monitorCrossRefID) {
        this.monitorCrossRefID = monitorCrossRefID;
    }
    
    public Instant getEventTime() {
        return eventTime;
    }
    
    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }
    
    public Long getEventSequenceNumber() {
        return eventSequenceNumber;
    }
    
    public void setEventSequenceNumber(Long eventSequenceNumber) {
        this.eventSequenceNumber = eventSequenceNumber;
    }
    
    public String getEventClass() {
        return eventClass;
    }
    
    /**
     * Get the CSTA event type name as defined in ECMA-269
     * This should be overridden by concrete event classes
     * 
     * @return The CSTA event type name
     */
    public abstract String getCSTAEventType();
    
    /**
     * Get the CSTA event category (Call Control, Physical Device, etc.)
     * From ECMA-269 Section 17: Event categories
     * 
     * @return The CSTA event category
     */
    public abstract CSTAEventCategory getEventCategory();
    
    /**
     * Validate the event according to ECMA-269 standards
     * 
     * @return true if the event is valid, false otherwise
     */
    public boolean isValid() {
        return monitorCrossRefID != null && !monitorCrossRefID.trim().isEmpty()
               && eventTime != null;
    }
    
    /**
     * Convert event to ECMA-269 compliant XML representation
     * 
     * @return XML string representation of the event
     */
    public abstract String toXML();
    
    /**
     * Get event priority for processing order
     * From ECMA-269: Some events have higher priority than others
     * 
     * @return Event priority (1 = highest, 10 = lowest)
     */
    public int getEventPriority() {
        return 5; // Default priority
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSTAEvent cstaEvent = (CSTAEvent) o;
        return Objects.equals(monitorCrossRefID, cstaEvent.monitorCrossRefID) &&
               Objects.equals(eventTime, cstaEvent.eventTime) &&
               Objects.equals(eventSequenceNumber, cstaEvent.eventSequenceNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(monitorCrossRefID, eventTime, eventSequenceNumber);
    }
    
    @Override
    public String toString() {
        return String.format("%s{monitorCrossRefID='%s', eventTime=%s, eventSequenceNumber=%d}",
                getClass().getSimpleName(), monitorCrossRefID, eventTime, eventSequenceNumber);
    }
    
    /**
     * ECMA-269 Event Categories
     * From Section 17: "Events are categorized into different types"
     */
    public enum CSTAEventCategory {
        CALL_CONTROL("Call Control Events"),
        PHYSICAL_DEVICE("Physical Device Events"),
        LOGICAL_DEVICE("Logical Device Events"),
        CALL_ASSOCIATED("Call Associated Events"),
        MEDIA_ATTACHMENT("Media Attachment Events"),
        VOICE_UNIT("Voice Unit Events"),
        MAINTENANCE("Maintenance Events"),
        PRIVATE("Private Events");
        
        private final String description;
        
        CSTAEventCategory(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
