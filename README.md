# Shop Management Application

## Overview

A RESTful Spring Boot back-end for an online shop, supporting product, user, and order management with JWT-based authentication, role-based authorization, and social login via Google and GitHub. The app is containerized with Docker and deployed on AWS EC2.

## Features

- **Product Management**: CRUD operations for products and images.
- **User Management**:
  - Register, login, logout with JWT.
  - Social login and account linking with Google and GitHub.
  - Role-based access (Admin/User).
- **Order Management**: Create orders, manage cart, and process checkout.
- **Security**: JWT authentication, token refresh, and centralized exception handling.
- **Validation**: Custom input validators (e.g., email, price).
- **Testing**: Unit tests with JUnit and Mockito.
- **Deployment**: Dockerized, hosted on Docker Hub, deployed on AWS EC2.

## Technologies

- **Back-end**: Java, Spring Boot, Hibernate, QueryDSL
- **Database**: MySQL
- **Security**: JWT, OAuth2 (Google, GitHub)
- **Testing**: JUnit, Mockito
- **DevOps**: Docker, Docker Hub, AWS EC2
- **Tools**: Maven, Git, Postman

## Prerequisites

- Java 17+
- MySQL 8.0+
- Docker (optional)
- Maven
- Postman
- Google and GitHub OAuth2 credentials (for social login)

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/hieund18/ShopApp.git
   cd ShopApp
   ```

2. **Set up the database**:
   - Run the provided schema: `database.sql`.
   - Update `src/main/resources/application.yaml`:
   ```yaml
   spring:
      datasource:
         url: jdbc:mysql://localhost:3306/shopapp
         username: your_username
         password: your_password
   outbound:
      identity:
         google:
            client-id: your-google-client-id
            client-secret: your-google-client-secret
         github:
            client-id: your-github-client-id
            client-secret: your-github-client-secret
     ```

3. **Build and run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Run with Docker** (optional):
   ```bash
   docker build -t shop-app .
   docker run -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://[mysql-host]:3306/shop_app -e  shop-app
   ```

Access at `http://localhost:8080`.

## Deployment on AWS

1. Push to Docker Hub:
   ```bash
   docker tag shop-app [your-dockerhub-username]/shop-app
   docker push [your-dockerhub-username]/shop-app
   ```

2. On AWS EC2 (Ubuntu):
   - Pull image: `docker pull [your-dockerhub-username]/shop-app`.
   - Run container with database environment variables.
   - Allow port 8080 in Security Groups.

## Testing

Run unit tests:
```bash
mvn test
```

## Contact

Nguyen Duy Hieu - hieund.forwork@gmail.com