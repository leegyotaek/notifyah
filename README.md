# 🚀 NotiFyah

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7-blue.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-WTFPL-red.svg)](http://www.wtfpl.net/)

**Real-time Notification System** built with Spring Boot 3.2, featuring Apache Kafka for message streaming, WebSocket for real-time delivery, and PostgreSQL for persistence.

## ✨ Features

- 🔔 **Real-time Notifications** - Instant delivery using WebSocket technology
- 📨 **Kafka Integration** - High-throughput message streaming with fault tolerance
- 🔐 **JWT Authentication** - Secure stateless authentication
- 🗄️ **PostgreSQL** - Reliable ACID-compliant data persistence
- 🌐 **RESTful API** - Clean, documented REST endpoints
- 📱 **WebSocket Client** - Built-in test client for real-time testing
- 📚 **OpenAPI Documentation** - Interactive API documentation with Swagger UI
- 🐳 **Docker Support** - Easy local development setup

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Client    │    │   Spring Boot   │    │   PostgreSQL    │
│   (WebSocket)   │◄──►│   Application   │◄──►│   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Apache Kafka  │
                       │   (Message Bus) │
                       └─────────────────┘
```

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.2, Spring Security, Spring Data JPA
- **Messaging**: Apache Kafka 3.7 (KRaft mode)
- **Database**: PostgreSQL 15
- **Real-time**: WebSocket with JWT authentication
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Gradle 8.x
- **Java Version**: 17
- **Documentation**: OpenAPI 3.0 (Swagger UI)

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/notifyah.git
cd notifyah
```

### 2. Start Infrastructure Services

```bash
# Start PostgreSQL, Kafka, and Kafka UI
docker compose up -d

# Verify services are running
docker compose ps
```

**Services will be available at:**
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`
- Kafka UI: `http://localhost:8081`

### 3. Run the Application

```bash
# Build and run the application
./gradlew bootRun
```

The application will start at `http://localhost:8080`

### 4. Test the System

#### Access the Test Client
Open your browser and navigate to:
- **Main Page**: `http://localhost:8080/`
- **Notification Client**: `http://localhost:8080/notification-client.html`
- **API Documentation**: `http://localhost:8080/swagger-ui.html`

#### Quick Test Flow
1. **Sign up** with a new user account
2. **Login** to get a JWT token
3. **Connect WebSocket** for real-time notifications
4. **Send test notifications** to yourself or other users
5. **View real-time delivery** via WebSocket

## 📖 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/signup` | User registration |
| `POST` | `/auth/login` | User authentication |
| `GET` | `/auth/token/{userId}` | Generate token for testing |

### Notification Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/notifications` | Get user notifications (paginated) |
| `GET` | `/api/notifications/unread-count` | Get unread count |
| `PATCH` | `/api/notifications/{id}/read` | Mark notification as read |
| `PATCH` | `/api/notifications/read-all` | Mark all as read |
| `DELETE` | `/api/notifications/{id}` | Delete notification |
| `POST` | `/api/notifications/send` | Send notification to user |

### WebSocket

- **Endpoint**: `ws://localhost:8080/ws/notifications`
- **Authentication**: JWT token via query parameter `?token=<jwt>`
- **Real-time**: Instant notification delivery to connected clients

### Debug Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/debug/events/comment` | Publish test Kafka event |

## 🔧 Configuration

### Application Properties

Key configuration in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notifyah
    username: notifyah
    password: notifyah
  
  kafka:
    bootstrap-servers: localhost:9092
    
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: your-secret-key-here
  expiration: 86400000 # 24 hours
```

### Docker Compose

The `docker-compose.yml` includes:
- **PostgreSQL 15** with persistent volume
- **Apache Kafka 3.7** in KRaft mode (single broker)
- **Kafka UI** for monitoring and testing

## 🧪 Testing

### Manual Testing

1. **Start the application** with `./gradlew bootRun`
2. **Open the test client** at `/notification-client.html`
3. **Create test users** and send notifications
4. **Verify real-time delivery** via WebSocket

### API Testing

1. **Use Swagger UI** at `/swagger-ui.html`
2. **Authenticate** with JWT token
3. **Test all endpoints** interactively

### Kafka Testing

1. **Access Kafka UI** at `http://localhost:8081`
2. **Monitor topics** and message flow
3. **Use debug endpoint** to publish test events

## 📁 Project Structure

```
src/main/java/com/notifyah/
├── auth/                    # Authentication & authorization
│   ├── controller/         # Auth endpoints
│   ├── dto/               # Auth DTOs
│   └── service/            # Auth business logic
├── common/                 # Shared components
│   ├── exception/          # Global exception handling
│   └── security/           # JWT utilities
├── config/                 # Configuration classes
├── debug/                  # Debug/testing endpoints
├── notification/            # Core notification system
│   ├── controller/         # Notification REST API
│   ├── dto/               # Notification DTOs
│   ├── entity/            # JPA entities
│   ├── listener/          # Kafka consumers
│   ├── repository/        # Data access layer
│   └── service/           # Business logic
├── user/                   # User management
│   ├── entity/            # User entities
│   ├── repository/        # User data access
│   └── service/           # User business logic
└── websocket/              # WebSocket handling
    └── NotificationWebSocketHandler.java
```

## 🚀 Deployment

### Production Considerations

1. **Environment Variables** - Use proper secrets management
2. **Database** - Configure production PostgreSQL with proper credentials
3. **Kafka** - Set up multi-broker cluster for production
4. **Security** - Configure CORS, rate limiting, and security headers
5. **Monitoring** - Add health checks and metrics

### Docker Deployment

```bash
# Build the application
./gradlew build

# Run with Docker
docker run -p 8080:8080 notifyah:latest
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the **WTFPL - Do What The Fuck You Want To Public License**.

**WTFPL** - You can do whatever the fuck you want with this software!

See [LICENSE](LICENSE) file for full details.

## ☕ Support the Project

If you find this software useful, consider supporting the development:

- **☕ Buy Me a Coffee**: [https://buymeacoffee.com/hitaek](https://buymeacoffee.com/hitaek)


## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Apache Kafka community for the robust messaging system
- PostgreSQL team for the reliable database
- All contributors and users of NotiFyah

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/notifyah/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/notifyah/discussions)
- **Wiki**: [Project Wiki](https://github.com/yourusername/notifyah/wiki)

---

**Made with ❤️ by the NotiFyah community**

*Real-time notifications made simple and scalable.*

---

**WTFPL License**: Do What The Fuck You Want To Public License - Version 2

Copyright (c) 2025 NotiFyah

This program is free software. It comes without any warranty, to the extent
permitted by applicable law. You can redistribute it and/or modify it under
the terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See http://www.wtfpl.net/ for more details. 