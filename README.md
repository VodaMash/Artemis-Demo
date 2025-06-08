# Kratos - Voucher Management System

A Spring Boot application that provides asynchronous voucher management capabilities using Apache ActiveMQ Artemis
message broker and H2 database.

## Overview

Kratos is a microservice designed to handle voucher operations (create, redeem, expire) through an event-driven
architecture. It uses JMS (Java Message Service) for asynchronous processing, ensuring scalable and reliable voucher
management.

## Features

- **Asynchronous Voucher Processing**: Operations are queued and processed asynchronously
- **RESTful API**: Clean REST endpoints for voucher management
- **Message-Driven Architecture**: Uses Apache ActiveMQ Artemis for reliable message processing
- **Database Persistence**: H2 in-memory database with JPA/Hibernate
- **Error Handling**: Comprehensive exception handling and validation
- **Logging**: Detailed logging for monitoring and debugging

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.0**
- **Spring JMS** - Message processing
- **Apache ActiveMQ Artemis 2.33.0** - Message broker
- **Spring Data JPA** - Database access
- **H2 Database** - In-memory database
- **Lombok** - Reducing boilerplate code
- **Maven** - Build and dependency management
- **Docker Compose** - Container orchestration

## Architecture

The application follows an event-driven architecture:

1. **Controller Layer**: Receives HTTP requests and initiates async operations
2. **Service Layer**: Business logic and message publishing
3. **Producer**: Sends messages to JMS queue
4. **Consumer**: Processes messages from JMS queue
5. **Repository Layer**: Database operations

```
HTTP Request → Controller → Service → Producer → JMS Queue → Consumer → Database
```

## Project Structure

```
src/main/java/com/mash/kratos/
├── config/
│   └── JmsConfig.java              # JMS configuration
├── consumer/
│   └── VoucherConsumer.java        # Message consumer
├── controller/
│   └── VoucherController.java      # REST endpoints
├── dto/
│   ├── VoucherAction.java          # Action enum
│   └── VoucherMessage.java         # Message DTO
├── entity/
│   ├── Voucher.java                # Voucher entity
│   └── VoucherStatus.java          # Status enum
├── exception/
│   ├── GlobalExceptionHandler.java # Global error handling
│   ├── JmsErrorHandler.java        # JMS error handling
│   └── [Custom Exceptions]
├── producer/
│   └── VoucherProducer.java        # Message producer
├── repository/
│   └── VoucherRepository.java      # Data access
├── service/
│   └── VoucherService.java         # Business logic
└── KratosApplication.java          # Main application
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (for ActiveMQ Artemis)

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd kratos
   ```

2. **Start ActiveMQ Artemis**

   ```bash
   docker-compose up -d
   ```

3. **Build the application**

   ```bash
   ./mvnw clean compile
   ```

4. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Docker Services

- **ActiveMQ Artemis**: Available at `http://localhost:8161` (admin/admin)
- **JMS Port**: `61616`

## API Endpoints

### Get All Vouchers

```http
GET /vouchers
```

### Get Voucher by Code

```http
GET /vouchers/{voucherCode}
```

### Create Voucher (Async)

```http
POST /vouchers/create
Content-Type: application/x-www-form-urlencoded

voucherCode=SAVE20&description=20% Off&amount=20.0
```

### Redeem Voucher (Async)

```http
POST /vouchers/redeem
Content-Type: application/x-www-form-urlencoded

voucherCode=SAVE20
```

### Expire Voucher (Async)

```http
POST /vouchers/expire
Content-Type: application/x-www-form-urlencoded

voucherCode=SAVE20
```

## Usage Examples

### Create a Voucher

```bash
curl -X POST http://localhost:8080/vouchers/create \
  -d "voucherCode=WELCOME10&description=Welcome Discount&amount=10.0"
```

### Get All Vouchers

```bash
curl http://localhost:8080/vouchers
```

### Redeem a Voucher

```bash
curl -X POST http://localhost:8080/vouchers/redeem \
  -d "voucherCode=WELCOME10"
```

## Configuration

### Application Properties

Key configurations in `application.yml`:

```yaml
server:
  port: 8080

spring:
  artemis:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin

  datasource:
    url: jdbc:h2:file:./data/kratos-db
    username: sa
    password: password

  h2:
    console:
      enabled: true
```

### H2 Database Console

Access the H2 console at `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:file:./data/kratos-db`
- **Username**: `sa`
- **Password**: `password`

## Voucher States

Vouchers can be in one of three states:

- **ACTIVE**: Newly created vouchers ready for redemption
- **REDEEMED**: Vouchers that have been successfully used
- **EXPIRED**: Vouchers that have been marked as expired

## Message Flow

1. **Create Voucher**: Controller → Producer → Queue → Consumer → Database
2. **Redeem Voucher**: Controller → Producer → Queue → Consumer → Database (status update)
3. **Expire Voucher**: Controller → Producer → Queue → Consumer → Database (status update)

## Error Handling

The application includes comprehensive error handling:

- **VoucherNotFoundException**: When voucher doesn't exist
- **VoucherAlreadyExistsException**: When trying to create duplicate voucher
- **InvalidVoucherStatusException**: When invalid state transitions are attempted
- **JmsErrorHandler**: For message processing errors
- **GlobalExceptionHandler**: Centralized exception handling

## Monitoring and Logging

- **Logging**: Configured with SLF4J and Logback
- **Actuator**: Spring Boot Actuator endpoints available
- **H2 Console**: Database monitoring and querying
- **ActiveMQ Console**: Message broker monitoring

## Development

### Building

```bash
./mvnw clean compile
```

### Testing

```bash
./mvnw test
```

### Packaging

```bash
./mvnw clean package
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please create an issue in the repository.

---

**Note**: This application uses an in-memory H2 database. Data will be persisted to the `./data/kratos-db.mv.db` file
but consider using a production database for production deployments.
