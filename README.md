# FullStack Quiz Application

A comprehensive quiz application built with Spring Boot and Angular, packaged as a single JAR for easy deployment. Test your knowledge of Spring Framework and Angular with randomized questions.

## Features

- **Two Quiz Categories**: Spring Framework and Angular
- **Flexible Question Counts**: Choose 10, 20, or 30 questions
- **Randomized Questions**: Questions are shuffled for each quiz attempt
- **Progress Tracking**: Visual progress bar during quiz
- **Detailed Results**: Review your answers with correct solutions
- **Responsive Design**: Mobile-friendly interface with Angular Material
- **Single JAR Deployment**: Backend API and frontend served from one artifact

## Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.2.x**
- **Maven 3.8+**
- **OpenCSV** for question data parsing
- **Lombok** for reducing boilerplate code

### Frontend
- **Angular 18**
- **TypeScript**
- **Angular Material** for UI components
- **SCSS** for styling
- **RxJS** for reactive programming

### Testing
- **JUnit 5** and **MockMvc** for backend unit tests
- **Cypress** for end-to-end testing

### DevOps
- **Docker** for containerization
- **Maven Frontend Plugin** for integrated builds

## Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **Node.js 18+** and **npm 10+** (for development)
- **Docker** (optional, for containerized deployment)

## Quick Start

### Build the Application

```bash
mvn clean install
```

This command will:
1. Download and install Node.js and npm (via frontend-maven-plugin)
2. Install Angular dependencies
3. Build the Angular frontend
4. Copy frontend assets to `src/main/resources/static/`
5. Build the Spring Boot application
6. Run backend unit tests
7. Package everything into a single JAR

### Run the Application

```bash
java -jar target/fullstack-quiz-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

## Development Workflow

### Backend Development

Run Spring Boot in development mode:

```bash
mvn spring-boot:run
```

The backend API will be available at `http://localhost:8080/api`

### Frontend Development

For faster frontend development with live reload:

```bash
cd angular-src
npm install
ng serve
```

The Angular dev server will run on `http://localhost:4200`

**Note**: Create a proxy configuration in `angular-src/proxy.conf.json` to forward API requests to the backend:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

Then run: `ng serve --proxy-config proxy.conf.json`

### Running Tests

#### Backend Unit Tests

```bash
mvn test
```

#### End-to-End Tests with Cypress

First, ensure the application is running:

```bash
java -jar target/fullstack-quiz-0.0.1-SNAPSHOT.jar
```

Then in another terminal:

```bash
# Install Cypress dependencies
npm install

# Run Cypress in interactive mode
npm run cypress:open

# Run Cypress in headless mode
npm run cypress:run
```

## Docker Deployment

### Build Docker Image

```bash
docker build -f docker/Dockerfile -t fullstack-quiz:latest .
```

### Run Docker Container

```bash
docker run -d -p 8080:8080 --name fullstack-quiz fullstack-quiz:latest
```

### Access the Application

```
http://localhost:8080
```

### Stop and Remove Container

```bash
docker stop fullstack-quiz
docker rm fullstack-quiz
```

## API Endpoints

### Get Questions

```
GET /api/questions/{technology}/{count}
```

**Parameters**:
- `technology`: `Spring` or `Angular`
- `count`: `10`, `20`, or `30`

**Example**:
```bash
curl http://localhost:8080/api/questions/Spring/10
```

**Response**:
```json
[
  {
    "question": "What is the primary purpose of Dependency Injection in Spring?",
    "options": [
      "To reduce coupling between components",
      "To improve application performance",
      "To handle HTTP requests",
      "To manage database connections"
    ],
    "correctIndex": 0
  }
]
```

## Project Structure

```
fullstack-quiz/
├── src/
│   ├── main/
│   │   ├── java/com/fullstackquiz/
│   │   │   ├── controller/        # REST controllers
│   │   │   ├── service/           # Business logic
│   │   │   ├── model/             # Data models
│   │   │   ├── config/            # Configuration classes
│   │   │   └── FullstackQuizApplication.java
│   │   └── resources/
│   │       ├── data/              # CSV question files
│   │       ├── static/            # Angular build output
│   │       └── application.yml
│   └── test/                      # Backend unit tests
├── angular-src/                   # Angular application source
│   ├── src/
│   │   ├── app/
│   │   │   ├── selection/         # Technology & count selection
│   │   │   ├── quiz/              # Quiz interface
│   │   │   ├── summary/           # Results page
│   │   │   └── shared/            # Models, services, components
│   │   └── index.html
│   ├── angular.json
│   └── package.json
├── cypress/                       # E2E tests
│   ├── e2e/
│   └── support/
├── docker/
│   └── Dockerfile
├── pom.xml
└── README.md
```

## Adding New Questions

Questions are stored in CSV files in `src/main/resources/data/`:

- `spring-questions.csv`: Spring Framework questions
- `angular-questions.csv`: Angular questions

**CSV Format**:
```
question;option1;option2;option3;option4;correctIndex
```

**Example**:
```
What is IoC?;Inversion of Control;Input Output Control;Integrated Object Control;Interface Oriented Configuration;0
```

**Important**: Use semicolon (`;`) as the delimiter and ensure `correctIndex` is 0-based (0, 1, 2, or 3).

## Configuration

### Change Server Port

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 9090
```

### Adjust Logging

Edit `src/main/resources/application.yml`:

```yaml
logging:
  level:
    com.fullstackquiz: DEBUG
```

## Troubleshooting

### Build Fails with "Node/npm not found"

The frontend-maven-plugin will automatically download Node and npm during build. If this fails:

1. Check internet connectivity
2. Clear Maven cache: `rm -rf ~/.m2/repository`
3. Retry: `mvn clean install`

### Angular Build Fails

Manually build the Angular app:

```bash
cd angular-src
npm install
npm run build
```

### Port 8080 Already in Use

Either:
1. Stop the process using port 8080
2. Change the port in `application.yml`

### Tests Fail

Ensure CSV files are present and correctly formatted:
```bash
ls -la src/main/resources/data/
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add new feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
- Create an issue in the repository
- Check existing documentation in `01_documents/`

## Roadmap

Future enhancements:
- [ ] User authentication and progress tracking
- [ ] Database persistence (PostgreSQL)
- [ ] Question difficulty levels
- [ ] Timed quizzes
- [ ] Leaderboards
- [ ] Additional technology categories (React, Vue, etc.)
- [ ] Question submission by users
- [ ] Multi-language support

## Rosbberry Pai

W katalogu z pom.xml

docker buildx create --use --name pi-builder
docker buildx inspect --bootstrap
docker buildx build --platform linux/arm/v7 -f docker/Dockerfile -t fullstack-quiz-armv7:latest --load .
docker save -o quiz-armv7.tar fullstack-quiz-armv7:latest

Prześlij TAR: scp quiz-armv7.tar pi@IP_Pi:~/ (PuTTY/WinSCP).

Na Pi (tylko TAR + minimalny compose)
text
docker load -i ~/quiz-armv7.tar
Stwórz docker-compose-pi.yml (~5 linii):

text
version: '3.8'
services:
quiz-app:
image: fullstack-quiz-armv7:latest
container_name: fullstack-quiz-app
ports:
- "8002:8002"
restart: unless-stopped
environment:
- JAVA_OPTS=-Xmx256m -Xms128m  # Mniejszy RAM na Pi
healthcheck:
test: ["CMD", "curl", "-f", "http://localhost:8002/api/questions/Spring/10"]
      interval: 30s
timeout: 3s
start_period: 40s
retries: 3