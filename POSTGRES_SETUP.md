# PostgreSQL Setup Guide for Blockchain E-Voting System

This guide will help you set up PostgreSQL database for the Spring Boot Blockchain E-Voting System on your local machine.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (see installation instructions below)

## PostgreSQL Installation

### For macOS (using Homebrew)

```bash
# Install PostgreSQL
brew install postgresql

# Start PostgreSQL service
brew services start postgresql

# Create database and user
createdb evoting_db
createuser postgres
psql -c "ALTER USER postgres PASSWORD 'password';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE evoting_db TO postgres;"
```

### For Windows

1. Download PostgreSQL from: https://www.postgresql.org/download/windows/
2. Run the installer and follow the setup wizard
3. During installation:
   - Set password for postgres user as: `password`
   - Keep default port: `5432`
   - Create database: `evoting_db`

### For Linux (Ubuntu/Debian)

```bash
# Update package list
sudo apt update

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Switch to postgres user and create database
sudo -u postgres psql -c "CREATE DATABASE evoting_db;"
sudo -u postgres psql -c "CREATE USER postgres WITH PASSWORD 'password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE evoting_db TO postgres;"
```

## Database Configuration Verification

After installation, verify the setup:

```bash
# Connect to PostgreSQL
psql -U postgres -d evoting_db -h localhost -p 5432

# You should see a prompt like: evoting_db=#
# Type \q to exit
```

## Application Configuration

The application is configured to use these database settings (in `src/main/resources/application.properties`):

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/evoting_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
```

## Running the Application

1. Make sure PostgreSQL is running
2. Navigate to the project directory
3. Run the Spring Boot application:

```bash
mvn spring-boot:run
```

## Troubleshooting

### Connection Issues

If you get connection errors:

1. **Check if PostgreSQL is running:**
   ```bash
   # macOS
   brew services list | grep postgresql

   # Linux
   sudo systemctl status postgresql

   # Windows: Check Services panel
   ```

2. **Verify database exists:**
   ```bash
   psql -U postgres -l
   ```

3. **Test connection:**
   ```bash
   psql -U postgres -d evoting_db -c "SELECT version();"
   ```

### Port Conflicts

If port 5432 is already in use:

1. Find what's using the port:
   ```bash
   # macOS/Linux
   lsof -i :5432

   # Windows
   netstat -ano | findstr :5432
   ```

2. Either stop the conflicting service or change PostgreSQL port in `postgresql.conf`

### Permission Issues

If you get permission errors:

```bash
# macOS/Linux
sudo -u postgres createdb evoting_db
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE evoting_db TO postgres;"
```

## Alternative: Using Docker

If you prefer using Docker:

```bash
# Run PostgreSQL in Docker
docker run --name postgres-evoting \
  -e POSTGRES_DB=evoting_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:13

# To stop and remove
docker stop postgres-evoting
docker rm postgres-evoting
```

## Database Schema

The application uses Hibernate with `ddl-auto=update`, so tables will be created automatically when you first run the application. The main tables are:

- `users` - User accounts
- `elections` - Election information
- `candidates` - Election candidates
- `votes` - Vote records

## Next Steps

After setting up PostgreSQL:

1. Run the application: `mvn spring-boot:run`
2. Access the API at: http://localhost:8080/api
3. Use the Postman collection in `postman/` directory for testing
4. Check the README.md for more detailed instructions

## Support

If you encounter issues:

1. Check the application logs for detailed error messages
2. Verify your PostgreSQL version is compatible (9.6+ recommended)
3. Ensure the database credentials match exactly as specified
4. Check firewall settings if connection is blocked
