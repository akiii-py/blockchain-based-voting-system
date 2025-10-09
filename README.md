# 🗳️ Blockchain-Based E-Voting System

A secure, transparent, and tamper-proof electronic voting system built using blockchain technology with End-to-End Verifiability (E2E-V) and cryptographic receipts.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Security Features](#security-features)
- [Deployment](#deployment)

## ✨ Features

- **Blockchain Integration**: Immutable vote storage using blockchain principles
- **E2E-V (End-to-End Verifiability)**: Voters can verify their vote at every stage
- **Cryptographic Receipts**: Unique receipt generation for vote verification
- **Multi-Role System**: Admin, Voter, and Candidate modules
- **Real-time Results**: Transparent vote tallying and visualization
- **Security Measures**: Double-voting prevention, voter eligibility checks, timestamp validation

## 🛠️ Technology Stack

### Backend
- **Java 17** - Primary programming language
- **Spring Boot 3.1.0** - Web framework
- **Spring Security** - Authentication and authorization
- **PostgreSQL** - Database
- **JWT** - Token-based authentication
- **Web3j** - Ethereum blockchain integration
- **Maven** - Build tool

### Security
- **Bouncy Castle** - Cryptographic operations
- **BCrypt** - Password hashing
- **JWT Tokens** - Secure authentication

### Development Tools
- **JUnit** - Unit testing
- **Mockito** - Mocking framework
- **JaCoCo** - Code coverage

## 📋 Prerequisites

1. **Java 17+**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **PostgreSQL 14+**
   ```bash
   psql --version
   ```

4. **Git** (optional)
   ```bash
   git --version
   ```

## 🚀 Quick Start

### 1. Clone and Setup Database
```bash
# Create PostgreSQL database
createdb evoting_db

# Or using psql
psql -U postgres
CREATE DATABASE evoting_db;
\q
```

### 2. Build and Run
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Test the System
Follow the comprehensive testing guide in [`TESTING_GUIDE.md`](TESTING_GUIDE.md)

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Admin Endpoints
- `POST /api/admin/elections` - Create election
- `PUT /api/admin/elections/{id}/activate` - Activate election
- `PUT /api/admin/elections/{id}/deactivate` - Deactivate election

### Election Management
- `GET /api/elections/active` - Get active elections
- `GET /api/elections/{id}/candidates` - Get candidates for election
- `POST /api/elections/{electionId}/candidates` - Add candidate to election (Admin)

### Voting Operations
- `POST /api/voting/vote` - Cast vote
- `GET /api/voting/election/{electionId}/votes` - Get votes for election
- `GET /api/voting/user/{userId}/election/{electionId}/status` - Check if user voted

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### Manual Testing
See [`TESTING_GUIDE.md`](TESTING_GUIDE.md) for complete end-to-end testing instructions.

## 📁 Project Structure

```
blockchain-voting-system/
├── src/
│   ├── main/
│   │   ├── java/com/evoting/blockchainvotingsystem/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── model/              # JPA entities
│   │   │   ├── repository/         # Data repositories
│   │   │   ├── service/            # Business logic
│   │   │   ├── config/             # Configuration classes
│   │   │   ├── dto/                # Data transfer objects
│   │   │   ├── util/               # Utility classes
│   │   │   └── BlockchainVotingSystemApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/             # Static resources
│   └── test/                       # Test classes
├── pom.xml                         # Maven configuration
├── TESTING_GUIDE.md               # Complete testing guide
├── commands.txt                   # Setup and run commands
└── README.md                      # This file
```

## 🔒 Security Features

- **Password Hashing**: BCrypt for secure password storage
- **JWT Authentication**: Token-based secure authentication
- **Role-Based Access Control**: Admin and User roles
- **Input Validation**: Comprehensive input sanitization
- **CORS Configuration**: Cross-origin resource sharing setup
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Input validation and encoding

## 🚀 Deployment

### Production Build
```bash
# Build for production
mvn clean package -DskipTests

# Run the JAR
java -jar target/blockchain-voting-system-1.0.0.jar
```

### Environment Variables
```bash
export SPRING_DATASOURCE_USERNAME=your_db_user
export SPRING_DATASOURCE_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret_key
export SPRING_PROFILES_ACTIVE=prod
```

### Docker Deployment (Future Enhancement)
```dockerfile
FROM openjdk:17-jdk-alpine
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- Web3j for Ethereum integration
- PostgreSQL for reliable database
- All contributors and testers

## 📞 Support

For support, email support@evoting-system.com or create an issue in the repository.

---

**🎉 Happy Voting! Your voice matters in the blockchain era.**
