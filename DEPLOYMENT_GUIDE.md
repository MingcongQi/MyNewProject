# CTI Event Monitor Deployment Guide

## Overview

This guide provides step-by-step instructions for deploying the Avaya CTI Event Monitor for Amazon Connect integration. The monitor captures CTI events from Avaya Aura Contact Center and publishes them to Amazon Connect for accurate contact tracking and Contact Lens integration.

## Prerequisites

### System Requirements
- **Operating System**: Linux, Windows, or macOS
- **Java**: OpenJDK 21 or Oracle JDK 21+
- **Memory**: Minimum 2GB RAM, recommended 4GB+
- **CPU**: Minimum 2 cores, recommended 4+ cores
- **Network**: Connectivity to both Avaya AES and Amazon Connect

### Network Requirements
- **Avaya AES**: Port 4721 (DMCC API) - typically SSL/TLS
- **Amazon Connect**: HTTPS (443) for Lambda Function URLs
- **DNS Resolution**: Ability to resolve both Avaya and AWS hostnames

### Access Requirements
- **Avaya AES**: Valid DMCC user account with appropriate permissions
- **Amazon Connect**: Lambda Function URL or Connect API access
- **AWS**: IAM credentials with Lambda invoke permissions (if using AWS APIs)

## Installation Steps

### 1. Install Java 21

#### On Ubuntu/Debian:
```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version
```

#### On CentOS/RHEL:
```bash
sudo yum install java-21-openjdk-devel
java -version
```

#### On macOS:
```bash
brew install openjdk@21
java -version
```

#### On Windows:
1. Download OpenJDK 21 from https://adoptium.net/
2. Install using the MSI installer
3. Verify installation: `java -version`

### 2. Build the Application

```bash
# Clone the repository
git clone https://github.com/MingcongQi/MyNewProject.git
cd MyNewProject

# Build the application
./gradlew build

# Create the executable JAR
./gradlew jar
```

The executable JAR will be created at: `build/libs/lambda-client-pure-java-2.0.0.jar`

### 3. Configuration Setup

#### Create Configuration Directory
```bash
mkdir -p /opt/cti-monitor/config
mkdir -p /opt/cti-monitor/logs
```

#### Create Configuration File
Create `/opt/cti-monitor/config/cti-monitor.properties`:

```properties
# DMCC Configuration
dmcc.server.host=your-aes-server.company.com
dmcc.server.port=4721
dmcc.username=cti_monitor_user
dmcc.password=secure_password
dmcc.application.name=CTI-Event-Monitor
dmcc.device.name=
dmcc.use.ssl=true
dmcc.connection.timeout.ms=30000
dmcc.read.timeout.ms=60000

# Amazon Connect Configuration
connect.instance.id=your-connect-instance-id
connect.region=us-east-1
connect.lambda.function.url=https://your-lambda-function-url.lambda-url.us-east-1.on.aws/
connect.max.retry.attempts=3
connect.retry.delay.ms=1000
connect.connection.timeout.ms=30000
connect.read.timeout.ms=60000

# Monitor Processing Configuration
monitor.processing.threads=5
monitor.max.retry.attempts=3
monitor.retry.delay.ms=1000
monitor.heartbeat.interval.ms=30000

# Logging Configuration
java.util.logging.level=INFO
java.util.logging.ConsoleHandler.level=INFO
java.util.logging.FileHandler.level=INFO
java.util.logging.FileHandler.pattern=/opt/cti-monitor/logs/cti-monitor.log
java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter
```

#### Set Environment Variables (Alternative)
```bash
export DMCC_SERVER_HOST=your-aes-server.company.com
export DMCC_SERVER_PORT=4721
export DMCC_USERNAME=cti_monitor_user
export DMCC_PASSWORD=secure_password

export CONNECT_LAMBDA_FUNCTION_URL=https://your-lambda-function-url.lambda-url.us-east-1.on.aws/
export CONNECT_REGION=us-east-1
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
```

### 4. Amazon Connect Lambda Function Setup

#### Create Lambda Function
Create a new Lambda function in your AWS account:

```python
import json
import logging
import boto3
from datetime import datetime

logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Initialize Connect client
connect_client = boto3.client('connect')

def lambda_handler(event, context):
    try:
        # Parse the CTI event
        if 'body' in event:
            cti_event = json.loads(event['body'])
        else:
            cti_event = event
            
        logger.info(f"Received CTI event: {cti_event['eventType']} for call {cti_event.get('callId', 'unknown')}")
        
        # Process based on event type
        if cti_event['eventType'] == 'CALL_INITIATED':
            return handle_call_initiated(cti_event)
        elif cti_event['eventType'] == 'CALL_DISCONNECTED':
            return handle_call_disconnected(cti_event)
        elif cti_event['eventType'] == 'CALL_TRANSFERRED':
            return handle_call_transferred(cti_event)
        elif cti_event['eventType'] == 'AGENT_STATE_CHANGE':
            return handle_agent_state_change(cti_event)
        elif cti_event['eventType'] == 'HEARTBEAT':
            return handle_heartbeat(cti_event)
        else:
            logger.warning(f"Unknown event type: {cti_event['eventType']}")
            
        return {
            'statusCode': 200,
            'body': json.dumps({'status': 'processed'})
        }
        
    except Exception as e:
        logger.error(f"Error processing CTI event: {str(e)}")
        return {
            'statusCode': 500,
            'body': json.dumps({'error': str(e)})
        }

def handle_call_initiated(event):
    """Handle call initiation event"""
    call_id = event.get('callId')
    calling_party = event.get('callingParty')
    called_party = event.get('calledParty')
    
    # Create or update contact attributes
    contact_attributes = {
        'CTI_CallId': call_id,
        'CTI_CallingParty': calling_party or '',
        'CTI_CalledParty': called_party or '',
        'CTI_EventTimestamp': str(event.get('timestamp', '')),
        'CTI_Source': 'Avaya'
    }
    
    logger.info(f"Call initiated: {call_id} from {calling_party} to {called_party}")
    
    return {
        'statusCode': 200,
        'body': json.dumps({
            'status': 'call_initiated_processed',
            'callId': call_id,
            'attributes': contact_attributes
        })
    }

def handle_call_disconnected(event):
    """Handle call disconnection event"""
    call_id = event.get('callId')
    duration = event.get('duration', 0)
    disconnect_reason = event.get('eventData', {}).get('disconnectReason', 'Unknown')
    
    logger.info(f"Call disconnected: {call_id}, duration: {duration}ms, reason: {disconnect_reason}")
    
    return {
        'statusCode': 200,
        'body': json.dumps({
            'status': 'call_disconnected_processed',
            'callId': call_id,
            'duration': duration,
            'reason': disconnect_reason
        })
    }

def handle_call_transferred(event):
    """Handle call transfer event"""
    call_id = event.get('callId')
    transferred_to = event.get('eventData', {}).get('transferredTo', 'Unknown')
    
    logger.info(f"Call transferred: {call_id} to {transferred_to}")
    
    return {
        'statusCode': 200,
        'body': json.dumps({
            'status': 'call_transferred_processed',
            'callId': call_id,
            'transferredTo': transferred_to
        })
    }

def handle_agent_state_change(event):
    """Handle agent state change event"""
    agent_id = event.get('agentId')
    old_state = event.get('oldState')
    new_state = event.get('newState')
    
    logger.info(f"Agent state change: {agent_id} from {old_state} to {new_state}")
    
    return {
        'statusCode': 200,
        'body': json.dumps({
            'status': 'agent_state_processed',
            'agentId': agent_id,
            'oldState': old_state,
            'newState': new_state
        })
    }

def handle_heartbeat(event):
    """Handle heartbeat event"""
    monitor_status = event.get('monitorStatus')
    events_sent = event.get('eventsSent', 0)
    
    logger.info(f"Heartbeat received: status={monitor_status}, events_sent={events_sent}")
    
    return {
        'statusCode': 200,
        'body': json.dumps({
            'status': 'heartbeat_received',
            'monitorStatus': monitor_status,
            'eventsSent': events_sent
        })
    }
```

#### Configure Lambda Function URL
1. Go to AWS Lambda Console
2. Select your function
3. Go to Configuration → Function URL
4. Create Function URL with:
   - Auth type: NONE (or AWS_IAM for additional security)
   - CORS: Configure as needed
5. Copy the Function URL for your configuration

### 5. Deployment Options

#### Option 1: Manual Deployment
```bash
# Copy JAR to deployment location
cp build/libs/lambda-client-pure-java-2.0.0.jar /opt/cti-monitor/

# Create startup script
cat > /opt/cti-monitor/start-monitor.sh << 'EOF'
#!/bin/bash
cd /opt/cti-monitor
java -Xmx2g -jar lambda-client-pure-java-2.0.0.jar \
  --config config/cti-monitor.properties \
  start
EOF

chmod +x /opt/cti-monitor/start-monitor.sh

# Start the monitor
/opt/cti-monitor/start-monitor.sh
```

#### Option 2: Systemd Service (Linux)
Create `/etc/systemd/system/cti-monitor.service`:

```ini
[Unit]
Description=CTI Event Monitor for Amazon Connect
After=network.target

[Service]
Type=simple
User=cti-monitor
Group=cti-monitor
WorkingDirectory=/opt/cti-monitor
ExecStart=/usr/bin/java -Xmx2g -jar lambda-client-pure-java-2.0.0.jar --config config/cti-monitor.properties start
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Enable and start the service:
```bash
# Create user
sudo useradd -r -s /bin/false cti-monitor
sudo chown -R cti-monitor:cti-monitor /opt/cti-monitor

# Enable service
sudo systemctl daemon-reload
sudo systemctl enable cti-monitor
sudo systemctl start cti-monitor

# Check status
sudo systemctl status cti-monitor
```

#### Option 3: Docker Deployment
Create `Dockerfile`:

```dockerfile
FROM openjdk:21-jre-slim

RUN groupadd -r cti-monitor && useradd -r -g cti-monitor cti-monitor

WORKDIR /app

COPY build/libs/lambda-client-pure-java-2.0.0.jar ./cti-monitor.jar
COPY src/main/resources/cti-monitor.properties ./config/

RUN chown -R cti-monitor:cti-monitor /app

USER cti-monitor

EXPOSE 8080

CMD ["java", "-Xmx2g", "-jar", "cti-monitor.jar", "--config", "config/cti-monitor.properties", "start"]
```

Build and run:
```bash
docker build -t cti-monitor .
docker run -d --name cti-monitor \
  -e DMCC_SERVER_HOST=your-aes-server.company.com \
  -e DMCC_USERNAME=cti_monitor_user \
  -e DMCC_PASSWORD=secure_password \
  -e CONNECT_LAMBDA_FUNCTION_URL=https://your-lambda-url.lambda-url.us-east-1.on.aws/ \
  cti-monitor
```

### 6. Testing and Validation

#### Test Configuration
```bash
java -jar lambda-client-pure-java-2.0.0.jar --config config/cti-monitor.properties test
```

#### Test DMCC Connection
```bash
java -jar lambda-client-pure-java-2.0.0.jar --config config/cti-monitor.properties status
```

#### Monitor Logs
```bash
tail -f /opt/cti-monitor/logs/cti-monitor.log
```

#### Interactive Testing
```bash
java -jar lambda-client-pure-java-2.0.0.jar --config config/cti-monitor.properties
# Use interactive commands: status, config, calls, help
```

### 7. Monitoring and Maintenance

#### Health Checks
Create a health check script:

```bash
#!/bin/bash
# health-check.sh

MONITOR_STATUS=$(java -jar /opt/cti-monitor/lambda-client-pure-java-2.0.0.jar \
  --config /opt/cti-monitor/config/cti-monitor.properties status 2>/dev/null)

if echo "$MONITOR_STATUS" | grep -q "Connected: true"; then
    echo "CTI Monitor is healthy"
    exit 0
else
    echo "CTI Monitor is unhealthy"
    exit 1
fi
```

#### Log Rotation
Configure logrotate in `/etc/logrotate.d/cti-monitor`:

```
/opt/cti-monitor/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 cti-monitor cti-monitor
    postrotate
        systemctl reload cti-monitor
    endscript
}
```

#### Monitoring Metrics
Monitor these key metrics:
- Connection status to DMCC and Connect
- Event processing rate
- Error rates and retry attempts
- Memory and CPU usage
- Network connectivity

### 8. Troubleshooting

#### Common Issues

**DMCC Connection Failed**
- Check network connectivity to AES server
- Verify username/password credentials
- Confirm SSL/TLS configuration
- Check firewall rules

**Connect Publishing Failed**
- Verify Lambda Function URL
- Check AWS credentials and permissions
- Test network connectivity to AWS
- Review Lambda function logs

**High Memory Usage**
- Adjust JVM heap size (-Xmx parameter)
- Monitor for memory leaks
- Consider reducing processing threads

**Event Processing Delays**
- Increase processing thread count
- Check network latency
- Monitor system resources

#### Debug Mode
Enable debug logging:
```properties
java.util.logging.level=FINE
java.util.logging.ConsoleHandler.level=FINE
```

### 9. Security Considerations

#### Credential Security
- Use environment variables for sensitive data
- Implement credential rotation
- Use AWS IAM roles when possible
- Restrict file permissions (600 for config files)

#### Network Security
- Use VPN or private network connections
- Implement firewall rules
- Enable SSL/TLS for all connections
- Monitor network traffic

#### Access Control
- Create dedicated service accounts
- Implement least privilege access
- Regular security audits
- Monitor access logs

### 10. Scaling and High Availability

#### Multiple Instances
- Deploy multiple monitor instances
- Use different device names for each instance
- Implement load balancing for Connect publishing
- Monitor for duplicate event processing

#### Failover Configuration
- Implement health checks and automatic restart
- Use container orchestration (Kubernetes, Docker Swarm)
- Configure database persistence for call state
- Implement event queuing for reliability

This deployment guide provides comprehensive instructions for setting up the CTI Event Monitor in production environments. Adjust configurations based on your specific requirements and infrastructure.
