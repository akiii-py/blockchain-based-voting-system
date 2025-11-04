# Java Swing Frontend for Blockchain E-Voting System

This is a complete Java Swing desktop application that provides a user-friendly interface for the Blockchain E-Voting System backend.

## Features

- **User Authentication**: Login and registration with JWT token management
- **Voter Dashboard**: View active elections and cast votes
- **Admin Panel**: Manage elections, candidates, and activate/deactivate elections
- **Vote Receipts**: Display cryptographic receipts after voting
- **Real-time Updates**: Refresh data from the backend API

## Prerequisites

1. Java 17 or higher
2. Maven 3.6+
3. Backend application running on `http://localhost:8080`
4. PostgreSQL database configured

## Building and Running

### Step 1: Ensure Backend is Running

Make sure your Spring Boot backend is running:
```bash
mvn spring-boot:run
```

The backend should be accessible at `http://localhost:8080/api`

### Step 2: Build the Project

The Swing client is part of the main Maven project, so dependencies are already included. Just build the project:

```bash
mvn clean compile
```

### Step 3: Run the Swing Client

**Option 1: Using Maven Exec Plugin (Recommended)**
```bash
# First, ensure all dependencies are downloaded
mvn clean compile

# Run with exec plugin (includes all dependencies)
mvn exec:java -Dexec.mainClass="com.evoting.swingclient.EVotingClient"
```

**Option 2: Using Java with Full Classpath**
```bash
# Compile all classes
mvn clean compile

# Get the classpath with all dependencies and run
mvn dependency:build-classpath -DincludeScope=runtime -q -Dmdep.outputFile=target/classpath.txt
java -cp "target/classes:$(cat target/classpath.txt)" com.evoting.swingclient.EVotingClient
```

**Option 3: Create a Fat JAR (Best for Distribution)**
```bash
# Create a runnable JAR with all dependencies
mvn clean package

# Run the JAR
java -jar target/blockchain-voting-system-1.0.0.jar
```

## Usage Guide

### For Voters

1. **Registration**: Click "Register" on the login screen
   - Fill in: Username, Password, Email, Full Name, Voter ID
   - Click "Register"

2. **Login**: Enter your username and password, then click "Login"

3. **View Elections**: The dashboard shows all active elections
   - Click "Refresh" to reload elections
   - Select an election from the table

4. **Cast Vote**: 
   - Select an election and click "Vote"
   - Choose a candidate from the list
   - Click "Submit Vote" and confirm
   - Save your cryptographic receipt for verification

### For Administrators

1. **Login**: Use admin credentials (username must be "admin" or have ADMIN role)

2. **Create Election**:
   - Go to Admin Panel
   - Click "Create Election"
   - Enter: Title, Description, Start Time, End Time (ISO format: `2024-01-15T09:00:00`)
   - Click "Create"

3. **Add Candidates**:
   - Select an election from the table
   - Click "Add Candidate"
   - Enter: Name, Party, Description
   - Click "Add"

4. **Activate/Deactivate Elections**:
   - Select an election
   - Click "Activate" or "Deactivate"

5. **View Candidates**:
   - Go to "Candidates" tab
   - Select an election from dropdown
   - Click "Refresh" to see candidates and vote counts

## Project Structure

```
src/main/java/com/evoting/swingclient/
├── EVotingClient.java          # Main application entry point
├── api/                         # API service classes
│   ├── ApiService.java         # Base HTTP client
│   ├── AuthService.java        # Authentication API
│   ├── ElectionService.java    # Election management API
│   └── VotingService.java      # Voting operations API
├── model/                       # Data model classes
│   ├── User.java
│   ├── Election.java
│   ├── Candidate.java
│   └── Vote.java
└── ui/                          # Swing UI components
    ├── LoginPanel.java         # Login screen
    ├── RegisterPanel.java      # Registration screen
    ├── DashboardPanel.java     # Voter dashboard
    ├── VoteDialog.java         # Vote casting dialog
    ├── ReceiptDialog.java      # Vote receipt display
    └── AdminPanel.java         # Admin management panel
```

## API Integration

The Swing client communicates with the backend through REST API endpoints:

- **Authentication**: `/api/auth/login`, `/api/auth/register`
- **Elections**: `/api/elections/active`, `/api/elections/{id}/candidates`
- **Voting**: `/api/voting/vote`, `/api/voting/user/{userId}/election/{electionId}/status`
- **Admin**: `/api/admin/elections`, `/api/admin/elections/{id}/activate`

All authenticated requests include JWT token in the `Authorization: Bearer {token}` header.

## Troubleshooting

### Connection Errors

If you see connection errors:
1. Ensure the backend is running on `http://localhost:8080`
2. Check that the database is accessible
3. Verify the API context path is `/api`

### Authentication Errors

If login fails:
1. Check username and password are correct
2. Ensure user is registered in the database
3. Verify JWT secret configuration matches backend

### Compilation Errors

If you encounter compilation errors:
1. Run `mvn clean install` to rebuild all dependencies
2. Ensure Java 17+ is being used: `java -version`
3. Check that Gson dependency is properly installed

## Notes

- The Swing client uses Gson for JSON parsing (already included in pom.xml)
- All API calls are executed in background threads using SwingWorker to prevent UI freezing
- JWT tokens are stored in memory and sent with each authenticated request
- The application automatically navigates to Admin Panel for ADMIN role users

## Future Enhancements

- Save vote receipts to file
- Export election results
- Vote verification using receipt signature
- Bulletin board viewer
- Improved error handling and user feedback
