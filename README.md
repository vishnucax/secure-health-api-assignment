# Secure Patient Service Backend

This project is a secure Spring Boot microservice that manages patient data with:

- Keycloak Authentication  
- Role-Based Access Control (RBAC)  
- HTTPS (TLS)  
- AES Encryption for sensitive data  
- Jenkins CI/CD pipeline  
- GitHub integration  

The goal of this project is to demonstrate enterprise-level backend security implementation.

---

## Project Features

| Feature | Description |
|--------|------------|
| Authentication | Managed using Keycloak |
| Authorization | Role-based API access control |
| HTTPS | Secure TLS communication |
| AES Encryption | Protects patient data in database |
| Jenkins CI/CD | Automatic project build pipeline |
| GitHub | Version control integration |

---

## Technology Stack

| Technology | Purpose |
|-----------|--------|
| Spring Boot | Backend framework |
| Keycloak | Authentication & authorization |
| MySQL | Database |
| AES | Data encryption |
| HTTPS (TLS) | Secure communication |
| Jenkins | CI/CD automation |
| GitHub | Source control |

---

## Security Architecture

### 1. Authentication
Handled using Keycloak.  
Users must log in before accessing APIs.

### 2. Authorization (RBAC)

| Role | Permission |
|------|-----------|
| viewer | Can GET patient data |
| editor | Can GET + POST patient data |

---

### 3. Data Encryption

Sensitive fields encrypted before saving:

- name  
- disease  

Stored encrypted in database using AES algorithm.  
Returned decrypted in API responses.

---

## HTTPS Configuration

https://localhost:8443

---

## Keycloak Setup

- Realm: healthcare  
- Client: patient-api  

Users:
- testuser  
- editoruser  

Roles:
- viewer  
- editor  

---

## API Endpoints

Base URL: https://localhost:8443/patients

- GET /patients  
- GET /patients/{id}  
- POST /patients  

---

## AES Encryption Implementation

Implemented using AESUtil.java

Flow:

Request → Encrypt → Save DB  
Response → Decrypt → Return JSON  

---

## Jenkins CI/CD Pipeline

- Pull project from repository  
- Execute Maven build  
- Generate executable JAR  
- Verify build success  

Command:
mvn clean install

---

## Database Security

Stored values are encrypted and returned decrypted.

---

## How To Run Project

1. Start Keycloak: http://localhost:9090  
2. Run Spring Boot: mvn spring-boot:run  
3. Access API: https://localhost:8443/patients  

---

## Author

**Vishnu K**  
Portfolio: https://vishnucax.github.io  
