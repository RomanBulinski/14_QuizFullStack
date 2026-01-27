# Project Architecture & Folder Structure

## Single Maven Project Layout
├── pom.xml
├── angular-src/              # Angular source (ng build → static/)
│   ├── src/app/selection/...
│   └── package.json
├── src/main/java/...         # Backend controllers/services
├── src/main/resources/
│   ├── static/               # Angular BUILD OUTPUT
│   └── data/*.csv
├── cypress/e2e/...           # E2E tests
└── docker/Dockerfile

## Build Steps
mvn install → ng build → JAR

## Detailed Folders
Project Structure (Single Maven project):
├── pom.xml (spring-boot-starter-web, frontend-maven-plugin, opencsv)
├── angular-src/                          # Pełne Angular source code
│   ├── angular.json (build config: --base-href './')
│   ├── package.json (ng build --prod)
│   ├── src/
│   │   ├── app/
│   │   │   ├── selection/selection.component.ts|html|scss
│   │   │   ├── quiz/quiz.component.ts|html|scss + quiz-api.service.ts
│   │   │   ├── summary/summary.component.ts|html|scss
│   │   │   ├── shared/progress-bar.component.ts + question.model.ts
│   │   │   └── app-routing.module.ts (HashLocationStrategy for static)
│   │   ├── index.html (root dla Spring static)
│   │   └── main.ts
│   └── tsconfig.json
├── src/
│   ├── main/
│   │   ├── java/com/fullstackquiz/
│   │   │   ├── FullstackQuizApplication.java (@SpringBootApplication)
│   │   │   ├── controller/QuizController.java (REST /api/questions/{tech}/{count})
│   │   │   ├── service/QuizService.java (CSV loader, randomize N questions)
│   │   │   ├── model/Question.java (String text, String[] options, int correct)
│   │   │   └── config/WebConfig.java (@EnableWebMvc, static resource handler)
│   │   └── resources/
│   │       ├── static/                    # NG BUILD OUTPUT (automat)
│   │       │   ├── index.html
│   │       │   ├── main-[hash].js
│   │       │   ├── styles-[hash].css
│   │       │   └── assets/ (icons itp.)
│   │       ├── application.yml (server.port=8080)
│   │       └── data/
│   │           ├── spring-questions.csv (question;opt1;opt2;opt3;opt4;correctIdx)
│   │           └── angular-questions.csv
│   └── test/java/com/fullstackquiz/QuizControllerTest.java (MockMvc)
├── cypress/                              # E2E tests (na localhost:8080)
│   ├── e2e/quiz-spring.cy.ts
│   └── cypress.config.ts (baseUrl: 'http://localhost:8080')
└── docker/Dockerfile (FROM openjdk:21-jdk, COPY target/*.jar)

Build & Run Process:
1. mvn clean install → frontend-maven-plugin: npm install → ng build → mvn package
2. java -jar target/fullstack-quiz-0.0.1-SNAPSHOT.jar
3. App działa: http://localhost:8001 (Angular) + /api/... (backend)

Technical Requirements:
• Frontend: Angular 18+ source w angular-src/, build do resources/static/ (Hash routing).
• Backend: Spring Boot 3.x serves static/** automatically.
• API: GET /api/questions/{tech}/{count} → List<Question> JSON.
• Data: CSV loaded at startup (OpenCSV).
• No Auth: No Spring Security.
• Dev workflow: ng serve:4200 (proxy /api → :8080) + Spring run.
• Testing: Cypress E2E (full flows), JUnit backend unit/integration.

Non-Functional:
• Deployment: Single JAR/Docker.
• Performance: <2s quiz load.
• Responsive: Mobile-first Angular Material/Tailwind.