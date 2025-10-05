# Blockchain-Based E-Voting System

A secure, transparent, and tamper-proof electronic voting system built with Spring Boot, JWT authentication, and blockchain integration.

## Features

- **Secure Authentication**: JWT-based authentication with role-based access control
- **Blockchain Integration**: Votes are recorded on the blockchain for immutability
- **Real-time Voting**: Live election monitoring and results
- **Admin Dashboard**: Election management and user administration
- **Voter Verification**: Unique voter ID system to prevent double voting
- **Audit Trail**: Complete transaction history on blockchain

## Technology Stack

- **Backend**: Spring Boot 3.1.0, Java 17
- **Database**: MySQL 8.0
- **Security**: Spring Security, JWT, BCrypt
- **Blockchain**: Web3j, Ethereum
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Ethereum node (Ganache for development)

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/blockchain-voting-system.git
   cd blockchain-voting-system
   ```

2. **Configure Database**
   - Create a MySQL database named `evoting_db`
   - Update database credentials in `src/main/resources/application.properties`

3. **Configure Blockchain**
   - Install and run Ganache (local Ethereum network)
   - Update blockchain configuration in `application.properties`

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Elections
- `GET /api/elections/active` - Get active elections
- `GET /api/elections/{id}` - Get election details
- `GET /api/elections/{id}/candidates` - Get election candidates

### Voting
- `POST /api/voting/vote` - Cast a vote
- `GET /api/voting/election/{electionId}/votes` - Get election votes

### Admin (Requires ADMIN role)
- `POST /api/admin/users` - Create user
- `POST /api/admin/elections` - Create election
- `PUT /api/admin/elections/{id}/activate` - Activate election

## Security Features

- **JWT Authentication**: Stateless authentication with token expiration
- **Password Encryption**: BCrypt hashing for secure password storage
- **Role-based Access**: Different permissions for voters and administrators
- **Input Validation**: Comprehensive validation using Bean Validation
- **CORS Configuration**: Configured for frontend integration

## Blockchain Integration

The system integrates with Ethereum blockchain to ensure:
- **Immutability**: Once recorded, votes cannot be altered
- **Transparency**: All transactions are publicly verifiable
- **Security**: Cryptographic proof of vote authenticity

## Database Schema

### Users Table
- id (Primary Key)
- username (Unique)
- password (Encrypted)
- email (Unique)
- role (USER/ADMIN)
- voter_id (Unique)
- has_voted (Boolean)

### Elections Table
- id (Primary Key)
- title
- description
- start_time
- end_time
- is_active

### Candidates Table
- id (Primary Key)
- name
- party
- description
- election_id (Foreign Key)
- vote_count

### Votes Table
- id (Primary Key)
- user_id (Foreign Key)
- candidate_id (Foreign Key)
- election_id (Foreign Key)
- transaction_hash (Blockchain)
- voted_at (Timestamp)

## Testing

Run the tests using:
```bash
mvn test
```

## Deployment

1. **Build the application**
   ```bash
   mvn clean package
   ```

2. **Run with production profile**
   ```bash
   java -jar target/blockchain-voting-system-1.0.0.jar --spring.profiles.active=prod
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

For support and questions, please open an issue on GitHub.
