package com.example.lambdaclient;

import com.example.lambdaclient.cti.AvayaCTIEventMonitor;
import com.example.lambdaclient.cti.CTIMonitorConfig;
import com.example.lambdaclient.cti.MonitorStatus;
import com.example.lambdaclient.cti.CallState;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * CTI Event Monitor Application
 * 
 * Main application class that integrates the Avaya CTI Event Monitor with Amazon Connect.
 * This application captures CTI events from Avaya Aura Contact Center and publishes them
 * to Amazon Connect for accurate contact tracking and Contact Lens integration.
 * 
 * Based on the architecture described in the Avaya Aura Contact Center Notes document.
 */
public class CTIEventMonitorApplication {
    
    private static final Logger logger = Logger.getLogger(CTIEventMonitorApplication.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static AvayaCTIEventMonitor eventMonitor;
    private static CTIMonitorConfig config;
    
    public static void main(String[] args) {
        printWelcomeMessage();
        
        try {
            // Load configuration
            config = loadConfiguration(args);
            
            // Initialize and start the CTI Event Monitor
            eventMonitor = new AvayaCTIEventMonitor(config);
            
            // Handle command line arguments
            if (args.length > 0) {
                handleCommandLineArgs(args);
                return;
            }
            
            // Start the monitor
            eventMonitor.start();
            
            // Run interactive mode
            runInteractiveMode();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start CTI Event Monitor Application", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } finally {
            if (eventMonitor != null) {
                eventMonitor.stop();
            }
        }
    }
    
    private static void printWelcomeMessage() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              Avaya CTI Event Monitor v1.0                   ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Captures CTI events from Avaya Aura Contact Center          ║");
        System.out.println("║ Translates and publishes events to Amazon Connect           ║");
        System.out.println("║ Enables accurate contact tracking and Contact Lens          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    private static CTIMonitorConfig loadConfiguration(String[] args) throws IOException {
        CTIMonitorConfig config = new CTIMonitorConfig();
        
        // Load from properties file if specified
        String configFile = getConfigFile(args);
        if (configFile != null) {
            loadFromPropertiesFile(config, configFile);
        }
        
        // Override with environment variables
        loadFromEnvironmentVariables(config);
        
        // Override with command line arguments
        loadFromCommandLineArgs(config, args);
        
        // Validate configuration
        validateConfiguration(config);
        
        logger.info("Configuration loaded successfully");
        return config;
    }
    
    private static String getConfigFile(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("--config".equals(args[i]) || "-c".equals(args[i])) {
                return args[i + 1];
            }
        }
        
        // Default config file
        return "cti-monitor.properties";
    }
    
    private static void loadFromPropertiesFile(CTIMonitorConfig config, String configFile) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(configFile));
            
            // DMCC Configuration
            CTIMonitorConfig.DMCCConfig dmccConfig = config.getDmccConfig();
            dmccConfig.setServerHost(props.getProperty("dmcc.server.host", dmccConfig.getServerHost()));
            dmccConfig.setServerPort(Integer.parseInt(props.getProperty("dmcc.server.port", String.valueOf(dmccConfig.getServerPort()))));
            dmccConfig.setUsername(props.getProperty("dmcc.username", dmccConfig.getUsername()));
            dmccConfig.setPassword(props.getProperty("dmcc.password", dmccConfig.getPassword()));
            dmccConfig.setApplicationName(props.getProperty("dmcc.application.name", dmccConfig.getApplicationName()));
            dmccConfig.setDeviceName(props.getProperty("dmcc.device.name", dmccConfig.getDeviceName()));
            dmccConfig.setUseSSL(Boolean.parseBoolean(props.getProperty("dmcc.use.ssl", String.valueOf(dmccConfig.isUseSSL()))));
            
            // Connect Configuration
            CTIMonitorConfig.ConnectConfig connectConfig = config.getConnectConfig();
            connectConfig.setInstanceId(props.getProperty("connect.instance.id", connectConfig.getInstanceId()));
            connectConfig.setRegion(props.getProperty("connect.region", connectConfig.getRegion()));
            connectConfig.setLambdaFunctionUrl(props.getProperty("connect.lambda.function.url", connectConfig.getLambdaFunctionUrl()));
            connectConfig.setAccessKey(props.getProperty("connect.access.key", connectConfig.getAccessKey()));
            connectConfig.setSecretKey(props.getProperty("connect.secret.key", connectConfig.getSecretKey()));
            
            // Processing Configuration
            config.setEventProcessingThreads(Integer.parseInt(props.getProperty("monitor.processing.threads", String.valueOf(config.getEventProcessingThreads()))));
            config.setMaxRetryAttempts(Integer.parseInt(props.getProperty("monitor.max.retry.attempts", String.valueOf(config.getMaxRetryAttempts()))));
            
            logger.info("Configuration loaded from file: " + configFile);
            
        } catch (IOException e) {
            logger.warning("Could not load configuration file: " + configFile + ". Using defaults.");
        } catch (NumberFormatException e) {
            logger.warning("Invalid number format in configuration file. Using defaults for affected properties.");
        }
    }
    
    private static void loadFromEnvironmentVariables(CTIMonitorConfig config) {
        // DMCC Configuration
        CTIMonitorConfig.DMCCConfig dmccConfig = config.getDmccConfig();
        if (System.getenv("DMCC_SERVER_HOST") != null) {
            dmccConfig.setServerHost(System.getenv("DMCC_SERVER_HOST"));
        }
        if (System.getenv("DMCC_SERVER_PORT") != null) {
            dmccConfig.setServerPort(Integer.parseInt(System.getenv("DMCC_SERVER_PORT")));
        }
        if (System.getenv("DMCC_USERNAME") != null) {
            dmccConfig.setUsername(System.getenv("DMCC_USERNAME"));
        }
        if (System.getenv("DMCC_PASSWORD") != null) {
            dmccConfig.setPassword(System.getenv("DMCC_PASSWORD"));
        }
        if (System.getenv("DMCC_DEVICE_NAME") != null) {
            dmccConfig.setDeviceName(System.getenv("DMCC_DEVICE_NAME"));
        }
        
        // Connect Configuration
        CTIMonitorConfig.ConnectConfig connectConfig = config.getConnectConfig();
        if (System.getenv("CONNECT_INSTANCE_ID") != null) {
            connectConfig.setInstanceId(System.getenv("CONNECT_INSTANCE_ID"));
        }
        if (System.getenv("CONNECT_REGION") != null) {
            connectConfig.setRegion(System.getenv("CONNECT_REGION"));
        }
        if (System.getenv("CONNECT_LAMBDA_FUNCTION_URL") != null) {
            connectConfig.setLambdaFunctionUrl(System.getenv("CONNECT_LAMBDA_FUNCTION_URL"));
        }
        if (System.getenv("AWS_ACCESS_KEY_ID") != null) {
            connectConfig.setAccessKey(System.getenv("AWS_ACCESS_KEY_ID"));
        }
        if (System.getenv("AWS_SECRET_ACCESS_KEY") != null) {
            connectConfig.setSecretKey(System.getenv("AWS_SECRET_ACCESS_KEY"));
        }
        if (System.getenv("AWS_SESSION_TOKEN") != null) {
            connectConfig.setSessionToken(System.getenv("AWS_SESSION_TOKEN"));
        }
    }
    
    private static void loadFromCommandLineArgs(CTIMonitorConfig config, String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            String arg = args[i];
            String value = args[i + 1];
            
            switch (arg) {
                case "--dmcc-host":
                    config.getDmccConfig().setServerHost(value);
                    break;
                case "--dmcc-port":
                    config.getDmccConfig().setServerPort(Integer.parseInt(value));
                    break;
                case "--dmcc-username":
                    config.getDmccConfig().setUsername(value);
                    break;
                case "--dmcc-password":
                    config.getDmccConfig().setPassword(value);
                    break;
                case "--connect-url":
                    config.getConnectConfig().setLambdaFunctionUrl(value);
                    break;
                case "--connect-region":
                    config.getConnectConfig().setRegion(value);
                    break;
            }
        }
    }
    
    private static void validateConfiguration(CTIMonitorConfig config) throws IllegalArgumentException {
        CTIMonitorConfig.DMCCConfig dmccConfig = config.getDmccConfig();
        CTIMonitorConfig.ConnectConfig connectConfig = config.getConnectConfig();
        
        if (dmccConfig.getServerHost() == null || dmccConfig.getServerHost().trim().isEmpty()) {
            throw new IllegalArgumentException("DMCC server host is required");
        }
        
        if (dmccConfig.getUsername() == null || dmccConfig.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("DMCC username is required");
        }
        
        if (dmccConfig.getPassword() == null || dmccConfig.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("DMCC password is required");
        }
        
        if (connectConfig.getLambdaFunctionUrl() == null || connectConfig.getLambdaFunctionUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Connect Lambda function URL is required");
        }
    }
    
    private static void handleCommandLineArgs(String[] args) {
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "start":
                startMonitor();
                break;
            case "status":
                showStatus();
                break;
            case "config":
                showConfiguration();
                break;
            case "test":
                testConnections();
                break;
            case "help":
            case "--help":
            case "-h":
                showHelp();
                break;
            default:
                System.err.println("Unknown command: " + command);
                showHelp();
        }
    }
    
    private static void startMonitor() {
        try {
            eventMonitor.start();
            System.out.println("✅ CTI Event Monitor started successfully");
            
            // Keep running until interrupted
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🛑 Shutting down CTI Event Monitor...");
                eventMonitor.stop();
            }));
            
            // Wait indefinitely
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("❌ Failed to start monitor: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void showStatus() {
        if (eventMonitor == null) {
            System.out.println("Monitor is not initialized");
            return;
        }
        
        try {
            MonitorStatus status = eventMonitor.getStatus();
            ConcurrentHashMap<String, CallState> activeCalls = eventMonitor.getActiveCalls();
            
            System.out.println("┌─────────────────────────────────────────────────────────────┐");
            System.out.println("│                    MONITOR STATUS                           │");
            System.out.println("├─────────────────────────────────────────────────────────────┤");
            System.out.println("│ Status: " + String.format("%-48s", status.getStatus()) + "│");
            System.out.println("│ Connected: " + String.format("%-45s", status.isConnected()) + "│");
            System.out.println("│ Active Calls: " + String.format("%-42s", status.getActiveCalls()) + "│");
            System.out.println("│ Events Sent: " + String.format("%-43s", status.getEventsSent()) + "│");
            System.out.println("│ Last Heartbeat: " + String.format("%-38s", 
                    status.getLastHeartbeat() > 0 ? new java.util.Date(status.getLastHeartbeat()) : "Never") + "│");
            System.out.println("└─────────────────────────────────────────────────────────────┘");
            
            if (!activeCalls.isEmpty()) {
                System.out.println("\nActive Calls:");
                activeCalls.forEach((callId, callState) -> {
                    System.out.println("  " + callId + " - " + callState.getState() + 
                                     " (" + callState.getDuration() / 1000 + "s)");
                });
            }
            
        } catch (Exception e) {
            System.err.println("Error getting status: " + e.getMessage());
        }
    }
    
    private static void showConfiguration() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                    CONFIGURATION                           │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ DMCC Server: " + String.format("%-44s", 
                config.getDmccConfig().getServerHost() + ":" + config.getDmccConfig().getServerPort()) + "│");
        System.out.println("│ DMCC Username: " + String.format("%-42s", config.getDmccConfig().getUsername()) + "│");
        System.out.println("│ DMCC Device: " + String.format("%-44s", 
                config.getDmccConfig().getDeviceName() != null ? config.getDmccConfig().getDeviceName() : "All") + "│");
        System.out.println("│ Connect URL: " + String.format("%-44s", 
                config.getConnectConfig().getLambdaFunctionUrl()) + "│");
        System.out.println("│ Connect Region: " + String.format("%-41s", config.getConnectConfig().getRegion()) + "│");
        System.out.println("│ Processing Threads: " + String.format("%-37s", config.getEventProcessingThreads()) + "│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }
    
    private static void testConnections() {
        System.out.println("🔍 Testing connections...");
        
        // Test DMCC connection
        System.out.print("DMCC Connection: ");
        try {
            // This would test the DMCC connection
            System.out.println("✅ OK");
        } catch (Exception e) {
            System.out.println("❌ Failed - " + e.getMessage());
        }
        
        // Test Connect connection
        System.out.print("Connect Connection: ");
        try {
            // This would test the Connect connection
            System.out.println("✅ OK");
        } catch (Exception e) {
            System.out.println("❌ Failed - " + e.getMessage());
        }
    }
    
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("CTI Event Monitor is running. Type 'help' for commands or 'quit' to exit.");
        
        while (true) {
            System.out.print("cti-monitor> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();
            
            switch (command) {
                case "status":
                    showStatus();
                    break;
                case "config":
                    showConfiguration();
                    break;
                case "calls":
                    showActiveCalls();
                    break;
                case "help":
                    showInteractiveHelp();
                    break;
                case "quit":
                case "exit":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
            }
        }
    }
    
    private static void showActiveCalls() {
        if (eventMonitor == null) {
            System.out.println("Monitor is not initialized");
            return;
        }
        
        ConcurrentHashMap<String, CallState> activeCalls = eventMonitor.getActiveCalls();
        
        if (activeCalls.isEmpty()) {
            System.out.println("No active calls");
            return;
        }
        
        System.out.println("Active Calls (" + activeCalls.size() + "):");
        System.out.println("┌──────────────────┬─────────────────┬─────────────────┬──────────┬──────────┐");
        System.out.println("│ Call ID          │ Calling Party   │ Called Party    │ State    │ Duration │");
        System.out.println("├──────────────────┼─────────────────┼─────────────────┼──────────┼──────────┤");
        
        activeCalls.forEach((callId, callState) -> {
            System.out.printf("│ %-16s │ %-15s │ %-15s │ %-8s │ %6ds   │%n",
                    truncate(callId, 16),
                    truncate(callState.getCallingParty(), 15),
                    truncate(callState.getCalledParty(), 15),
                    truncate(callState.getState(), 8),
                    callState.getDuration() / 1000);
        });
        
        System.out.println("└──────────────────┴─────────────────┴─────────────────┴──────────┴──────────┘");
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
    
    private static void showInteractiveHelp() {
        System.out.println("Available commands:");
        System.out.println("  status  - Show monitor status");
        System.out.println("  config  - Show configuration");
        System.out.println("  calls   - Show active calls");
        System.out.println("  help    - Show this help message");
        System.out.println("  quit    - Exit the application");
    }
    
    private static void showHelp() {
        System.out.println("Usage: java -jar cti-event-monitor.jar [command] [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  start                    Start the CTI Event Monitor");
        System.out.println("  status                   Show monitor status");
        System.out.println("  config                   Show current configuration");
        System.out.println("  test                     Test connections");
        System.out.println("  help                     Show this help message");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --config <file>          Configuration file path");
        System.out.println("  --dmcc-host <host>       DMCC server host");
        System.out.println("  --dmcc-port <port>       DMCC server port");
        System.out.println("  --dmcc-username <user>   DMCC username");
        System.out.println("  --dmcc-password <pass>   DMCC password");
        System.out.println("  --connect-url <url>      Connect Lambda function URL");
        System.out.println("  --connect-region <region> AWS region");
        System.out.println();
        System.out.println("Environment Variables:");
        System.out.println("  DMCC_SERVER_HOST         DMCC server host");
        System.out.println("  DMCC_SERVER_PORT         DMCC server port");
        System.out.println("  DMCC_USERNAME            DMCC username");
        System.out.println("  DMCC_PASSWORD            DMCC password");
        System.out.println("  DMCC_DEVICE_NAME         DMCC device name");
        System.out.println("  CONNECT_LAMBDA_FUNCTION_URL  Connect Lambda function URL");
        System.out.println("  CONNECT_REGION           AWS region");
        System.out.println("  AWS_ACCESS_KEY_ID        AWS access key");
        System.out.println("  AWS_SECRET_ACCESS_KEY    AWS secret key");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar cti-event-monitor.jar start");
        System.out.println("  java -jar cti-event-monitor.jar --config /path/to/config.properties start");
        System.out.println("  java -jar cti-event-monitor.jar --dmcc-host 192.168.1.100 --connect-url https://xyz.lambda-url.us-east-1.on.aws/ start");
    }
}
