# webapp - Health Check API
This project implements a health check API using Spring Boot and Hibernate ORM in Java.

Before you begin, ensure you have the following installed on your system:

1. Java Development Kit (JDK) 17 or later
2. Apache Maven 3.6.3 or later
3. PostgreSQL 13 or later
4. Git (optional, for version control)

## Installation

1. Clone the repository (if using Git):
 (git clone https://github.com/Madhu-Cloud-CSYE6225/webapp.git)
2. If not using Git, download the project files and navigate to the project directory.
3. Configure the database connection in `src/main/resources/application.properties`:
    spring.datasource.url=jdbc:postgresql://localhost:5432/{database_name}
    spring.datasource.username= {your_username}
    spring.datasource.password= {your_password}
4. Build the project:
    mvn clean package

## Running the Application

1. After building the project, you can run the JAR file using the following command:
    java -jar target/{project-name-0.0.1-SNAPSHOT.jar}
2. The application will start, and you should see log output indicating that the server has started.
3. The health check API will be available at: `http://localhost:8080/healthz`