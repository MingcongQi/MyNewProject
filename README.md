# CTI Event Monitor - ECMA-269 CSTA Phase III Compliant

## 🎯 Overview

A professional-grade **CTI (Computer Telephony Integration) Event Monitor** that implements the **ECMA-269 CSTA Phase III** international standard for monitoring and processing telecommunications events from Avaya Aura Contact Center systems.

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Avaya DMCC    │───▶│  CTI Event       │───▶│  External       │
│   API Events    │    │  Monitor         │    │  Systems        │
│                 │    │  (ECMA-269)      │    │  (Connect/etc.) │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## ✨ Key Features

### **🔧 ECMA-269 CSTA Phase III Compliance**
- Full implementation of ECMA-269 international standard
- Proper XML namespace and schema validation
- Standard CSTA data types and event structures
- Official Avaya DMCC SDK compatibility

### **📞 Comprehensive Event Support**
- **DeliveredEvent** (Section 17.2.5) - Call ringing/alerting
- **EstablishedEvent** (Section 17.2.8) - Call connected/answered
- **CallClearedEvent** (Section 17.2.2) - Call ended/cleared
- **ConnectionClearedEvent** (Section 17.2.3) - Connection dropped
- **Additional Events**: Diverted, Transferred, Conferenced, Queued

### **🚀 Production-Ready Features**
- Asynchronous event processing with configurable thread pools
- Automatic reconnection and error recovery
- Comprehensive logging and monitoring
- Health checks and status reporting
- Graceful shutdown handling

### **🔗 Integration Capabilities**
- Avaya DMCC API integration
- Amazon Connect event publishing
- RESTful status and monitoring endpoints
- Configurable event routing and filtering

## 📋 Requirements

- **Java 21+**
- **Avaya Communication Manager** with DMCC enabled
- **Network access** to Avaya DMCC server (typically port 4721)
- **Optional**: AWS credentials for Connect integration

## 🚀 Quick Start

### **1. Clone and Build**
```bash
git clone https://github.com/MingcongQi/MyNewProject.git
cd MyNewProject
./gradlew build
```

### **2. Configure Environment**
```bash
export DMCC_HOST=your-avaya-server.com
export DMCC_PORT=4721
export DMCC_USERNAME=your-username
export DMCC_PASSWORD=your-password
export DMCC_SECURE=true
```

### **3. Run the Application**
```bash
./gradlew run
```

Or run the JAR directly:
```bash
java -jar build/libs/cti-event-monitor-3.0.0.jar
```

## ⚙️ Configuration

### **Environment Variables**

| Variable | Description | Default |
|----------|-------------|---------|
| `DMCC_HOST` | Avaya DMCC server hostname | `localhost` |
| `DMCC_PORT` | DMCC server port | `4721` |
| `DMCC_USERNAME` | DMCC username | `admin` |
| `DMCC_PASSWORD` | DMCC password | `password` |
| `DMCC_SECURE` | Use secure connection | `true` |
| `EVENT_PROCESSING_THREADS` | Number of processing threads | `4` |
| `MAX_QUEUE_SIZE` | Maximum event queue size | `1000` |
| `HEARTBEAT_INTERVAL` | Heartbeat interval (seconds) | `30` |

### **Connect Integration (Optional)**
```bash
export CONNECT_INSTANCE_ID=your-connect-instance
export CONNECT_REGION=us-east-1
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
```

## 📊 Monitoring

### **Application Status**
The application provides comprehensive status information:

```
📊 CTI Event Monitor Status:
  🔗 DMCC Connection: ✅ Connected
  📞 Active Calls: 15
  📤 Events Sent: 1,247
  💓 Last Heartbeat: 2024-08-05T23:00:00Z

🎯 Supported CSTA Events:
  📞 DeliveredEvent (ECMA-269 Section 17.2.5)
  ✅ EstablishedEvent (ECMA-269 Section 17.2.8)
  📴 CallClearedEvent (ECMA-269 Section 17.2.2)
  🔌 ConnectionClearedEvent (ECMA-269 Section 17.2.3)
```

### **Health Checks**
- DMCC connection status
- Event processing queue health
- Memory and thread pool monitoring
- Automatic reconnection attempts

## 🧪 Testing

### **Run Tests**
```bash
./gradlew test
```

### **CSTA Compliance Testing**
```bash
./gradlew test --tests "*CSTAComplianceTest"
```

### **Integration Testing**
```bash
./gradlew test --tests "*IntegrationTest"
```

## 📚 Documentation

### **ECMA-269 CSTA Compliance**
See [ECMA_269_CSTA_COMPLIANCE.md](ECMA_269_CSTA_COMPLIANCE.md) for detailed compliance information.

### **API Documentation**
- [Event Discovery README](EVENT_DISCOVERY_README.md)
- [CTI Event Monitor README](CTI_EVENT_MONITOR_README.md)
- [Deployment Guide](DEPLOYMENT_GUIDE.md)

### **Standards References**
- [ECMA-269 Standard](https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/)
- [Avaya DMCC Documentation](https://support.avaya.com/elmodocs2/cmapi/docs/api/ch/ecma/csta/callcontrol/CallControlListener.html)

## 🏭 Production Deployment

### **Docker Deployment**
```dockerfile
FROM openjdk:21-jre-slim
COPY build/libs/cti-event-monitor-3.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### **Windows Service**
```bash
./gradlew createWindowsExe
# Install the generated .exe as a Windows service
```

### **Systemd Service (Linux)**
```ini
[Unit]
Description=CTI Event Monitor
After=network.target

[Service]
Type=simple
User=cti
ExecStart=/usr/local/bin/java -jar /opt/cti/cti-event-monitor-3.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

## 🔧 Development

### **Project Structure**
```
src/main/java/com/example/cti/
├── events/                 # ECMA-269 CSTA event classes
│   ├── CSTAEvent.java     # Base CSTA event
│   ├── DeliveredEvent.java
│   ├── EstablishedEvent.java
│   └── CallClearedEvent.java
├── dmcc/                  # DMCC integration
│   ├── DMCCConnection.java
│   ├── CSTACallControlListener.java
│   └── EventDiscovery.java
├── connect/               # External system integration
└── CTIEventMonitorApplication.java
```

### **Adding New Event Types**
1. Create event class extending `CSTAEvent`
2. Implement required ECMA-269 methods
3. Add to `CSTACallControlListener`
4. Update documentation

### **Contributing**
1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Ensure ECMA-269 compliance
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Support

- **Issues**: [GitHub Issues](https://github.com/MingcongQi/MyNewProject/issues)
- **Documentation**: [Project Wiki](https://github.com/MingcongQi/MyNewProject/wiki)
- **ECMA-269 Standard**: [Official Documentation](https://www.ecma-international.org/computer-supported-telecommunications-applications-csta/)

## 🎯 Roadmap

- [ ] **Enhanced Event Types**: Additional CSTA events
- [ ] **Real-time Dashboard**: Web-based monitoring interface
- [ ] **Event Analytics**: Historical event analysis
- [ ] **Multi-tenant Support**: Support for multiple Avaya systems
- [ ] **Cloud Integration**: Enhanced cloud service integrations

---

**Built with ❤️ for the CTI community | ECMA-269 CSTA Phase III Compliant**
