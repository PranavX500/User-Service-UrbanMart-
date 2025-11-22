# 👤 User Service (Ecommerce Microservices)

The **User Service** handles all authentication, user onboarding, login session management, and secured profile access for the Ecommerce Microservices system.

This service uses **JWT Authentication**, **HTTP-only cookies**, **Spring Security**, **MySQL**, and **Kafka** for event-driven communication.

---

# 🚀 Tech Stack
- **Spring Boot**
- **Spring Security**
- **JWT Authentication**
- **HTTP-Only Cookies**
- **MySQL**
- **Kafka (for communication events)**
- **Spring Cloud Microservices**
- **Eureka Service Discovery**
- **API Gateway Integration**
- **Maven**
- **Lombok**

---

# 📌 Features Implemented

### ✔️ **User Signup**
- Registers new users  
- Stores details in MySQL  
- Sends Kafka events (if configured)

### ✔️ **User Login**
- Validates credentials  
- Generates **JWT token**  
- Stores JWT in secure **HTTP-only cookies**

### ✔️ **User Logout**
- Deletes HTTP-only cookies  
- Invalidates session

### ✔️ **User Profile (Secured Endpoint)**
- Only accessible with valid JWT  
- Returns the currently authenticated user from Spring Security context

### ✔️ **Role-based Authentication Ready**
- Can extend for Admin/User roles easily

---

# 🎯 Upcoming Enhancements (Future Scope)
These points will be shown in README as “To be implemented”:

### ⏳ **OAuth 2.0 Login (Google, GitHub, etc.)**
Implementing OAuth 2.0 for social login support.

### ⏳ **Refresh Token Mechanism**
Issue long-living refresh tokens to improve security.

### ⏳ **Kafka User Activity Events**
Send events like:
- USER_SIGNUP  
- USER_LOGIN  
- USER_LOGOUT  

### ⏳ **User Permissions / RBAC (Role Based Access Control)**



