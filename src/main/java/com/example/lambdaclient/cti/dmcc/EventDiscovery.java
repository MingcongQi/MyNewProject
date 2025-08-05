package com.example.lambdaclient.cti.dmcc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Event Discovery System
 * 
 * This class discovers actual DMCC event types from your Avaya system
 * instead of making assumptions about event names.
 */
public class EventDiscovery {
    
    private static final Logger logger = Logger.getLogger(EventDiscovery.class.getName());
    
    // Track discovered event types
    private final Set<String> discoveredEvents = ConcurrentHashMap.newKeySet();
    
    // Common patterns for DMCC events (based on CSTA standards)
    private static final Pattern[] EVENT_PATTERNS = {
        Pattern.compile("<([A-Za-z]+Event)\\s"),           // <SomeEvent ...>
        Pattern.compile("<([A-Za-z]+)\\s.*Event"),        // <Something ...Event>
        Pattern.compile("eventType=\"([^\"]+)\""),         // eventType="EventName"
        Pattern.compile("<eventName>([^<]+)</eventName>"), // <eventName>EventName</eventName>
        Pattern.compile("xmlns.*#([A-Za-z]+Event)"),       // namespace#EventName
    };
    
    /**
     * Analyze incoming XML to discover event types
     */
    public String discoverEventType(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return null;
        }
        
        // Try each pattern to extract event type
        for (Pattern pattern : EVENT_PATTERNS) {
            Matcher matcher = pattern.matcher(xmlContent);
            if (matcher.find()) {
                String eventType = matcher.group(1);
                
                // Add to discovered events
                if (discoveredEvents.add(eventType)) {
                    logger.info("🔍 DISCOVERED NEW EVENT TYPE: " + eventType);
                    logger.fine("Sample XML: " + xmlContent.substring(0, Math.min(200, xmlContent.length())));
                }
                
                return eventType;
            }
        }
        
        // Log unknown event format for analysis
        logger.warning("❓ UNKNOWN EVENT FORMAT - Please analyze:");
        logger.warning("XML: " + xmlContent.substring(0, Math.min(500, xmlContent.length())));
        
        return "UnknownEvent";
    }
    
    /**
     * Get all discovered event types
     */
    public Set<String> getDiscoveredEvents() {
        return Set.copyOf(discoveredEvents);
    }
    
    /**
     * Print discovered events summary
     */
    public void printDiscoveredEvents() {
        logger.info("📋 DISCOVERED DMCC EVENT TYPES (" + discoveredEvents.size() + " total):");
        discoveredEvents.stream()
            .sorted()
            .forEach(event -> logger.info("  ✓ " + event));
    }
    
    /**
     * Check if this looks like a call-related event
     */
    public boolean isCallEvent(String eventType) {
        if (eventType == null) return false;
        
        String lower = eventType.toLowerCase();
        return lower.contains("call") || 
               lower.contains("connection") ||
               lower.contains("delivered") ||
               lower.contains("established") ||
               lower.contains("cleared") ||
               lower.contains("transferred") ||
               lower.contains("conferenced");
    }
    
    /**
     * Check if this looks like an agent-related event
     */
    public boolean isAgentEvent(String eventType) {
        if (eventType == null) return false;
        
        String lower = eventType.toLowerCase();
        return lower.contains("agent") ||
               lower.contains("login") ||
               lower.contains("logout") ||
               lower.contains("state") ||
               lower.contains("workmode");
    }
    
    /**
     * Generate suggested handler method name
     */
    public String suggestHandlerName(String eventType) {
        if (eventType == null) return "handleUnknownEvent";
        
        // Convert EventName to handleEventName
        return "handle" + eventType;
    }
}
