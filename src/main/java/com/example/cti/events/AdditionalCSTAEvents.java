package com.example.cti.events;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * Additional ECMA-269 CSTA Phase III Events
 * 
 * This file contains additional CSTA events as defined in ECMA-269 standard:
 * - DivertedEvent
 * - TransferredEvent  
 * - ConferencedEvent
 * - QueuedEvent
 */

/**
 * ECMA-269 CSTA Phase III DivertedEvent
 * 
 * From ECMA-269: "This event indicates that a call has been diverted 
 * (transferred/forwarded) from one device to another."
 */
@XmlRootElement(name = "DivertedEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DivertedEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "connection",
    "divertingDevice",
    "newDestination",
    "localConnectionInfo",
    "cause"
})
class DivertedEvent extends CSTAEvent {
    
    @XmlElement(name = "connection", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private ConnectionID connection;
    
    @XmlElement(name = "divertingDevice", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private DeviceID divertingDevice;
    
    @XmlElement(name = "newDestination", namespace = CSTA_NAMESPACE)
    private DeviceID newDestination;
    
    @XmlElement(name = "localConnectionInfo", namespace = CSTA_NAMESPACE)
    private LocalConnectionState localConnectionInfo;
    
    @XmlElement(name = "cause", namespace = CSTA_NAMESPACE)
    private CSTACause cause;
    
    public DivertedEvent() {
        super();
    }
    
    public DivertedEvent(String monitorCrossRefID, ConnectionID connection, DeviceID divertingDevice) {
        super(monitorCrossRefID);
        this.connection = connection;
        this.divertingDevice = divertingDevice;
    }
    
    @Override
    public String getCSTAEventType() {
        return "DivertedEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<DivertedEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (connection != null) {
            xml.append("<connection>");
            xml.append("<callID>").append(connection.getCallID()).append("</callID>");
            xml.append("<deviceID>").append(connection.getDeviceID()).append("</deviceID>");
            xml.append("</connection>");
        }
        if (divertingDevice != null) {
            xml.append("<divertingDevice>").append(divertingDevice.getValue()).append("</divertingDevice>");
        }
        if (newDestination != null) {
            xml.append("<newDestination>").append(newDestination.getValue()).append("</newDestination>");
        }
        xml.append("</DivertedEvent>");
        return xml.toString();
    }
    
    // Getters and Setters
    public ConnectionID getConnection() { return connection; }
    public void setConnection(ConnectionID connection) { this.connection = connection; }
    public DeviceID getDivertingDevice() { return divertingDevice; }
    public void setDivertingDevice(DeviceID divertingDevice) { this.divertingDevice = divertingDevice; }
    public DeviceID getNewDestination() { return newDestination; }
    public void setNewDestination(DeviceID newDestination) { this.newDestination = newDestination; }
}

/**
 * ECMA-269 CSTA Phase III TransferredEvent
 * 
 * From ECMA-269: "This event indicates that a call has been transferred 
 * from one device to another."
 * 
 * Note: Official Avaya SDK uses "TransferedEvent" (with one 'r') in the interface
 */
@XmlRootElement(name = "TransferredEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferredEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "primaryOldCall",
    "secondaryOldCall",
    "transferringDevice",
    "transferredToDevice",
    "newCall",
    "localConnectionInfo",
    "cause"
})
class TransferredEvent extends CSTAEvent {
    
    @XmlElement(name = "primaryOldCall", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private ConnectionID primaryOldCall;
    
    @XmlElement(name = "secondaryOldCall", namespace = CSTA_NAMESPACE)
    private ConnectionID secondaryOldCall;
    
    @XmlElement(name = "transferringDevice", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private DeviceID transferringDevice;
    
    @XmlElement(name = "transferredToDevice", namespace = CSTA_NAMESPACE)
    private DeviceID transferredToDevice;
    
    @XmlElement(name = "newCall", namespace = CSTA_NAMESPACE)
    private CallID newCall;
    
    @XmlElement(name = "localConnectionInfo", namespace = CSTA_NAMESPACE)
    private LocalConnectionState localConnectionInfo;
    
    @XmlElement(name = "cause", namespace = CSTA_NAMESPACE)
    private CSTACause cause;
    
    public TransferredEvent() {
        super();
    }
    
    public TransferredEvent(String monitorCrossRefID, ConnectionID primaryOldCall, DeviceID transferringDevice) {
        super(monitorCrossRefID);
        this.primaryOldCall = primaryOldCall;
        this.transferringDevice = transferringDevice;
    }
    
    @Override
    public String getCSTAEventType() {
        return "TransferredEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<TransferredEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (primaryOldCall != null) {
            xml.append("<primaryOldCall>");
            xml.append("<callID>").append(primaryOldCall.getCallID()).append("</callID>");
            xml.append("<deviceID>").append(primaryOldCall.getDeviceID()).append("</deviceID>");
            xml.append("</primaryOldCall>");
        }
        if (transferringDevice != null) {
            xml.append("<transferringDevice>").append(transferringDevice.getValue()).append("</transferringDevice>");
        }
        if (transferredToDevice != null) {
            xml.append("<transferredToDevice>").append(transferredToDevice.getValue()).append("</transferredToDevice>");
        }
        xml.append("</TransferredEvent>");
        return xml.toString();
    }
    
    // Getters and Setters
    public ConnectionID getPrimaryOldCall() { return primaryOldCall; }
    public void setPrimaryOldCall(ConnectionID primaryOldCall) { this.primaryOldCall = primaryOldCall; }
    public DeviceID getTransferringDevice() { return transferringDevice; }
    public void setTransferringDevice(DeviceID transferringDevice) { this.transferringDevice = transferringDevice; }
    public DeviceID getTransferredToDevice() { return transferredToDevice; }
    public void setTransferredToDevice(DeviceID transferredToDevice) { this.transferredToDevice = transferredToDevice; }
}

/**
 * ECMA-269 CSTA Phase III ConferencedEvent
 * 
 * From ECMA-269: "This event indicates that a call has been conferenced 
 * with other parties."
 */
@XmlRootElement(name = "ConferencedEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConferencedEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "primaryOldCall",
    "secondaryOldCall",
    "conferencingDevice",
    "addedParty",
    "newCall",
    "localConnectionInfo",
    "cause"
})
class ConferencedEvent extends CSTAEvent {
    
    @XmlElement(name = "primaryOldCall", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private ConnectionID primaryOldCall;
    
    @XmlElement(name = "secondaryOldCall", namespace = CSTA_NAMESPACE)
    private ConnectionID secondaryOldCall;
    
    @XmlElement(name = "conferencingDevice", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private DeviceID conferencingDevice;
    
    @XmlElement(name = "addedParty", namespace = CSTA_NAMESPACE)
    private DeviceID addedParty;
    
    @XmlElement(name = "newCall", namespace = CSTA_NAMESPACE)
    private CallID newCall;
    
    @XmlElement(name = "localConnectionInfo", namespace = CSTA_NAMESPACE)
    private LocalConnectionState localConnectionInfo;
    
    @XmlElement(name = "cause", namespace = CSTA_NAMESPACE)
    private CSTACause cause;
    
    public ConferencedEvent() {
        super();
    }
    
    public ConferencedEvent(String monitorCrossRefID, ConnectionID primaryOldCall, DeviceID conferencingDevice) {
        super(monitorCrossRefID);
        this.primaryOldCall = primaryOldCall;
        this.conferencingDevice = conferencingDevice;
    }
    
    @Override
    public String getCSTAEventType() {
        return "ConferencedEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<ConferencedEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (primaryOldCall != null) {
            xml.append("<primaryOldCall>");
            xml.append("<callID>").append(primaryOldCall.getCallID()).append("</callID>");
            xml.append("<deviceID>").append(primaryOldCall.getDeviceID()).append("</deviceID>");
            xml.append("</primaryOldCall>");
        }
        if (conferencingDevice != null) {
            xml.append("<conferencingDevice>").append(conferencingDevice.getValue()).append("</conferencingDevice>");
        }
        if (addedParty != null) {
            xml.append("<addedParty>").append(addedParty.getValue()).append("</addedParty>");
        }
        xml.append("</ConferencedEvent>");
        return xml.toString();
    }
    
    // Getters and Setters
    public ConnectionID getPrimaryOldCall() { return primaryOldCall; }
    public void setPrimaryOldCall(ConnectionID primaryOldCall) { this.primaryOldCall = primaryOldCall; }
    public DeviceID getConferencingDevice() { return conferencingDevice; }
    public void setConferencingDevice(DeviceID conferencingDevice) { this.conferencingDevice = conferencingDevice; }
    public DeviceID getAddedParty() { return addedParty; }
    public void setAddedParty(DeviceID addedParty) { this.addedParty = addedParty; }
}

/**
 * ECMA-269 CSTA Phase III QueuedEvent
 * 
 * From ECMA-269: "This event indicates that a call has been placed 
 * in a queue (ACD queue, hunt group, etc.)."
 */
@XmlRootElement(name = "QueuedEvent", namespace = CSTAEvent.CSTA_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueuedEventType", namespace = CSTAEvent.CSTA_NAMESPACE, propOrder = {
    "queuedConnection",
    "queue",
    "callingDevice",
    "calledDevice",
    "lastRedirectionDevice",
    "numberQueued",
    "localConnectionInfo",
    "cause"
})
class QueuedEvent extends CSTAEvent {
    
    @XmlElement(name = "queuedConnection", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private ConnectionID queuedConnection;
    
    @XmlElement(name = "queue", namespace = CSTA_NAMESPACE, required = true)
    @NotNull
    private DeviceID queue;
    
    @XmlElement(name = "callingDevice", namespace = CSTA_NAMESPACE)
    private DeviceID callingDevice;
    
    @XmlElement(name = "calledDevice", namespace = CSTA_NAMESPACE)
    private DeviceID calledDevice;
    
    @XmlElement(name = "lastRedirectionDevice", namespace = CSTA_NAMESPACE)
    private DeviceID lastRedirectionDevice;
    
    @XmlElement(name = "numberQueued", namespace = CSTA_NAMESPACE)
    private Integer numberQueued;
    
    @XmlElement(name = "localConnectionInfo", namespace = CSTA_NAMESPACE)
    private LocalConnectionState localConnectionInfo;
    
    @XmlElement(name = "cause", namespace = CSTA_NAMESPACE)
    private CSTACause cause;
    
    public QueuedEvent() {
        super();
    }
    
    public QueuedEvent(String monitorCrossRefID, ConnectionID queuedConnection, DeviceID queue) {
        super(monitorCrossRefID);
        this.queuedConnection = queuedConnection;
        this.queue = queue;
    }
    
    @Override
    public String getCSTAEventType() {
        return "QueuedEvent";
    }
    
    @Override
    public CSTAEventCategory getEventCategory() {
        return CSTAEventCategory.CALL_CONTROL;
    }
    
    @Override
    public String toXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<QueuedEvent xmlns=\"").append(CSTA_NAMESPACE).append("\">");
        xml.append("<monitorCrossRefID>").append(getMonitorCrossRefID()).append("</monitorCrossRefID>");
        if (getEventTime() != null) {
            xml.append("<eventTime>").append(getEventTime().toString()).append("</eventTime>");
        }
        if (queuedConnection != null) {
            xml.append("<queuedConnection>");
            xml.append("<callID>").append(queuedConnection.getCallID()).append("</callID>");
            xml.append("<deviceID>").append(queuedConnection.getDeviceID()).append("</deviceID>");
            xml.append("</queuedConnection>");
        }
        if (queue != null) {
            xml.append("<queue>").append(queue.getValue()).append("</queue>");
        }
        if (numberQueued != null) {
            xml.append("<numberQueued>").append(numberQueued).append("</numberQueued>");
        }
        xml.append("</QueuedEvent>");
        return xml.toString();
    }
    
    // Getters and Setters
    public ConnectionID getQueuedConnection() { return queuedConnection; }
    public void setQueuedConnection(ConnectionID queuedConnection) { this.queuedConnection = queuedConnection; }
    public DeviceID getQueue() { return queue; }
    public void setQueue(DeviceID queue) { this.queue = queue; }
    public Integer getNumberQueued() { return numberQueued; }
    public void setNumberQueued(Integer numberQueued) { this.numberQueued = numberQueued; }
}
