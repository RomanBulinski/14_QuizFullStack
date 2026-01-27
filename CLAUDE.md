# Claude Code Development Guide

This document provides context for Claude Code when working with the FullStack Quiz application.

## Quick Reference

### Build Commands


```bash
# Full build (backend + frontend)
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run application
mvn spring-boot:run

# Or run JAR directly
java -jar target/fullstack-quiz-0.0.1-SNAPSHOT.jar
```

### Test Commands

```bash
# Backend unit tests
mvn test

# Specific test class
mvn test -Dtest=QuizServiceTest

# E2E tests (app must be running)
npm run cypress:open   # Interactive mode
npm run cypress:run    # Headless mode
```

### Frontend Development

```bash
cd angular-src

# Install dependencies
npm install

# Development server
ng serve

# Build production
npm run build
```

## Architecture Overview

### Monolithic Fullstack Design

- **Single JAR Deployment**: Backend and frontend packaged together
- **Static Resource Serving**: Spring Boot serves compiled Angular files from `classpath:/static/`
- **SPA Routing**: Uses HashLocationStrategy (`#/route`) for compatibility with static serving
- **CSV-Based Data**: Questions stored in CSV files, loaded at startup into memory cache

### Build Pipeline

1. **Maven Lifecycle**: Parent build process
2. **Frontend Maven Plugin**: Executes during `generate-resources` phase
   - Installs Node.js and npm locally
   - Runs `npm install` in angular-src/
   - Runs `npm run build` (Angular production build)
   - Outputs to `src/main/resources/static/`
3. **Spring Boot Plugin**: Packages everything into executable JAR

### Key Design Decisions

#### Backend
- **OpenCSV**: Parses semicolon-delimited CSV files
- **@PostConstruct**: Loads and caches questions on application startup
- **Collections.shuffle()**: Randomizes question order
- **No Database**: MVP uses CSV for simplicity

#### Frontend
- **Angular Material**: Pre-built UI components
- **Router State**: Passes data between components (selection → quiz → summary)
- **HashLocationStrategy**: Required for SPA routing without server-side rewrites
- **No Guards**: Public access, no authentication required

## File Organization

### Backend Structure

```
src/main/java/com/fullstackquiz/
├── controller/
│   └── QuizController.java        # REST API endpoints
├── service/
│   └── QuizService.java           # Business logic, CSV loading
├── model/
│   └── Question.java              # Data model with Lombok
├── config/
│   └── WebConfig.java             # SPA fallback routing
└── FullstackQuizApplication.java  # Main class
```

### Frontend Structure

```
angular-src/src/app/
├── selection/                     # Choose tech & count
├── quiz/                          # Question display & answering
├── summary/                       # Results & review
└── shared/
    ├── models/
    │   └── question.model.ts      # TypeScript interface
    ├── services/
    │   └── quiz-api.service.ts    # HTTP client
    └── components/
        └── progress-bar/          # Reusable progress indicator
```

## Coding Standards

**IMPORTANT**: When writing or modifying code, always follow software engineering best practices. Detailed coding rules are documented in:
- **Java/Spring Boot**: `01_documents/claude/java-coding-rules.md`
- **Angular/TypeScript**: `01_documents/claude/angular-typescript-rules.md`
- **GUI/UX**: `01_documents/claude/gui_rules.md`

### Core Principles

Apply these principles to all code:

#### SOLID Principles
- **S**ingle Responsibility: One class/component, one reason to change
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subtypes must be substitutable for base types
- **I**nterface Segregation: Many specific interfaces > one general interface
- **D**ependency Inversion: Depend on abstractions, not concretions

#### Other Essential Principles
- **DRY** (Don't Repeat Yourself): Extract common logic, avoid duplication
- **KISS** (Keep It Simple, Stupid): Simplicity over complexity
- **YAGNI** (You Aren't Gonna Need It): Only implement what's needed now
- **Separation of Concerns**: Controllers → Services → Data Access (backend), Components → Services → API (frontend)
- **Fail Fast**: Validate inputs early, report errors immediately
- **Composition Over Inheritance**: Prefer object composition for code reuse
- **Immutability** (TypeScript): Use immutable state updates with spread operator

### Quick Guidelines

#### Java/Spring Boot
- Use **constructor injection**, not field injection
- Keep **controllers thin** - delegate to services
- Use **Lombok** to reduce boilerplate (`@Data`, `@AllArgsConstructor`, etc.)
- Configure **CSVParser with semicolon delimiter** (`;`) for CSV files
- **Fail fast** with early validation (`Objects.requireNonNull`, parameter checks)
- Follow **Google Java Style Guide** (enforced by Checkstyle)
- Write **Javadoc** on all public methods
- Use **SLF4J** for logging with appropriate levels

#### Angular/TypeScript
- **Type everything** - avoid `any`, use explicit types
- **Unsubscribe** from observables using `takeUntil` pattern or `async` pipe
- Use **OnPush** change detection when possible
- **Separate concerns**: Components (presentation) → Services (state/API)
- Use **immutable state updates** (spread operator, no mutations)
- Implement **pure functions** without side effects
- Use **RxJS operators** appropriately (`map`, `filter`, `switchMap`, `catchError`)
- Follow **Angular Style Guide** naming conventions
- Use **Angular Material** components consistently

### Before Writing Code

1. **Read existing code** first - understand patterns before modifying
2. **Check detailed rules** in the coding rules files when uncertain
3. **Validate against principles** - does this follow SOLID/DRY/KISS?
4. **Keep it simple** - avoid over-engineering

## Key Conventions

### CSV Format

```
question;option1;option2;option3;option4;correctIndex
```

- **Delimiter**: Semicolon (`;`)
- **Correct Index**: 0-based (0, 1, 2, or 3)
- **No Headers**: First row is data, not headers
- **Encoding**: UTF-8

### API Conventions

- **Base Path**: `/api`
- **Technology Names**: Case-insensitive (`Spring`, `spring`, `SPRING` all work)
- **Valid Counts**: Only 10, 20, 30 accepted
- **Response**: JSON array of Question objects
- **Errors**:
  - 400 Bad Request: Invalid technology or count
  - 404 Not Found: No questions available

### Angular Routing

- **Hash Strategy**: All routes prefixed with `#` (e.g., `http://localhost:8080#/selection`)
- **State Passing**: Uses `Router.navigate()` with `state` object
- **Fallback**: WebConfig redirects unknown routes to `index.html`

## Development Workflow

### Making Backend Changes

1. Modify Java files in `src/main/java/`
2. Run tests: `mvn test`
3. Run application: `mvn spring-boot:run`
4. Test API: `curl http://localhost:8080/api/questions/Spring/10`

### Making Frontend Changes

#### Option 1: Integrated Build (Slower)
```bash
mvn clean install
java -jar target/fullstack-quiz-0.0.1-SNAPSHOT.jar
```

#### Option 2: Separate Dev Server (Faster, requires proxy)
```bash
# Terminal 1: Backend
mvn spring-boot:run

# Terminal 2: Frontend
cd angular-src
ng serve --proxy-config proxy.conf.json
```

**proxy.conf.json**:
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

### Adding New Questions

1. Edit CSV file in `src/main/resources/data/`
2. Follow format: `question;opt1;opt2;opt3;opt4;correctIndex`
3. Restart application (questions loaded at startup)

### Checkstyle

- Uses Google Java Style Guide
- Configured in `pom.xml`
- Runs during `validate` phase
- Set to `failsOnError=false` (warnings only)

## Common Tasks

### Change Server Port

Edit `src/main/resources/application.yml`:
```yaml
server:
  port: 9090
```

### Add New Technology

1. Create CSV: `src/main/resources/data/newtech-questions.csv`
2. Update QuizService.init(): Add `loadQuestionsFromCsv("newtech")`
3. Update QuizController.isValidTechnology(): Add validation
4. Update Angular selection component: Add new tile

### Migrate to Database

1. Add dependencies to `pom.xml`:
   - `spring-boot-starter-data-jpa`
   - `postgresql` or other DB driver
2. Create JPA entity from Question model
3. Create QuestionRepository interface
4. Update QuizService to use repository
5. Add `application.yml` datasource configuration
6. See `example_pom.xml` in `01_documents/` for reference

## Testing Strategy

### Backend Unit Tests

- **Service Tests**: Test CSV loading, randomization, edge cases
- **Controller Tests**: Use `@WebMvcTest` and MockMvc
- **Mocking**: Mock QuizService in controller tests
- **Coverage**: Aim for critical paths, not 100% coverage

### E2E Tests

- **Cypress**: Tests full user flow
- **Running App Required**: Start app before running Cypress
- **Selectors**: Use text content (`cy.contains()`) over brittle CSS selectors
- **Waits**: Use `cy.wait()` for transitions, avoid flaky tests

## Troubleshooting

### Issue: Frontend not updating after Angular changes
**Solution**:
```bash
cd angular-src
rm -rf node_modules dist
npm install
npm run build
cd ..
mvn clean package
```

### Issue: 404 on refresh in production
**Cause**: SPA routing without proper fallback
**Solution**: WebConfig should handle this, verify PathResourceResolver returns index.html

### Issue: CORS errors during development
**Solution**: @CrossOrigin on controller (already present), or use ng serve proxy

### Issue: Questions not loading
**Check**:
1. CSV files present in `src/main/resources/data/`
2. CSV format correct (semicolons, 6 columns)
3. Application logs show "Loaded X questions for Y"
4. API endpoint accessible: `curl localhost:8080/api/questions/Spring/10`

## Production Considerations

### Performance
- Questions cached in memory (no disk I/O per request)
- Angular production build minified and optimized
- Consider CDN for static assets if scaling

### Security
- No authentication (per requirements)
- Consider rate limiting for API
- CORS configured for all origins (restrict in production)

### Scalability
- Stateless design (can run multiple instances)
- Shared question cache (could use Redis for distributed caching)
- Database migration recommended for user tracking features

## Migration Paths

### To PostgreSQL

See `example_pom.xml` in `01_documents/` for database-enabled configuration.

Key changes:
1. Add JPA and PostgreSQL dependencies
2. Convert Question to `@Entity`
3. Create QuestionRepository
4. Load CSV data into database on first run
5. Update application.yml with datasource config

### To Microservices

If scaling is needed:
1. Split into quiz-service and question-service
2. Use Spring Cloud for service discovery
3. Add API Gateway (Spring Cloud Gateway)
4. Externalize configuration (Spring Cloud Config)

## Useful Maven Commands

```bash
# Skip frontend build (for backend-only changes)
mvn clean package -Dskip.npm

# Skip tests and frontend
mvn clean package -DskipTests -Dskip.npm

# Run specific test
mvn test -Dtest=QuizControllerTest#testGetQuestionsSpring10

# Generate dependency tree
mvn dependency:tree

# Clean everything including frontend
mvn clean
cd angular-src && rm -rf node_modules dist
```

## Code Style

### Backend (Java)
- Google Java Style Guide
- Lombok for getters/setters/constructors
- Constructor injection for dependencies
- Javadoc on public methods
- SLF4J for logging

### Frontend (TypeScript/Angular)
- Angular style guide
- PascalCase for components
- camelCase for methods/properties
- Type everything (avoid `any`)
- RxJS for async operations
- SCSS for styling (BEM-like structure)

## Known Limitations

1. **No Persistence**: Questions not saved, quiz state lost on refresh
2. **No User Accounts**: Anyone can take any quiz
3. **No Time Limits**: Quiz can be paused indefinitely
4. **Limited Validation**: Assumes well-formed CSV input
5. **Memory Cache**: Questions loaded into heap (not suitable for huge datasets)

## Future Enhancement Ideas

- User authentication (Spring Security + JWT)
- PostgreSQL for persistence
- Quiz history and analytics
- Difficulty levels
- Timed quizzes with countdown
- Admin panel for question management
- Export results as PDF
- Social sharing features
