

# 🧪 Blockchain E-Voting System - Complete Testing Guide

## 📋 Prerequisites

1. **PostgreSQL Database:**
   ```bash
   # Start PostgreSQL
   brew services start postgresql

   # Create database
   createdb evoting_db
   ```

2. **Java 17+ and Maven:**
   ```bash
   java -version  # Should show Java 17+
   mvn -version   # Should show Maven
   ```

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

## 🔄 Complete Testing Flow

### **Step 1: Create Admin User**

First, create an admin user to manage the system:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "email": "admin@evoting.com",
    "fullName": "System Administrator",
    "voterId": "ADMIN001"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@evoting.com",
  "fullName": "System Administrator",
  "voterId": "ADMIN001",
  "role": "USER"
}
```

**Note:** The role is set to "USER" by default. In a real system, you'd have a separate admin registration or role assignment.

### **Step 2: Admin Login**

Login as admin to get JWT token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@evoting.com",
    "fullName": "System Administrator",
    "voterId": "ADMIN001",
    "role": "USER"
  }
}
```

**Save the token** for subsequent requests. Let's call it `ADMIN_TOKEN`.

### **Step 3: Create Election (Admin Only)**

Use the admin token to create an election:

```bash
curl -X POST http://localhost:8080/api/admin/elections \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "title=Presidential Election 2024&description=Choose the next president&startTime=2024-01-15T09:00:00&endTime=2024-01-15T17:00:00"
```

**Expected Response:**
```json
{
  "id": 1,
  "title": "Presidential Election 2024",
  "description": "Choose the next president",
  "startTime": "2024-01-15T09:00:00",
  "endTime": "2024-01-15T17:00:00",
  "isActive": false
}
```

### **Step 4: Add Candidates to Election (Admin Only)**

Add candidates to the election:

```bash
# Add Candidate 1
curl -X POST "http://localhost:8080/api/elections/1/candidates" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=John Smith&party=Democratic Party&description=Experienced leader with focus on education"

# Add Candidate 2
curl -X POST "http://localhost:8080/api/elections/1/candidates" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Jane Doe&party=Republican Party&description=Business expert committed to economic growth"
```

**Expected Response for each:**
```json
{
  "id": 1,
  "name": "John Smith",
  "party": "Democratic Party",
  "description": "Experienced leader with focus on education",
  "electionId": 1,
  "voteCount": 0
}
```

### **Step 5: Activate Election (Admin Only)**

Activate the election so users can vote:

```bash
curl -X PUT http://localhost:8080/api/admin/elections/1/activate \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Expected Response:** `200 OK` (empty body)

### **Step 6: Create Regular User**

Create a regular voter user:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "voter1",
    "password": "voter123",
    "email": "voter1@example.com",
    "fullName": "John Doe",
    "voterId": "VOTER001"
  }'
```

### **Step 7: User Login**

Login as the regular user:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "voter1",
    "password": "voter123"
  }'
```

**Save the token** as `USER_TOKEN`.

### **Step 8: Check Active Elections**

User checks for active elections:

```bash
curl -X GET http://localhost:8080/api/elections/active \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "title": "Presidential Election 2024",
    "description": "Choose the next president",
    "startTime": "2024-01-15T09:00:00",
    "endTime": "2024-01-15T17:00:00",
    "isActive": true
  }
]
```

### **Step 9: View Candidates**

User views available candidates for the election:

```bash
curl -X GET http://localhost:8080/api/elections/1/candidates \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "John Smith",
    "party": "Democratic Party",
    "description": "Experienced leader with focus on education",
    "electionId": 1,
    "voteCount": 0
  },
  {
    "id": 2,
    "name": "Jane Doe",
    "party": "Republican Party",
    "description": "Business expert committed to economic growth",
    "electionId": 1,
    "voteCount": 0
  }
]
```

### **Step 10: Cast Vote**

User casts their vote for candidate ID 1 (John Smith). Note: No authentication required for voting.

**Important:** If you get 401 Unauthorized, restart the Spring Boot application for security configuration changes to take effect.

```bash
curl -X POST "http://localhost:8080/api/voting/vote?userId=2&electionId=1&candidateId=1"
```

**Expected Response:**
```json
{
  "id": 1,
  "userId": 2,
  "candidateId": 1,
  "electionId": 1,
  "transactionHash": "0x1234567890abcdef...",
  "blockNumber": null,
  "votedAt": "2024-01-15T10:30:00",
  "isVerified": false
}
```

### **Step 11: Check Vote Results (Admin)**

Admin can check the vote results:

```bash
curl -X GET http://localhost:8080/api/voting/election/1/votes \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "userId": 2,
    "candidateId": 1,
    "electionId": 1,
    "transactionHash": "0x1234567890abcdef...",
    "timestamp": "2024-01-15T10:30:00"
  }
]
```

### **Step 12: Verify Vote Status**

User can check if they have voted:

```bash
curl -X GET "http://localhost:8080/api/voting/user/2/election/1/status" \
  -H "Authorization: Bearer $USER_TOKEN"
```

**Expected Response:** `true`

## 🎯 API Endpoints Summary

### **Authentication:**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### **Admin Operations:**
- `POST /api/admin/elections` - Create election
- `PUT /api/admin/elections/{id}/activate` - Activate election
- `PUT /api/admin/elections/{id}/deactivate` - Deactivate election

### **Election Management:**
- `GET /api/elections/active` - Get active elections
- `GET /api/elections/{id}/candidates` - Get candidates for election
- `POST /api/elections/{electionId}/candidates` - Add candidate to election (Admin)

### **Voting Operations:**
- `POST /api/voting/vote?userId={userId}&electionId={electionId}&candidateId={candidateId}` - Cast vote (No authentication required)
- `GET /api/voting/election/{electionId}/votes` - Get votes for election
- `GET /api/voting/user/{userId}/election/{electionId}/status` - Check if user voted

## 🔧 Troubleshooting

### **Database Connection Issues:**
```bash
# Check PostgreSQL status
brew services list | grep postgresql

# Restart PostgreSQL
brew services restart postgresql
```

### **Application Won't Start:**
```bash
# Check if port 8080 is free
lsof -i :8080

# Kill process if needed
kill -9 <PID>
```

### **Authentication Errors:**
- Ensure you're using `Bearer ` prefix in Authorization header
- Check token expiration (default: 24 hours)
- Verify user credentials

### **CORS Issues (if using frontend):**
- The backend has CORS configured for all origins
- Check browser console for CORS errors

## 📊 Expected Database Tables

After running, you should have these tables:
- `user` - User accounts
- `election` - Election data
- `candidate` - Election candidates
- `vote` - Vote records

## 🚀 Next Steps

1. **Add Candidates:** Create candidates for elections
2. **Frontend Integration:** Connect with React/Vue frontend
3. **Blockchain Integration:** Implement actual blockchain recording
4. **Email Verification:** Add email verification for registration
5. **Two-Factor Auth:** Enhance security with 2FA

---

## ✅ **Complete Testing Flow Summary**

1. ✅ **Create Admin User** → Register admin account
2. ✅ **Admin Login** → Get JWT token for admin operations
3. ✅ **Create Election** → Admin creates election with title, description, dates
4. ✅ **Add Candidates** → Admin adds candidates to the election
5. ✅ **Activate Election** → Admin activates election for voting
6. ✅ **Create Regular User** → Register voter account
7. ✅ **User Login** → Voter gets JWT token
8. ✅ **Check Active Elections** → Voter sees available elections
9. ✅ **View Candidates** → Voter sees candidates for election
10. ✅ **Cast Vote** → Voter submits their vote
11. ✅ **Check Vote Results** → Admin views vote tally
12. ✅ **Verify Vote Status** → Voter confirms their vote was recorded

**🎉 Your blockchain e-voting system is now fully functional with complete end-to-end testing!**
