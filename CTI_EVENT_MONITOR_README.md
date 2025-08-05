# Avaya CTI Event Monitor for Amazon Connect

A comprehensive CTI Event Monitor that captures Computer Telephony Integration (CTI) events from Avaya Aura Contact Center systems and publishes them to Amazon Connect for accurate contact tracking and Contact Lens integration.

## Overview

This application implements the architecture described in the Avaya Aura Contact Center Notes, serving as the CTI Event Monitor component that:

1. **Connects to Avaya Aura Contact Center** via the Device, Media, and Call Control (DMCC) API
2. **Captures vendor-specific CTI events** including call establishment, transfers, conferences, and agent state changes
3. **Translates events into standardized formats** compatible with Amazon Connect
4. **Publishes events to Amazon Connect** using Lambda Function URLs or Connect APIs
5. **Maintains accurate call state tracking** with unique call identifiers for SIPREC mapping

## Architecture

```
┌─────────────────┐    CTI Events    ┌─────────────────┐    Connect Events    ┌─────────────────┐
│                 │ ──────────────► │                 │ ──────────────────► │                 │
│ Avaya Aura      │                 │ CTI Event       │                     │ Amazon Connect  │
│ Contact Center  │                 │ Monitor         │                     │                 │
│                 │ ◄────────────── │                 │                     │                 │
└─────────────────┘    DMCC API     └─────────────────┘                     └─────────────────┘
```

## Features

### CTI Event Capture
- **Call Events**: Establishment, clearing, transfers, conferences, hold/retrieve
- **Agent Events**: State changes, login/logout, reason codes
- **Real-time Processing**: Multi-threaded event processing with configurable thread pools
- **Connection Management**: Automatic reconnection and heartbeat monitoring

### Event Translation
- **Standardized Format**: Converts Avaya-specific events to Connect-compatible format
- **Call State Tracking**: Maintains comprehensive call state with timing and participant information
- **Unique Identifiers**: Preserves call IDs for accurate SIPREC session mapping

### Amazon Connect Integration
- **Lambda Function URLs**: Direct HTTP publishing to Connect Lambda functions
- **Retry Logic**: Configurable retry attempts with exponential backoff
- **Heartbeat Monitoring**: Regular health checks to ensure connectivity
- **Event Correlation**: Maps CTI events to Connect contact records

## Installation

### Prerequisites
- Java 21 or higher
- Gradle 8.0+
- Access to Avaya Aura Application Enablement Services (AES)
- Amazon Connect instance with Lambda Function URL
- Network connectivity between the monitor and both systems

### Build from Source
```bash
git clone https://github.com/MingcongQi/MyNewProject.git
cd MyNewProject
./gradlew build
```

### Create Executable JAR
```bash
./gradlew jar
```

The executable JAR will be created in `build/libs/lambda-client-pure-java-2.0.0.jar`

## Configuration

### Configuration File
Create a `cti-monitor.properties` file:

```properties
# DMCC Configuration
dmcc.server.host=your-aes-server.company.com
dmcc.server.port=4721
dmcc.username=your_dmcc_username
dmcc.password=your_dmcc_password
dmcc.application.name=CTI-Event-Monitor
dmcc.device.name=your_device_name
dmcc.use.ssl=true

# Amazon Connect Configuration
connect.instance.id=your_connect_instance_id
connect.region=us-east-1
connect.lambda.function.url=https://your-lambda-function-url.lambda-url.us-east-1.on.aws/

# Processing Configuration
monitor.processing.threads=5
monitor.max.retry.attempts=3
monitor.heartbeat.interval.ms=30000
```

### Environment Variables
```bash
export DMCC_SERVER_HOST=your-aes-server.company.com
export DMCC_SERVER_PORT=4721
export DMCC_USERNAME=your_dmcc_username
export DMCC_PASSWORD=your_dmcc_password
export DMCC_DEVICE_NAME=your_device_name

export CONNECT_LAMBDA_FUNCTION_URL=https://your-lambda-function-url.lambda-url.us-east-1.on.aws/
export CONNECT_REGION=us-east-1
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
```

## Usage

### Command Line Interface

#### Start the Monitor
```bash
java -jar cti-event-monitor.jar start
```

#### Check Status
```bash
java -jar cti-event-monitor.jar status
```

#### Test Connections
```bash
java -jar cti-event-monitor.jar test
```

#### Show Configuration
```bash
java -jar cti-event-monitor.jar config
```

### Interactive Mode
```bash
java -jar cti-event-monitor.jar
```

Interactive commands:
- `status` - Show monitor status and active calls
- `config` - Display current configuration
- `calls` - List active calls with details
- `help` - Show available commands
- `quit` - Exit the application

### Configuration Override
```bash
java -jar cti-event-monitor.jar --config /path/to/config.properties start
java -jar cti-event-monitor.jar --dmcc-host 192.168.1.100 --connect-url https://xyz.lambda-url.us-east-1.on.aws/ start
```

## Event Types

### Call Events
- **CALL_INITIATED**: New call established
- **CALL_DISCONNECTED**: Call cleared/ended
- **CALL_TRANSFERRED**: Call transferred to another party
- **CALL_CONFERENCED**: Conference call established
- **CALL_HELD**: Call placed on hold
- **CALL_RETRIEVED**: Call retrieved from hold
- **CALL_DELIVERED**: Call delivered to device
- **CALL_ANSWERED**: Call answered by agent

### Agent Events
- **AGENT_STATE_CHANGE**: Agent state transition (Available, Busy, After Call Work, etc.)

## Event Format

### Connect Call Event
```json
{
  "callId": "unique-call-identifier",
  "timestamp": 1640995200000,
  "eventType": "CALL_INITIATED",
  "callingParty": "+1234567890",
  "calledParty": "+0987654321",
  "state": "ACTIVE",
  "duration": 45000,
  "instanceId": "connect-instance-id",
  "contactId": "connect-contact-id",
  "eventData": {
    "deviceId": "agent-device",
    "connectionId": "connection-123",
    "direction": "INBOUND"
  }
}
```

### Connect Agent Event
```json
{
  "agentId": "agent-123",
  "timestamp": 1640995200000,
  "eventType": "AGENT_STATE_CHANGE",
  "oldState": "AVAILABLE",
  "newState": "BUSY",
  "reasonCode": "HANDLING_CALL",
  "instanceId": "connect-instance-id"
}
```

## Monitoring and Troubleshooting

### Status Monitoring
The monitor provides comprehensive status information:
- Connection status to DMCC and Connect
- Number of active calls being tracked
- Total events sent
- Last heartbeat timestamp
- Processing thread status

### Logging
Configure logging levels in the properties file:
```properties
java.util.logging.level=INFO
java.util.logging.ConsoleHandler.level=INFO
java.util.logging.FileHandler.level=INFO
java.util.logging.FileHandler.pattern=cti-monitor.log
```

### Common Issues

#### DMCC Connection Issues
- Verify AES server hostname and port
- Check username/password credentials
- Ensure SSL/TLS configuration matches AES setup
- Verify network connectivity and firewall rules

#### Connect Publishing Issues
- Validate Lambda Function URL
- Check AWS credentials and permissions
- Verify network connectivity to AWS
- Review Lambda function logs for processing errors

#### Performance Issues
- Adjust processing thread count based on call volume
- Monitor memory usage and garbage collection
- Consider increasing retry delays for high-latency networks

## Integration with Amazon Connect

### Lambda Function Setup
Create a Lambda function to receive CTI events:

```python
import json
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def lambda_handler(event, context):
    try:
        # Parse CTI event
        cti_event = json.loads(event['body']) if 'body' in event else event
        
        # Process based on event type
        if cti_event['eventType'] == 'CALL_INITIATED':
            handle_call_initiated(cti_event)
        elif cti_event['eventType'] == 'CALL_DISCONNECTED':
            handle_call_disconnected(cti_event)
        # ... handle other event types
        
        return {
            'statusCode': 200,
            'body': json.dumps({'status': 'success'})
        }
        
    except Exception as e:
        logger.error(f"Error processing CTI event: {str(e)}")
        return {
            'statusCode': 500,
            'body': json.dumps({'error': str(e)})
        }

def handle_call_initiated(event):
    # Update Connect contact with CTI data
    # Map call ID to SIPREC session
    # Update contact attributes
    pass

def handle_call_disconnected(event):
    # Finalize contact record
    # Update call duration and outcome
    pass
```

### SIPREC Integration
The unique call ID from CTI events should be embedded in SIPREC metadata:

```xml
<recording-session>
  <participant>
    <nameID>
      <name>Call-ID</name>
      <value>unique-call-identifier</value>
    </nameID>
  </participant>
</recording-session>
```

## Security Considerations

### Credentials Management
- Store sensitive credentials in environment variables or secure configuration files
- Use AWS IAM roles when running on EC2 instances
- Implement credential rotation policies
- Restrict network access using security groups and firewalls

### Network Security
- Use SSL/TLS for all connections
- Implement VPN or private network connectivity
- Configure appropriate firewall rules
- Monitor network traffic for anomalies

## Performance and Scalability

### Recommended Specifications
- **CPU**: 4+ cores for high-volume environments
- **Memory**: 4GB+ RAM
- **Network**: Low-latency connection to both Avaya and AWS
- **Storage**: SSD for log files and temporary data

### Scaling Considerations
- Deploy multiple monitor instances for redundancy
- Use load balancing for Connect event publishing
- Implement event queuing for burst traffic handling
- Monitor resource utilization and scale accordingly

## Support and Maintenance

### Health Checks
The monitor includes built-in health checks:
- DMCC connection status
- Connect publishing success rates
- Event processing latency
- Memory and CPU utilization

### Maintenance Tasks
- Regular log rotation and cleanup
- Configuration backup and versioning
- Performance monitoring and optimization
- Security updates and patches

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## References

- [Avaya Aura Contact Center Notes](https://quip-amazon.com/p8FpAWmP72mY/Avaya-Aura-Contact-Center-Notes)
- [Avaya AES DMCC Java API Programmer's Guide](documentation/192524_AESDeviceMediaandCallControlJavaAPIProgrammersGuide.14.0.pdf)
- [Amazon Connect API Reference](https://docs.aws.amazon.com/connect/latest/APIReference/)
- [Contact Lens Integration Guide](https://docs.aws.amazon.com/connect/latest/adminguide/contact-lens.html)
