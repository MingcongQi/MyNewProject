# CTI Event Monitor for Amazon Connect Integration

This system implements a comprehensive CTI Event Monitor that captures Avaya DMCC events and publishes them to Amazon Connect, enabling accurate contact tracking and metadata correlation for Contact Lens integration.

## Architecture Overview

Based on the design document and sequence diagram, this system implements the **CTI Event Monitor** component that:

1. **Captures** vendor-specific CTI events from Avaya DMCC
2. **Discovers** and classifies event types automatically
3. **Tracks** call sessions and correlations
4. **Publishes** relevant events to Amazon Connect using Connect APIs
5. **Maintains** mapping between call IDs and Connect contact IDs

## System Components

### 1. EventDiscovery.java (Enhanced)
- **Purpose**: Discovers and classifies DMCC events from Avaya systems
- **Key Features**:
  - Automatic event type discovery using regex patterns
  - Call session tracking with metadata extraction
  - Event classification (call events, SIP events, etc.)
  - Comprehensive metadata collection
  - Call correlation and lifecycle management

### 2. ConnectEventPublisher.java
- **Purpose**: Publishes CTI events to Amazon Connect
- **Key Features**:
  - Event-specific handlers for different DMCC event types
  - Call-to-Contact ID mapping maintenance
  - Asynchronous event publishing
  - Connect contact creation and updates
  - SIP event correlation for media tracking

### 3. CTIEventMonitor.java
- **Purpose**: Main orchestration class that coordinates the entire system
- **Key Features**:
  - Event processing pipeline
  - Batch processing capabilities
  - Monitoring and statistics
  - Periodic cleanup tasks
  - Configurable behavior

### 4. ConnectApiClientImpl.java
- **Purpose**: Sample implementation of Connect API integration
- **Key Features**:
  - Mock Connect API client for testing
  - Production client factory methods
  - Simulated failures for testing
  - Comprehensive logging

### 5. CTIEventMonitorDemo.java
- **Purpose**: Comprehensive demonstration of the system
- **Key Features**:
  - Complete call flow simulation
  - Batch processing demonstration
  - Sample DMCC event generation
  - Statistics and reporting

## Supported Event Types

The system recognizes and handles the following DMCC event types from the sequence diagram:

### Core Call Events
- **EventRinging**: Call is ringing at destination
- **EventQueued**: Call has been queued
- **EventDiverted**: Call has been diverted to agent
- **EventPartyChanged**: Call party information changed (transfers, etc.)
- **EventReleased**: Call has been released/ended

### SIP/Media Events
- **SipInvite**: SIP INVITE received (media session starting)
- **SipBye**: SIP BYE received (media session ended)

### Connect Integration Events
- **ContactCreated**: New contact created in Connect
- **ContactStateUpdated**: Contact state updated
- **ContactMappingCreated**: Call mapping created

## Usage Examples

### Basic Usage

```java
// 1. Configure the monitor
CTIEventMonitor.CTIMonitorConfig config = new CTIEventMonitor.CTIMonitorConfig()
    .setConnectIntegrationEnabled(true)
    .setCleanupIntervalMinutes(15)
    .setCallRetentionMinutes(60);

// 2. Create Connect API client
ConnectEventPublisher.ConnectApiClient connectClient = 
    ConnectApiClientImpl.createProductionClient(
        "your-connect-instance-id",
        "us-east-1",
        "your-access-key",
        "your-secret-key"
    );

// 3. Initialize the monitor
CTIEventMonitor monitor = new CTIEventMonitor(config, connectClient);

// 4. Process DMCC events
String dmccEventXml = "<EventRinging>...</EventRinging>";
CompletableFuture<CTIEventMonitor.ProcessingResult> result = 
    monitor.processEvent(dmccEventXml);
```

### Batch Processing

```java
String[] batchEvents = {
    "<EventRinging>...</EventRinging>",
    "<EventQueued>...</EventQueued>",
    "<EventDiverted>...</EventDiverted>"
};

CompletableFuture<CTIEventMonitor.BatchProcessingResult> batchResult = 
    monitor.processEventBatch(batchEvents);
```

### Monitoring and Statistics

```java
// Get current statistics
CTIEventMonitor.MonitoringStats stats = monitor.getStats();
System.out.println("Total events processed: " + stats.getTotalEventsProcessed());
System.out.println("Success rate: " + stats.getSuccessRate() + "%");

// Print comprehensive status report
monitor.printStatusReport();
```

## Configuration Options

### CTIMonitorConfig Parameters

- **connectIntegrationEnabled**: Enable/disable Connect integration (default: true)
- **cleanupIntervalMinutes**: How often to clean up completed calls (default: 15)
- **callRetentionMinutes**: How long to retain completed call data (default: 60)
- **statusReportIntervalMinutes**: How often to print status reports (default: 30)

### Connect API Configuration

For production use, configure the Connect API client with:
- Connect instance ID
- AWS region
- AWS credentials (access key/secret key or IAM role)

## Event Flow Sequence

Based on the sequence diagram, the typical event flow is:

1. **Avaya System** generates DMCC event (e.g., EventRinging)
2. **CTI Event Monitor** receives and processes the event
3. **EventDiscovery** classifies the event and extracts metadata
4. **ConnectEventPublisher** determines if event should be published
5. **Connect API** creates or updates contact record
6. **Contact Mapping** is maintained for correlation
7. **Subsequent events** update the same contact using the mapping

## Running the Demo

To see the system in action:

```bash
# Compile the project
javac -cp . com/example/lambdaclient/cti/dmcc/*.java

# Run the demo
java com.example.lambdaclient.cti.dmcc.CTIEventMonitorDemo
```

The demo will:
1. Initialize the CTI Event Monitor
2. Simulate a complete call flow with multiple events
3. Demonstrate batch processing
4. Show comprehensive statistics and reporting

## Integration with Avaya DMCC

To integrate with actual Avaya DMCC systems:

1. **Connect to DMCC**: Use Avaya's DMCC SDK to establish connection
2. **Register for Events**: Subscribe to relevant DMCC event types
3. **Event Handling**: Pass received XML events to `monitor.processEvent()`
4. **Error Handling**: Implement retry logic and error recovery

### Sample DMCC Integration

```java
// Pseudo-code for DMCC integration
DMCCConnection dmccConnection = new DMCCConnection(avayaServerUrl);
dmccConnection.registerEventListener(new DMCCEventListener() {
    @Override
    public void onEvent(String eventXml) {
        // Process the event through our monitor
        monitor.processEvent(eventXml)
            .thenAccept(result -> {
                if (!result.isProcessed()) {
                    logger.warning("Failed to process DMCC event: " + result.getMessage());
                }
            });
    }
});
```

## Production Considerations

### Performance
- Events are processed asynchronously to avoid blocking
- Batch processing support for high-volume scenarios
- Configurable cleanup intervals to manage memory usage

### Reliability
- Comprehensive error handling and logging
- Retry logic for Connect API failures
- Call session correlation and recovery

### Monitoring
- Built-in statistics and reporting
- Periodic status reports
- Event discovery and classification metrics

### Security
- Secure credential management for Connect API
- Input validation for DMCC events
- Audit logging for compliance

## Troubleshooting

### Common Issues

1. **Events not being published to Connect**
   - Check `connectIntegrationEnabled` configuration
   - Verify Connect API credentials
   - Review event classification logic

2. **Call correlation issues**
   - Ensure call IDs are being extracted correctly
   - Check call session cleanup intervals
   - Verify metadata extraction patterns

3. **Performance issues**
   - Adjust batch processing sizes
   - Tune cleanup intervals
   - Monitor memory usage

### Logging

The system uses Java logging extensively. Configure log levels:
- **INFO**: General operation and statistics
- **FINE**: Detailed event processing
- **WARNING**: Non-fatal errors and issues
- **SEVERE**: Critical errors requiring attention

## Future Enhancements

- Support for additional Avaya event types
- Integration with other contact center platforms (Genesys, Cisco)
- Real-time dashboards and monitoring
- Advanced analytics and reporting
- Cloud-native deployment options

## Support

For questions or issues:
1. Review the comprehensive logging output
2. Check the demo class for usage examples
3. Refer to the Avaya DMCC documentation
4. Consult the Amazon Connect API documentation
