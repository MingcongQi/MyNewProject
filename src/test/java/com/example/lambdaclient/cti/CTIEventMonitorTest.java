package com.example.lambdaclient.cti;

import com.example.cti.CTIMonitorConfig;
import com.example.cti.MonitorStatus;
import com.example.lambdaclient.cti.CallState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for CTI Event Monitor components
 */
public class CTIEventMonitorTest {
    
    private CTIMonitorConfig config;
    
    @BeforeEach
    void setUp() {
        config = new CTIMonitorConfig();
        
        // Set up test configuration
        CTIMonitorConfig.DMCCConfig dmccConfig = config.getDmccConfig();
        dmccConfig.setServerHost("test-server");
        dmccConfig.setServerPort(4721);
        dmccConfig.setUsername("test-user");
        dmccConfig.setPassword("test-password");
        
        CTIMonitorConfig.ConnectConfig connectConfig = config.getConnectConfig();
        connectConfig.setLambdaFunctionUrl("https://test-lambda-url.lambda-url.us-east-1.on.aws/");
        connectConfig.setRegion("us-east-1");
    }
    
    @Test
    void testConfigurationCreation() {
        assertNotNull(config);
        assertNotNull(config.getDmccConfig());
        assertNotNull(config.getConnectConfig());
        
        assertEquals("test-server", config.getDmccConfig().getServerHost());
        assertEquals(4721, config.getDmccConfig().getServerPort());
        assertEquals("test-user", config.getDmccConfig().getUsername());
        
        assertEquals("https://test-lambda-url.lambda-url.us-east-1.on.aws/", 
                     config.getConnectConfig().getLambdaFunctionUrl());
        assertEquals("us-east-1", config.getConnectConfig().getRegion());
    }
    
    @Test
    void testCallStateTracking() {
        CallState callState = new CallState("test-call-123", "+1234567890", "+0987654321", System.currentTimeMillis());
        
        assertNotNull(callState);
        assertEquals("test-call-123", callState.getCallId());
        assertEquals("+1234567890", callState.getCallingParty());
        assertEquals("+0987654321", callState.getCalledParty());
        assertEquals("INITIATED", callState.getState());
        
        // Test state change
        callState.setState("ACTIVE");
        assertEquals("ACTIVE", callState.getState());
        
        // Test transfer tracking
        callState.addTransfer("+1111111111", System.currentTimeMillis());
        assertEquals(1, callState.getTransfers().size());
        
        // Test conference tracking
        callState.addConferenceParticipant("+2222222222", System.currentTimeMillis());
        assertEquals(1, callState.getConferenceParticipants().size());
    }
    
    @Test
    void testMonitorStatus() {
        MonitorStatus status = new MonitorStatus(true, 5, 100, System.currentTimeMillis());
        
        assertTrue(status.isConnected());
        assertEquals(5, status.getActiveCalls());
        assertEquals(100, status.getEventsSent());
        assertEquals("ACTIVE", status.getStatus());
        
        // Test disconnected status
        status.setConnected(false);
        assertFalse(status.isConnected());
        assertEquals("DISCONNECTED", status.getStatus());
    }
    
    @Test
    void testConfigurationValidation() {
        // Test valid configuration
        assertDoesNotThrow(() -> {
            validateBasicConfig(config);
        });
        
        // Test invalid configuration - missing server host
        CTIMonitorConfig invalidConfig = new CTIMonitorConfig();
        invalidConfig.getDmccConfig().setServerHost("");
        
        assertThrows(IllegalArgumentException.class, () -> {
            validateBasicConfig(invalidConfig);
        });
    }
    
    private void validateBasicConfig(CTIMonitorConfig config) {
        if (config.getDmccConfig().getServerHost() == null || 
            config.getDmccConfig().getServerHost().trim().isEmpty()) {
            throw new IllegalArgumentException("DMCC server host is required");
        }
        
        if (config.getConnectConfig().getLambdaFunctionUrl() == null || 
            config.getConnectConfig().getLambdaFunctionUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Connect Lambda function URL is required");
        }
    }
}
