1. Product Requirements Document (PRD) – Spring Boot + Angular Static MVP

Project Goal:
Instant knowledge test for Spring/Angular without registration. Single JAR deployment.

Application Structure (Client-side routes):
1. Selection Page (/#/selection): Tech tiles, questions (10/20/30).
2. Quiz Page (/#/quiz): 1 question, 4 options, progress, Next.
3. Summary Page (/#/summary): % score, points, suggestions.

Technical Requirements:
• Frontend: Angular static served by Spring.
• Backend: Spring Boot REST /api/questions/{tech}/{count}.
• Data: CSV (spring-questions.csv, angular-questions.csv).
• No Auth.
• Build: Single JAR (frontend-maven-plugin).
• Testing: Cypress E2E, JUnit.

See: Project-Structure.md for full folders/build process.
