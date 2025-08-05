package com.example.cti;

/**
 * Configuration class for CTI Event Monitor
 */
public class CTIMonitorConfig {
    
    // DMCC Configuration
    private DMCCConfig dmccConfig;
    
    // Connect Configuration
    private ConnectConfig connectConfig;
    
    // Processing Configuration
    private int eventProcessingThreads = 5;
    private int maxRetryAttempts = 3;
    private long retryDelayMs = 1000;
    private long heartbeatIntervalMs = 30000;
    
    public CTIMonitorConfig() {
        this.dmccConfig = new DMCCConfig();
        this.connectConfig = new ConnectConfig();
    }
    
    // Getters and Setters
    public DMCCConfig getDmccConfig() {
        return dmccConfig;
    }
    
    public void setDmccConfig(DMCCConfig dmccConfig) {
        this.dmccConfig = dmccConfig;
    }
    
    public ConnectConfig getConnectConfig() {
        return connectConfig;
    }
    
    public void setConnectConfig(ConnectConfig connectConfig) {
        this.connectConfig = connectConfig;
    }
    
    public int getEventProcessingThreads() {
        return eventProcessingThreads;
    }
    
    public void setEventProcessingThreads(int eventProcessingThreads) {
        this.eventProcessingThreads = eventProcessingThreads;
    }
    
    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }
    
    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }
    
    public long getRetryDelayMs() {
        return retryDelayMs;
    }
    
    public void setRetryDelayMs(long retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }
    
    public long getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }
    
    public void setHeartbeatIntervalMs(long heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }
    
    @Override
    public String toString() {
        return "CTIMonitorConfig{" +
                "dmccConfig=" + dmccConfig +
                ", connectConfig=" + connectConfig +
                ", eventProcessingThreads=" + eventProcessingThreads +
                ", maxRetryAttempts=" + maxRetryAttempts +
                ", retryDelayMs=" + retryDelayMs +
                ", heartbeatIntervalMs=" + heartbeatIntervalMs +
                '}';
    }
    
    /**
     * DMCC Configuration
     */
    public static class DMCCConfig {
        private String serverHost = "localhost";
        private int serverPort = 4721;
        private String username;
        private String password;
        private String applicationName = "CTI-Event-Monitor";
        private String deviceName;
        private boolean useSSL = true;
        private int connectionTimeoutMs = 30000;
        private int readTimeoutMs = 60000;
        
        // Getters and Setters
        public String getServerHost() {
            return serverHost;
        }
        
        public void setServerHost(String serverHost) {
            this.serverHost = serverHost;
        }
        
        public int getServerPort() {
            return serverPort;
        }
        
        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getApplicationName() {
            return applicationName;
        }
        
        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }
        
        public String getDeviceName() {
            return deviceName;
        }
        
        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }
        
        public boolean isUseSSL() {
            return useSSL;
        }
        
        public void setUseSSL(boolean useSSL) {
            this.useSSL = useSSL;
        }
        
        public int getConnectionTimeoutMs() {
            return connectionTimeoutMs;
        }
        
        public void setConnectionTimeoutMs(int connectionTimeoutMs) {
            this.connectionTimeoutMs = connectionTimeoutMs;
        }
        
        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }
        
        public void setReadTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }
        
        @Override
        public String toString() {
            return "DMCCConfig{" +
                    "serverHost='" + serverHost + '\'' +
                    ", serverPort=" + serverPort +
                    ", username='" + username + '\'' +
                    ", applicationName='" + applicationName + '\'' +
                    ", deviceName='" + deviceName + '\'' +
                    ", useSSL=" + useSSL +
                    ", connectionTimeoutMs=" + connectionTimeoutMs +
                    ", readTimeoutMs=" + readTimeoutMs +
                    '}';
        }
    }
    
    /**
     * Connect Configuration
     */
    public static class ConnectConfig {
        private String instanceId;
        private String region = "us-east-1";
        private String accessKey;
        private String secretKey;
        private String sessionToken;
        private String lambdaFunctionUrl;
        private int maxRetryAttempts = 3;
        private long retryDelayMs = 1000;
        private int connectionTimeoutMs = 30000;
        private int readTimeoutMs = 60000;
        
        // Getters and Setters
        public String getInstanceId() {
            return instanceId;
        }
        
        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }
        
        public String getRegion() {
            return region;
        }
        
        public void setRegion(String region) {
            this.region = region;
        }
        
        public String getAccessKey() {
            return accessKey;
        }
        
        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }
        
        public String getSecretKey() {
            return secretKey;
        }
        
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
        
        public String getSessionToken() {
            return sessionToken;
        }
        
        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }
        
        public String getLambdaFunctionUrl() {
            return lambdaFunctionUrl;
        }
        
        public void setLambdaFunctionUrl(String lambdaFunctionUrl) {
            this.lambdaFunctionUrl = lambdaFunctionUrl;
        }
        
        public int getMaxRetryAttempts() {
            return maxRetryAttempts;
        }
        
        public void setMaxRetryAttempts(int maxRetryAttempts) {
            this.maxRetryAttempts = maxRetryAttempts;
        }
        
        public long getRetryDelayMs() {
            return retryDelayMs;
        }
        
        public void setRetryDelayMs(long retryDelayMs) {
            this.retryDelayMs = retryDelayMs;
        }
        
        public int getConnectionTimeoutMs() {
            return connectionTimeoutMs;
        }
        
        public void setConnectionTimeoutMs(int connectionTimeoutMs) {
            this.connectionTimeoutMs = connectionTimeoutMs;
        }
        
        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }
        
        public void setReadTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }
        
        @Override
        public String toString() {
            return "ConnectConfig{" +
                    "instanceId='" + instanceId + '\'' +
                    ", region='" + region + '\'' +
                    ", lambdaFunctionUrl='" + lambdaFunctionUrl + '\'' +
                    ", maxRetryAttempts=" + maxRetryAttempts +
                    ", retryDelayMs=" + retryDelayMs +
                    ", connectionTimeoutMs=" + connectionTimeoutMs +
                    ", readTimeoutMs=" + readTimeoutMs +
                    '}';
        }
    }
}
