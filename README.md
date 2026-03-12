# 🔐 RevPassword Manager (Full-Stack Web Application)

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.11-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Frontend](https://img.shields.io/badge/Frontend-HTML5_%7C_CSS3_%7C_JS-e34f26.svg)

**RevPassword Manager** is a robust, full-stack monolithic web application designed to help users securely store, generate, and manage their online credentials. Transitioning from a legacy console-based system to a modern web interface, this application provides an intuitive dashboard, encrypted data portability, and multi-layered security protocols to ensure a zero-knowledge architecture.

The project is built on **Spring Boot 3** using a clean **Layered Monolithic Architecture** with a hybrid rendering approach (Thymeleaf MVC + REST APIs), ensuring high performance, scalability, and secure data handling.

---

## 🚀 Key Features

### 🛡️ Multi-Layered Authentication & Security
* **Two-Factor Authentication (2FA):** Secure login pipeline requiring an email-based OTP verification code.
* **Zero-Knowledge Vault:** Master passwords are never stored in plain text (**BCrypt Hashing**).
* **Secure Reveal:** Users must re-authenticate their Master Password to view or copy sensitive vault credentials.
* **Account Recovery:** Advanced "Forgot Password" flow utilizing 3 hashed security questions.

### 🗄️ Advanced Vault Management (CRUD)
* **Military-Grade Encryption:** All stored passwords are encrypted and decrypted on the fly using **AES Encryption**.
* **Dynamic Organization:** Filter passwords by **Category** (Social Media, Banking, Work, etc.) and mark frequently used accounts as **Favorites**.
* **Real-Time Rendering:** Search, sort (by name, date added, date modified), and filter passwords instantly using a high-speed, client-side JavaScript rendering engine.

### ⚙️ Specialized Security Tools
* **Custom Password Generator:** Create cryptographically strong passwords with highly customizable parameters (length, uppercase, numbers, special characters) with real-time strength evaluation.
* **Security Audit Dashboard:** Visual analytics using **Chart.js** to identify weak, reused, and old passwords at a glance.
* **Data Portability (Disaster Recovery):** Export the entire vault into a single, heavily encrypted `.enc` file, and seamlessly import it back to restore data safely.

---

## 🛠️ Technology Stack

**Frontend (Client-Side Rendering & UI)**
* **Markup & Styling:** HTML5, CSS3, Bootstrap 5.3
* **Templating:** Spring MVC with Thymeleaf
* **Scripting & Data Viz:** Vanilla JavaScript (Fetch API), Chart.js

**Backend (REST API & Business Logic)**
* **Core Framework:** Java 17, Spring Boot 3.5.11
* **Security:** Spring Security 6, `javax.crypto` (AES), BCrypt
* **Communications:** Spring Boot Mail (SMTP Email Delivery)

**Database & Testing**
* **Database:** MySQL 8
* **ORM:** Spring Data JPA / Hibernate
* **Testing:** JUnit 4, Mockito (Layered Unit Testing)

---

## 🏗️ System Architecture

The application is built on a **Hybrid Layered Monolithic Architecture**, utilizing `@RestController`s for dynamic JSON data fetching and standard `@Controller`s for serving UI views.

### 📌 Architecture Diagram

![System Architecture Diagram](docs/images/Architecture%20Diagram.png)

**Layer Responsibilities:**
* **Controller Layer:** Intercepts HTTP requests, serves Thymeleaf templates, and routes JSON data payloads via REST APIs.
* **Service Layer:** Orchestrates the core business logic, handles AES encryption/decryption, and manages security workflows.
* **Repository Layer:** Manages database operations via Spring Data JPA interfaces.
* **DTO Layer:** Securely transfers filtered data between the client and server to prevent exposing sensitive entity fields.
* **Utility Layer:** Centralizes reusable cryptographic operations (AES, BCrypt, Strength Checking).

---

## 💾 Database Schema (ER Diagram)

The system utilizes a highly normalized relational database design with secure cascade rules and strict data typing.

### 🗄️ Entity Relationship Diagram (ERD)

![ER Diagram](docs/images/ER%20Diagram.png)

**Core Entities:**
* **Users:** Stores user profiles, 2FA preferences, and the BCrypt Master Password hash.
* **Vault_Entry:** Stores the AES-encrypted account credentials, linked to specific Categories.
* **Security_Question:** Stores the user's recovery questions and hashed answers.
* **Verification_Code:** Manages temporary, time-sensitive OTP codes for 2FA.
* **Backup_File:** Stores massive `LONGTEXT` encrypted JSON payloads for vault restoration.
* **Category:** Organizes vault entries into predefined system groups.
* **Security_Audit_Report:** Tracks historical vulnerability metrics for the user's vault.

---

## ⚙️ Setup & Installation

### Prerequisites
* **Java 17** or higher installed.
* **MySQL 8** installed and running.
* **Maven** installed (or use the embedded IDE wrapper).

Markdown
### Installation Steps

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/rajnaveen01/RevPasswordManager-JavaFullStack.git
   cd RevPasswordManager-JavaFullStack
   
2. Configure the Database & Email (application.properties):
Open src/main/resources/application.properties and update your credentials:

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/finalrev
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Email Configuration (For 2FA)
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_GOOGLE_APP_PASSWORD

(Note: You must create the finalrev schema in MySQL before running)

3. Build the Project:
   ```bash
   mvn clean install

4. Run the Application:
Run RevPasswordmanagerApplication.java from your IDE, or use the terminal:
   ```bash
   mvn spring-boot:run

5. Access the Web App:
Open your browser and navigate to: http://localhost:9092

📸 Usage Workflow
Register: Create an account with a secure Master Password and set up 3 mandatory security questions.

Login & 2FA: Authenticate with your credentials. If 2FA is enabled in your profile, check your email for the 6-digit OTP code to access the dashboard.

Dashboard Analytics: View your graphical security audit and total password counts.

Manage Vault: Add new credentials, assign them to categories, and test their strength.

Secure Reveal: Attempt to view a saved password—you will be prompted to re-enter your Master Password to decrypt it.

Backup Data: Navigate to the Backup tab to export your entire vault as a secure .enc file. Test the recovery process by importing it!

Developed by Naveenraj | Powered by Spring Boot 3
