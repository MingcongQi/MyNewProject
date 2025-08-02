package com.example.lambdaclient;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleLambdaClientTest {

    @Test
    void testLambdaConfigCreation() {
        LambdaConfig config = LambdaConfig.getInstance();
        assertNotNull(config);
        assertNotNull(config.getFunctionUrl());
        assertNotNull(config.getRegion());
        assertTrue(config.getTimeoutSeconds() > 0);
    }

    @Test
    void testConfigurationDefaults() {
        LambdaConfig config = LambdaConfig.getInstance();
        assertEquals("us-east-1", config.getRegion());
        assertEquals(30, config.getTimeoutSeconds());
    }

    @Test
    void testConfigurationUpdate() {
        LambdaConfig config = LambdaConfig.getInstance();
        String originalUrl = config.getFunctionUrl();
        
        String testUrl = "https://test-lambda-url.lambda-url.us-west-2.on.aws/";
        config.setFunctionUrl(testUrl);
        
        assertEquals(testUrl, config.getFunctionUrl());
        
        // Reset to original
        config.setFunctionUrl(originalUrl);
    }
}
