# BudgetManager

BudgetManager is a full-stack web application designed to help users manage their personal finances effectively. It allows users to track income and expenses, categorize transactions, and gain insights into their spending habits.

## Features

- **JWT Authentication**: Secure registration and login using JSON Web Tokens.
- **Transaction Management**: Add, edit, and delete income and expense transactions.
- **Categorization**: Organize transactions into customizable categories.
- **Account Management**: Change own account details. 

## Technologies Used

### Backend

- **Java**: Primary programming language for the backend.
- **Spring Boot**: Framework for building the RESTful API.
- **Maven**: Project management and build tool.
- **PostgreSQL**: Relational database used to store user data, transactions, and categories.

### Frontend

- **Angular**: A powerful front-end framework used to build dynamic and responsive user interfaces.
- **TypeScript**: Superset of JavaScript used for frontend development.
- **HTML5 & CSS3**: Markup and styling of the application.

## Getting Started

### Prerequisites

- Java 17 or higher
- Node.js and npm
- Maven

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/kKedaRr3/BudgetManager.git
   cd BudgetManager

2. **Backend Setup**:
   
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   
   In the `src/main/resources/application.properties` file, you need to specify your database credentials:
    
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   ```

   The backend api will be accessible at http://localhost:8080
   
4. **Frontend Setup**:

   ```bash
   cd frontend/budgetmanager
   npm install
   npm start
   ```
   
   The frontend will be accessible at http://localhost:4200.

### Project Structure

   ```text
   BudgetManager/
   ├── .mvn/
   ├── frontend/
   │   └── budgetmanager/        # Frontend application
   ├── src/                      # Backend source code
   ├── pom.xml                   # Maven configuration
   └── endpointsInfo             # API endpoint documentation
   ```

## API Overview

### Authentication

- `POST /api/auth/signup` – Register a new user
- `POST /api/auth/signin` – Log in and receive a JWT token

### Users

- `GET /api/users` – Returns all users
- `GET /api/users/<userId>` – Returns a specific user
- `PUT /api/users/<userId>` – Updates a specific user
- `DELETE /api/users/<userId>` – Deletes a specific user

### Categories

- `GET /api/categories` – Returns all categories of the authenticated user
- `GET /api/categories/<categoryId>` – Returns a specific category of the authenticated user
- `POST /api/categories` – Adds a new category for the authenticated user
- `PUT /api/categories/<categoryId>` – Updates a specific category of the authenticated user
- `DELETE /api/categories/<categoryId>` – Deletes a specific category of the authenticated user

### Transactions

- `GET /transactions/<categoryId>` – Returns all transactions of the authenticated user within the specified category
- `GET /transactions/<categoryId>/<transactionId>` – Returns a specific transaction of the authenticated user within the specified category
- `POST /transactions/<categoryId>` – Adds a transaction to the specified category for the authenticated user
- `PUT /transactions/<categoryId>/<transactionId>` – Updates a specific transaction of the authenticated user within the specified category
- `DELETE /transactions/<categoryId>/<transactionId>` – Deletes a specific transaction of the authenticated user within the specified category
