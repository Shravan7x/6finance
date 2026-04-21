Education Platform Registration System - Spring Boot

Requirements:
- Java 17 or higher
- Maven installed

Run steps:
1. Open terminal in the project folder.
2. Run: mvn clean install
3. Run: mvn spring-boot:run

API Endpoint:
POST http://localhost:8080/api/students/register

Sample valid JSON:
{
  "fullName": "Rahul Patil",
  "email": "rahul@gmail.com",
  "password": "Rahul@123",
  "confirmPassword": "Rahul@123",
  "age": 19,
  "course": "B.Tech Computer Engineering"
}

Sample invalid JSON:
{
  "fullName": "",
  "email": "rahulgmail.com",
  "password": "abc",
  "confirmPassword": "xyz",
  "age": 14,
  "course": ""
}
