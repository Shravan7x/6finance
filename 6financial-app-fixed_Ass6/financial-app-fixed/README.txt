FINANCIAL APP FIXED - SPRING BOOT MULTI-PROFILE PROJECT

This version fixes the missing DataSource problem by adding:
1. spring-boot-starter-jdbc
2. H2 in-memory database configuration for dev and test
3. default profile = dev

REQUIREMENTS
- Java JDK 17 or later
- Maven

HOW TO RUN
1. Extract this ZIP.
2. Open the folder in VS Code.
3. Open terminal in the project folder.

Run with default dev profile:
mvn spring-boot:run

Run explicitly with dev profile:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

Run with test profile:
mvn spring-boot:run -Dspring-boot.run.profiles=test

Run with production profile:
mvn spring-boot:run -Dspring-boot.run.profiles=prod

URLS
Home:
http://localhost:8080/

Health:
http://localhost:8080/health-check

Config:
http://localhost:8080/config

H2 Console:
http://localhost:8080/h2-console

H2 CONSOLE LOGIN VALUES
JDBC URL: jdbc:h2:mem:devdb
User Name: sa
Password: leave blank

IMPORTANT
- Production config uses sample PostgreSQL values only.
- For real production use, edit application-prod.yml.

CHECK FOLDER
Make sure you are inside the folder that contains:
- pom.xml
- src
- README.txt
