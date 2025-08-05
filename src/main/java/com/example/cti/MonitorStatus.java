package com.example.cti;

/**
 * Monitor Status
 * 
 * Represents the current status of the CTI Event Monitor
 */
public class MonitorStatus {
    
    private boolean connected;
    private int activeCalls;
    private long eventsSent;
    private long lastHeartbeat;
    private long uptime;
    private String status;
    
    public MonitorStatus(boolean connected, int activeCalls, long eventsSent, long lastHeartbeat) {
        this.connected = connected;
        this.activeCalls = activeCalls;
        this.eventsSent = eventsSent;
        this.lastHeartbeat = lastHeartbeat;
        this.uptime = System.currentTimeMillis();
        this.status = connected ? "ACTIVE" : "DISCONNECTED";
    }
    
    // Getters and Setters
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
        this.status = connected ? "ACTIVE" : "DISCONNECTED";
    }
    
    public int getActiveCalls() {
        return activeCalls;
    }
    
    public void setActiveCalls(int activeCalls) {
        this.activeCalls = activeCalls;
    }
    
    public long getEventsSent() {
        return eventsSent;
    }
    
    public void setEventsSent(long eventsSent) {
        this.eventsSent = eventsSent;
    }
    
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
    public long getUptime() {
        return uptime;
    }
    
    public void setUptime(long uptime) {
        this.uptime = uptime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "MonitorStatus{" +
                "connected=" + connected +
                ", activeCalls=" + activeCalls +
                ", eventsSent=" + eventsSent +
                ", lastHeartbeat=" + lastHeartbeat +
                ", uptime=" + uptime +
                ", status='" + status + '\'' +
                '}';
    }
}
