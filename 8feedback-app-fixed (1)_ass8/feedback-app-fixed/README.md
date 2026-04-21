# Customer Feedback Insights - Spring Boot Project

This Spring Boot application stores and retrieves customer feedback for an online service using a relational database.
It also uses JPQL and custom queries to generate insights such as the most frequent feedback topics.

## Features
- Add customer feedback
- View all feedback
- Search feedback by keyword
- Filter by service name
- Get average rating
- Get most frequent feedback topics
- Find low-rated feedback using custom native query
- H2 console support

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Maven

## Run the project
```bash
mvn spring-boot:run
```

Or build first:
```bash
mvn clean install
java -jar target/feedback-app-0.0.1-SNAPSHOT.jar
```

## API Endpoints
- `GET /api/feedback`
- `GET /api/feedback/{id}`
- `POST /api/feedback`
- `GET /api/feedback/service/{serviceName}`
- `GET /api/feedback/search?keyword=speed`
- `GET /api/feedback/insights/topics`
- `GET /api/feedback/insights/summary`

## Sample POST body
```json
{
  "customerName": "Dnyaneshwar Kadam",
  "customerEmail": "dnyaneshwar@example.com",
  "serviceName": "Online Learning",
  "topic": "Content",
  "comment": "Very useful lessons and great explanation.",
  "rating": 5
}
```

## H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:feedbackdb`
- Username: `sa`
- Password: *(blank)*
