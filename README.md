# AWS Lambda HTTP Client - Pure Java Application

A lightweight, pure Java application that sends HTTP requests to AWS Lambda functions via Function URLs. **No ports opened** - this is a true client application that doesn't act as a web server.

## Features

- Pure Java 21 application (no Spring Boot)
- No web server - doesn't open any ports
- Interactive console interface
- Command-line interface support
- Configurable Lambda function URL
- JSON request/response handling
- Health check functionality
- Windows executable generation
- Cross-platform compatibility

## Prerequisites

- Java 21 or higher
- Gradle 8.0+
- AWS Lambda function with Function URL enabled
- (Optional) AWS credentials for enhanced security

## Project Structure

```
lambda-client-app/
├── build.gradle
├── src/
│   ├── main/
│   │   ├── java/com/example/lambdaclient/
│   │   │   ├── LambdaClientApplication.java
│   │   │   ├── config/
│   │   │   │   └── LambdaConfig.java
│   │   │   ├── controller/
│   │   │   │   └── LambdaController.java
│   │   │   └── service/
│   │   │       └── LambdaService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── README.md
```

## Configuration

### Environment Variables

Set the following environment variables or update `application.yml`:

```bash
export LAMBDA_FUNCTION_URL=https://your-lambda-function-url.lambda-url.us-east-1.on.aws/
export AWS_REGION=us-east-1
export LAMBDA_TIMEOUT=30
```

### Application Properties

Update `src/main/resources/application.yml`:

```yaml
aws:
  lambda:
    function-url: https://your-actual-lambda-function-url.lambda-url.us-east-1.on.aws/
    region: us-east-1
    timeout-seconds: 30
```

## Installation & Running

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd lambda-client-app
```

### 2. Build the application
```bash
./gradlew build
```

### 3. Run the application
```bash
./gradlew bootRun
```

Or run the JAR file:
```bash
java -jar build/libs/lambda-client-app-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Health Check
```bash
GET http://localhost:8080/api/lambda/health
```

### Invoke Lambda with JSON payload
```bash
POST http://localhost:8080/api/lambda/invoke
Content-Type: application/json

{
  "message": "Hello from Spring Boot!",
  "data": {
    "key": "value"
  }
}
```

### Invoke Lambda with string payload
```bash
POST http://localhost:8080/api/lambda/invoke-string
Content-Type: application/json

"Hello Lambda!"
```

### Invoke Lambda with GET request
```bash
GET http://localhost:8080/api/lambda/invoke
```

## Example Lambda Function

Here's a simple Python Lambda function that works with this client:

```python
import json

def lambda_handler(event, context):
    print(f"Received event: {json.dumps(event)}")
    
    return {
        'statusCode': 200,
        'headers': {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*'
        },
        'body': json.dumps({
            'message': 'Hello from Lambda!',
            'received_data': event,
            'timestamp': context.aws_request_id
        })
    }
```

## Testing

Run tests with:
```bash
./gradlew test
```

## Building for Production

Create a production JAR:
```bash
./gradlew bootJar
```

## Windows Deployment

### Option 1: Download Pre-built Executable

1. Go to the [Releases page](https://github.com/MingcongQi/MyNewProject/releases)
2. Download the latest `LambdaClient.exe` or `LambdaClient.msi` installer
3. Run the executable directly (no Java installation required)

### Option 2: Build Windows Executable Locally

Requirements: JDK 21 installed

```cmd
# Build the executable
gradlew createWindowsExe

# The executable will be created in build/jpackage/output/
```

### Option 3: Running as Windows Service

1. Build the JAR file
2. Use a tool like NSSM (Non-Sucking Service Manager) to create a Windows service:

```cmd
nssm install LambdaClientApp
nssm set LambdaClientApp Application "C:\Program Files\Java\jdk-21\bin\java.exe"
nssm set LambdaClientApp AppParameters "-jar C:\path\to\lambda-client-app-0.0.1-SNAPSHOT.jar"
nssm set LambdaClientApp AppDirectory "C:\path\to\app"
nssm start LambdaClientApp
```

### GitHub Actions Automatic Builds

This repository is configured with GitHub Actions to automatically build Windows executables:

- **On every push to main**: Creates executable artifacts
- **On tagged releases**: Creates GitHub releases with downloadable executables
- **Manual trigger**: Can be triggered manually from the Actions tab

## Troubleshooting

### Common Issues

1. **Connection timeout**: Increase the timeout value in `application.yml`
2. **Lambda function not found**: Verify the Function URL is correct
3. **CORS issues**: Ensure your Lambda function returns proper CORS headers

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/spring.log
```

## License

MIT License
