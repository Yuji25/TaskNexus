# TaskNexus - Advanced Task Management System 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A robust, production-ready Task Management System built with Spring Boot 3.x, featuring JWT authentication, real-time email notifications via Kafka, file upload capabilities, comprehensive API documentation with Swagger, and advanced security features.

---

## ✨ Features

### Core Features
- **User Authentication & Authorization**: Secure JWT-based authentication with role-based access control
- **Task Management**: Complete CRUD operations for tasks with advanced filtering and pagination
- **File Upload**: Attach files to tasks (supports up to 10MB per file)
- **Email Notifications**: Automated email notifications for task operations via Apache Kafka
- **Real-time Updates**: Asynchronous task processing with event-driven architecture
- **API Documentation**: Interactive Swagger UI for API exploration and testing
- **Rate Limiting**: Built-in protection against abuse (100 requests/minute)
- **Health Monitoring**: Spring Boot Actuator endpoints for application health checks

### Advanced Features
- **Pagination & Sorting**: Efficient data retrieval with customizable page sizes
- **Search & Filter**: Advanced filtering by status, priority, due date, and assignee
- **Audit Trail**: Track creation and update timestamps for all entities
- **Password Management**: Secure password change functionality
- **Profile Management**: User profile updates with validation
- **Analytics**: Task statistics and productivity metrics

---

## 🛠️ Tech Stack

### Backend Framework
- **Spring Boot 3.5.10**: Core application framework
- **Spring Security 6.x**: Authentication and authorization
- **Spring Data JPA**: Database interaction and ORM
- **Spring Mail**: Email functionality
- **Spring Kafka**: Asynchronous messaging
- **Spring Boot Actuator**: Application monitoring

### Database
- **H2 Database**: In-memory database for development (easily switchable to PostgreSQL/MySQL)

### Security
- **JWT (JSON Web Tokens)**: Stateless authentication
- **BCrypt**: Password hashing
- **CORS Configuration**: Cross-Origin Resource Sharing support

### API Documentation
- **SpringDoc OpenAPI 3**: Swagger UI integration
- **Swagger UI 5.10.3**: Interactive API documentation

### Messaging & Events
- **Apache Kafka 3.9.1**: Event streaming platform for email notifications

### Utilities
- **Lombok**: Reduce boilerplate code
- **Hibernate Validator**: Input validation
- **Jackson**: JSON processing
- **Bucket4j**: Rate limiting

### Build & Deployment
- **Maven**: Dependency management and build automation
- **Docker**: Containerization support
- **Java 21**: Latest LTS version

---

## 🏗️ Architecture

TaskNexus follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│     (REST Controllers + DTOs)       │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Service Layer               │
│    (Business Logic + Services)      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Persistence Layer              │
│  (Repositories + JPA Entities)      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Database (H2)               │
└─────────────────────────────────────┘

         ┌───────────────┐
         │  Kafka Topic  │
         │ (task-events) │
         └───────┬───────┘
                 │
         ┌───────▼───────┐
         │ Email Service │
         └───────────────┘
```

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
  - [Download Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
  - [Download OpenJDK](https://adoptium.net/)
- **Apache Maven 3.8+**
  - [Download Maven](https://maven.apache.org/download.cgi)
- **Apache Kafka** (for email notifications)
  - [Download Kafka](https://kafka.apache.org/downloads)
- **Docker & Docker Compose** (optional, for containerized deployment)
  - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Git** (for version control)
  - [Download Git](https://git-scm.com/downloads)

### System Requirements
- **RAM**: Minimum 4GB, Recommended 8GB
- **Disk Space**: 500MB for application and dependencies
- **OS**: Windows 10/11, macOS 10.15+, or Linux (Ubuntu 20.04+)

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Yuji25/TaskNexus.git
cd TaskNexus
```

### 2. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# JWT Secret (Change this to your own secure secret)
jwt.secret=your_jwt_secret_key_at_least_256_bits_long_for_maximum_security_12345
jwt.expiration=86400000

# Gmail SMTP Configuration (Use your Gmail credentials)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Kafka Configuration (Ensure Kafka is running)
spring.kafka.bootstrap-servers=localhost:9092
```

### 3. Install Dependencies

```bash
mvn clean install
```

This will download all required dependencies and build the project.

---

## ⚙️ Configuration

### JWT Configuration

Generate a secure JWT secret key (minimum 256 bits):

```bash
openssl rand -base64 32
```

Update `jwt.secret` in `application.properties` with the generated key.

### Email Configuration

**Using Gmail:**

1. Enable 2-Factor Authentication in your Google Account
2. Generate an App Password:
   - Go to Google Account → Security → App Passwords
   - Generate a new app password for "Mail"
3. Update `spring.mail.username` and `spring.mail.password` in `application.properties`

**Using Other SMTP Providers:**

```properties
spring.mail.host=smtp.your-provider.com
spring.mail.port=587
spring.mail.username=your-email
spring.mail.password=your-password
```

### Kafka Configuration

**Start Kafka (Local Development):**

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Server (in a new terminal)
bin/kafka-server-start.sh config/server.properties

# Create topic (optional - auto-created by default)
bin/kafka-topics.sh --create --topic task-events --bootstrap-server localhost:9092
```

### Database Configuration

By default, H2 in-memory database is used. To switch to PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasknexus
spring.datasource.username=postgres
spring.datasource.password=your-password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

## 🏃 Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using JAR

```bash
# Build the JAR
mvn clean package -DskipTests

# Run the JAR
java -jar target/TaskNexus-0.0.1-SNAPSHOT.jar
```

### Verify Application is Running

```bash
curl http://localhost:8080/api/v1/actuator/health
```

Expected Response:
```json
{
  "status": "UP"
}
```

The application will be available at: **http://localhost:8080/api/v1**

---

## 🐳 Docker Deployment

### Build Docker Image

```bash
# Build the application JAR first
mvn clean package -DskipTests

# Build Docker image
docker build -t tasknexus:latest .
```

### Run Docker Container

```bash
docker run -d \
  -p 8080:8080 \
  --name tasknexus-app \
  tasknexus:latest
```

### View Container Logs

```bash
docker logs -f tasknexus-app
```

### Stop Container

```bash
docker stop tasknexus-app
docker rm tasknexus-app
```

---

## 📚 API Documentation

### Swagger UI

Access interactive API documentation at:

**http://localhost:8080/api/v1/swagger-ui.html**

### OpenAPI Specification

Raw OpenAPI JSON specification:

**http://localhost:8080/api/v1/v3/api-docs**

### Key API Endpoints

#### Authentication APIs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Login and get JWT token | No |

#### Task Management APIs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/tasks` | Create a new task | Yes |
| GET | `/tasks` | Get all tasks (paginated) | Yes |
| GET | `/tasks/{id}` | Get task by ID | Yes |
| PUT | `/tasks/{id}` | Update a task | Yes |
| DELETE | `/tasks/{id}` | Delete a task | Yes |
| PATCH | `/tasks/{id}/status` | Update task status | Yes |
| POST | `/tasks/{id}/upload` | Upload file to task | Yes |

#### User Profile APIs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/users/me` | Get current user profile | Yes |
| PUT | `/users/me` | Update user profile | Yes |
| PUT | `/users/me/password` | Change password | Yes |
| DELETE | `/users/me` | Deactivate account | Yes |

#### Analytics APIs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/analytics/stats` | Get task statistics | Yes |

#### Health Check APIs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/actuator/health` | Application health status | No |
| GET | `/actuator/info` | Application information | No |

---

## 🧪 Testing

### Import Postman Collection

A comprehensive Postman collection is available at:

**`others/TaskNexus-API-Collection.postman_collection.json`**

**Features:**
- Pre-configured requests for all APIs
- Automatic JWT token management
- Environment variables setup
- Sample request payloads

### Testing Workflow

1. **Register a User**
   ```json
   POST /api/v1/auth/register
   {
     "username": "testuser",
     "email": "test@example.com",
     "password": "Password@123",
     "firstName": "Test",
     "lastName": "User"
   }
   ```

2. **Login**
   ```json
   POST /api/v1/auth/login
   {
     "username": "testuser",
     "password": "Password@123"
   }
   ```

3. **Create a Task** (using JWT token from login)
   ```json
   POST /api/v1/tasks
   Authorization: Bearer <your-jwt-token>
   {
     "title": "Complete Project",
     "description": "Finish TaskNexus implementation",
     "priority": "HIGH",
     "dueDate": "2024-12-31T23:59:59"
   }
   ```

### Unit Testing

Run all tests:
```bash
mvn test
```

Run tests with coverage:
```bash
mvn clean test jacoco:report
```

---

## 📧 Email Notifications

Email notifications are sent automatically for the following events:

- ✅ Task Creation
- ✅ Task Update
- ✅ Task Deletion
- ✅ Task Status Change
- ✅ File Upload to Task
- ✅ Task Assignment
- ✅ Task Due Date Reminder

### Email Flow

```
Task Operation → Kafka Producer → task-events Topic → Kafka Consumer → Email Service → SMTP Server → User Email
```

### Email Templates

Emails include:
- Task title and description
- Operation performed
- Timestamp
- User information
- Direct link to task (future enhancement)

---

## 🔒 Security

### Authentication

- **JWT-based authentication**: Stateless and scalable
- **Token expiration**: 24 hours (configurable)
- **Refresh token**: Not implemented (future enhancement)

### Password Security

- **BCrypt hashing**: Industry-standard password encryption
- **Password validation**: Minimum 8 characters, complexity requirements
- **Password change**: Requires old password verification

### Authorization

- **Role-based access control**: USER and ADMIN roles
- **Method-level security**: `@PreAuthorize` annotations
- **Resource ownership**: Users can only access their own data

### CORS Configuration

CORS is enabled for development. For production, configure allowed origins:

```java
@Configuration
public class SecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Configure allowed origins
    }
}
```

---

## 🚦 Rate Limiting

### Configuration

- **Rate Limit**: 100 requests per minute per IP address
- **Strategy**: Token bucket algorithm (Bucket4j)
- **Response**: HTTP 429 (Too Many Requests)

### Customization

Modify rate limits in `application.properties`:

```properties
bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity=100
bucket4j.filters[0].rate-limits[0].bandwidths[0].time=1
bucket4j.filters[0].rate-limits[0].bandwidths[0].unit=minutes
```

---

## 🗄️ Database

### H2 Console

Access the H2 database console for debugging:

**URL**: http://localhost:8080/api/v1/h2-console

**Credentials:**
- **JDBC URL**: `jdbc:h2:mem:tasknexus`
- **Username**: `sa`
- **Password**: (leave empty)

### Entity Relationships

```
User (1) ──────── (N) Task
  │                    │
  └── id              └── id
      username            title
      email               description
      password            status
      role                priority
                         dueDate
                         createdAt
                         updatedAt
                         userId (FK)
                         assignedToId (FK)
```

### Database Migration

For production, use Flyway or Liquibase for database migrations:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

---

## 📁 Project Structure

```
TaskNexus/
├── src/
│   ├── main/
│   │   ├── java/org/example/tasknexus/
│   │   │   ├── config/               # Configuration classes
│   │   │   │   ├── OpenAPIConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/           # REST Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── TaskController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── AnalyticsController.java
│   │   │   │   └── WelcomeController.java
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── TaskDTO.java
│   │   │   │   ├── UserDTO.java
│   │   │   │   └── ApiResponse.java
│   │   │   ├── exception/            # Exception Handlers
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   └── ValidationException.java
│   │   │   ├── model/                # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Task.java
│   │   │   │   ├── Role.java (enum)
│   │   │   │   ├── TaskStatus.java (enum)
│   │   │   │   └── TaskPriority.java (enum)
│   │   │   ├── repository/           # Data Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── TaskRepository.java
│   │   │   ├── security/             # Security Components
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── service/              # Business Logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── TaskService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   ├── KafkaProducerService.java
│   │   │   │   ├── KafkaConsumerService.java
│   │   │   │   └── FileStorageService.java
│   │   │   ├── util/                 # Utility Classes
│   │   │   │   └── JwtUtil.java
│   │   │   └── TaskNexusApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/              # Static resources
│   └── test/                        # Unit & Integration Tests
│       └── java/org/example/tasknexus/
│           └── TaskNexusApplicationTests.java
├── uploads/                         # File upload directory
├── others/                          # Documentation & Resources
│   ├── TaskNexus Guidelines.md
│   ├── WORKFLOW.md
│   └── TaskNexus-API-Collection.postman_collection.json
├── target/                          # Build output
├── Dockerfile                       # Docker configuration
├── .gitignore
├── pom.xml                         # Maven dependencies
└── README.md                       # This file
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Port Already in Use

**Error**: `Web server failed to start. Port 8080 was already in use.`

**Solution**:
```bash
# Find process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Mac/Linux

# Kill the process or change port in application.properties
server.port=8081
```

#### 2. Kafka Connection Failed

**Error**: `Failed to connect to Kafka at localhost:9092`

**Solution**:
- Ensure Kafka is running
- Check Kafka is listening on port 9092
- Verify `spring.kafka.bootstrap-servers` configuration

```bash
# Start Kafka
bin/kafka-server-start.sh config/server.properties
```

#### 3. Email Not Sending

**Error**: `AuthenticationFailedException: 535 Username and Password not accepted`

**Solution**:
- Verify Gmail credentials are correct
- Use App Password (not regular password)
- Enable "Less secure app access" (if not using 2FA)
- Check SMTP settings

#### 4. JWT Token Expired

**Error**: `401 Unauthorized - JWT token has expired`

**Solution**:
- Login again to get a new token
- Increase token expiration time in `application.properties`
- Implement refresh token mechanism

#### 5. Database Connection Failed

**Error**: `Failed to configure a DataSource`

**Solution**:
- Check H2 is enabled in dependencies
- Verify `spring.datasource.url` configuration
- Ensure `spring.h2.console.enabled=true`

#### 6. Maven Build Fails

**Error**: `Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin`

**Solution**:
```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn clean install -U

# Skip tests
mvn clean install -DskipTests
```