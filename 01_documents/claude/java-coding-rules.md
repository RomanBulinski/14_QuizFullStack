# Java Coding Rules for FullStack Quiz Application

## Code Style

### General Guidelines
- Follow **Google Java Style Guide**
- Use **Lombok** annotations to reduce boilerplate code
- Use **constructor injection** for dependencies (not field injection)
- Add **Javadoc** on all public methods
- Use **SLF4J** for logging

### Naming Conventions
- **Classes**: PascalCase (e.g., `QuizService`, `QuizController`)
- **Methods**: camelCase (e.g., `getRandomQuestions`, `loadQuestionsFromCsv`)
- **Variables**: camelCase (e.g., `questionCache`, `fileName`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DATA_PATH`, `CSV_EXTENSION`)
- **Packages**: lowercase (e.g., `com.fullstackquiz.service`)

### File Organization

```
src/main/java/com/fullstackquiz/
├── controller/    # REST API endpoints
├── service/       # Business logic
├── model/         # Data models (POJOs with Lombok)
├── config/        # Configuration classes
└── FullstackQuizApplication.java
```

## Software Engineering Principles

### SOLID Principles

#### S - Single Responsibility Principle (SRP)
Each class should have one, and only one, reason to change.

**Example:**
```java
// Good: Each class has a single responsibility
@Service
public class QuizService {
  // Only responsible for quiz business logic
  public List<Question> getRandomQuestions(String technology, int count) { }
}

@Service
public class CsvLoaderService {
  // Only responsible for loading CSV files
  public List<Question> loadFromCsv(String fileName) { }
}

// Bad: Multiple responsibilities in one class
@Service
public class QuizService {
  public List<Question> getRandomQuestions() { }
  public void loadFromCsv() { }        // CSV loading
  public void sendEmail() { }           // Email notification
  public void logToDatabase() { }       // Logging
}
```

#### O - Open/Closed Principle (OCP)
Software entities should be open for extension but closed for modification.

**Example:**
```java
// Good: Use interfaces for extension
public interface QuestionLoader {
  List<Question> load(String source);
}

@Service
public class CsvQuestionLoader implements QuestionLoader {
  public List<Question> load(String source) {
    // CSV loading logic
  }
}

@Service
public class DatabaseQuestionLoader implements QuestionLoader {
  public List<Question> load(String source) {
    // Database loading logic
  }
}

// Bad: Modifying existing code for new functionality
@Service
public class QuestionLoader {
  public List<Question> load(String source, String type) {
    if (type.equals("csv")) {
      // CSV logic
    } else if (type.equals("database")) {
      // Database logic - requires modification
    }
  }
}
```

#### L - Liskov Substitution Principle (LSP)
Derived classes must be substitutable for their base classes.

**Example:**
```java
// Good: Subclasses honor the contract
public abstract class QuestionProvider {
  public abstract List<Question> getQuestions(int count);
}

public class CachedQuestionProvider extends QuestionProvider {
  @Override
  public List<Question> getQuestions(int count) {
    // Returns List<Question> as expected
    return cachedQuestions.subList(0, count);
  }
}

// Bad: Subclass violates contract
public class BrokenQuestionProvider extends QuestionProvider {
  @Override
  public List<Question> getQuestions(int count) {
    throw new UnsupportedOperationException(); // Violates contract
  }
}
```

#### I - Interface Segregation Principle (ISP)
Clients should not be forced to depend on interfaces they don't use.

**Example:**
```java
// Good: Focused interfaces
public interface QuestionReader {
  List<Question> read(String source);
}

public interface QuestionWriter {
  void write(String destination, List<Question> questions);
}

// Service only implements what it needs
@Service
public class CsvQuestionService implements QuestionReader {
  public List<Question> read(String source) { }
}

// Bad: Fat interface forces unnecessary implementation
public interface QuestionRepository {
  List<Question> read(String source);
  void write(String destination, List<Question> questions);
  void delete(String id);
  void update(Question question);
  void archive(String id);
}

// Service forced to implement methods it doesn't need
@Service
public class ReadOnlyQuestionService implements QuestionRepository {
  public List<Question> read(String source) { }
  public void write(String destination, List<Question> questions) {
    throw new UnsupportedOperationException(); // Doesn't need this
  }
  // ... more unnecessary methods
}
```

#### D - Dependency Inversion Principle (DIP)
Depend on abstractions, not concretions.

**Example:**
```java
// Good: Depend on abstractions (interfaces)
@RestController
public class QuizController {
  private final QuizService quizService; // Interface

  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }
}

// Bad: Depend on concrete implementation
@RestController
public class QuizController {
  private final CsvQuizService csvQuizService; // Concrete class

  public QuizController(CsvQuizService csvQuizService) {
    this.csvQuizService = csvQuizService;
  }
}
```

### DRY (Don't Repeat Yourself)
Every piece of knowledge must have a single, unambiguous representation in the system.

**Example:**
```java
// Good: Extract common logic
@Service
public class QuizService {
  private static final int DEFAULT_QUESTION_COUNT = 10;

  private List<Question> getQuestionsForTechnology(String technology, int count) {
    List<Question> allQuestions = questionCache.get(technology.toLowerCase());
    return getRandomSubset(allQuestions, count);
  }

  private List<Question> getRandomSubset(List<Question> questions, int count) {
    List<Question> shuffled = new ArrayList<>(questions);
    Collections.shuffle(shuffled);
    return shuffled.subList(0, Math.min(count, questions.size()));
  }
}

// Bad: Repeated logic
@Service
public class QuizService {
  public List<Question> getSpringQuestions(int count) {
    List<Question> all = questionCache.get("spring");
    List<Question> shuffled = new ArrayList<>(all);
    Collections.shuffle(shuffled);
    return shuffled.subList(0, Math.min(count, all.size()));
  }

  public List<Question> getAngularQuestions(int count) {
    List<Question> all = questionCache.get("angular");
    List<Question> shuffled = new ArrayList<>(all);
    Collections.shuffle(shuffled); // Repeated logic
    return shuffled.subList(0, Math.min(count, all.size()));
  }
}
```

### KISS (Keep It Simple, Stupid)
Systems work best when kept simple rather than made complicated.

**Example:**
```java
// Good: Simple and clear
public boolean isValidCount(int count) {
  return count == 10 || count == 20 || count == 30;
}

// Bad: Overcomplicated
public boolean isValidCount(int count) {
  Set<Integer> validCounts = new HashSet<>(Arrays.asList(10, 20, 30));
  return Optional.ofNullable(count)
      .map(validCounts::contains)
      .orElse(false);
}
```

### YAGNI (You Aren't Gonna Need It)
Don't add functionality until it's necessary.

**Example:**
```java
// Good: Only implement what's needed now
@Service
public class QuizService {
  public List<Question> getRandomQuestions(String technology, int count) {
    // Simple implementation for current requirements
    return questionCache.get(technology.toLowerCase())
        .stream()
        .limit(count)
        .collect(Collectors.toList());
  }
}

// Bad: Over-engineering for potential future needs
@Service
public class QuizService {
  // Adding features that aren't required yet
  public List<Question> getRandomQuestions(
      String technology,
      int count,
      DifficultyLevel difficulty,      // Not needed yet
      List<String> tags,                // Not needed yet
      QuestionSortStrategy sortStrategy, // Not needed yet
      FilterCriteria criteria) {         // Not needed yet
    // Complex implementation for features that may never be used
  }
}
```

### Separation of Concerns
Different concerns should be separated into different modules/layers.

**Example:**
```java
// Good: Clear separation of concerns
@RestController  // Presentation layer
public class QuizController {
  private final QuizService quizService;

  @GetMapping("/api/questions/{technology}/{count}")
  public ResponseEntity<List<Question>> getQuestions(
      @PathVariable String technology,
      @PathVariable int count) {
    // Only handles HTTP concerns
    if (!isValidInput(technology, count)) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(quizService.getRandomQuestions(technology, count));
  }
}

@Service  // Business logic layer
public class QuizService {
  // Only handles business logic
  public List<Question> getRandomQuestions(String technology, int count) {
    List<Question> questions = questionCache.get(technology.toLowerCase());
    return randomize(questions, count);
  }
}

// Bad: Mixed concerns
@RestController
public class QuizController {
  @GetMapping("/api/questions/{technology}/{count}")
  public ResponseEntity<List<Question>> getQuestions(
      @PathVariable String technology,
      @PathVariable int count) {
    // Mixing HTTP, business logic, and data access
    List<Question> questions = new ArrayList<>();
    try (CSVReader reader = new CSVReader(...)) {  // Data access in controller
      reader.readAll().forEach(row -> {
        questions.add(new Question(...));  // Business logic in controller
      });
    }
    Collections.shuffle(questions);  // More business logic
    return ResponseEntity.ok(questions.subList(0, count));
  }
}
```

### Fail Fast
Detect and report errors as early as possible.

**Example:**
```java
// Good: Validate early
@Service
public class QuizService {
  public List<Question> getRandomQuestions(String technology, int count) {
    Objects.requireNonNull(technology, "Technology cannot be null");
    if (count <= 0) {
      throw new IllegalArgumentException("Count must be positive");
    }
    if (!questionCache.containsKey(technology.toLowerCase())) {
      throw new IllegalArgumentException("Unknown technology: " + technology);
    }
    // Continue with valid data
    return getQuestions(technology, count);
  }
}

// Bad: Late validation
@Service
public class QuizService {
  public List<Question> getRandomQuestions(String technology, int count) {
    List<Question> questions = questionCache.get(technology.toLowerCase());
    List<Question> shuffled = new ArrayList<>(questions);
    Collections.shuffle(shuffled);
    // Fails late with NullPointerException
    return shuffled.subList(0, count);
  }
}
```

### Composition Over Inheritance
Favor composition over inheritance for code reuse.

**Example:**
```java
// Good: Use composition
@Service
public class QuizService {
  private final QuestionLoader questionLoader;
  private final QuestionRandomizer questionRandomizer;

  public QuizService(QuestionLoader loader, QuestionRandomizer randomizer) {
    this.questionLoader = loader;
    this.questionRandomizer = randomizer;
  }

  public List<Question> getQuestions(String technology, int count) {
    List<Question> questions = questionLoader.load(technology);
    return questionRandomizer.randomize(questions, count);
  }
}

// Bad: Deep inheritance hierarchy
public abstract class BaseQuestionService {
  protected abstract List<Question> loadQuestions();
}

public abstract class CachedQuestionService extends BaseQuestionService {
  protected Map<String, List<Question>> cache;
}

public class CsvCachedQuestionService extends CachedQuestionService {
  // Tightly coupled to parent classes
}
```

## Architecture Rules

### Controllers
- Annotate with `@RestController`
- Use `@RequestMapping` for base path (e.g., `/api`)
- Use specific HTTP method annotations: `@GetMapping`, `@PostMapping`, etc.
- Add `@CrossOrigin` for CORS support
- Keep controllers thin - delegate business logic to services
- Return appropriate HTTP status codes

**Example:**
```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QuizController {
  private final QuizService quizService;

  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }

  @GetMapping("/questions/{technology}/{count}")
  public ResponseEntity<List<Question>> getQuestions(
      @PathVariable String technology,
      @PathVariable int count) {
    // Validation and delegation
  }
}
```

### Services
- Annotate with `@Service`
- Use `@PostConstruct` for initialization logic
- Keep services stateless when possible
- Use constructor injection for dependencies
- Handle exceptions appropriately

**Example:**
```java
@Service
public class QuizService {
  private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

  @PostConstruct
  public void init() {
    logger.info("Initializing service...");
  }
}
```

### Models
- Use **Lombok** annotations: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Keep models simple POJOs
- Use appropriate data types
- Make fields private with getters/setters via Lombok

**Example:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
  private String question;
  private String[] options;
  private int correctIndex;
}
```

### Configuration
- Annotate with `@Configuration`
- Use `@Bean` for bean definitions
- Keep configuration separate from business logic

## CSV Handling

### OpenCSV Usage
- Use **semicolon (`;`)** as delimiter
- Configure `CSVParser` with `CSVParserBuilder`
- Use try-with-resources for automatic resource management
- Handle `IOException` and `CsvException`

**Example:**
```java
CSVParser parser = new CSVParserBuilder()
    .withSeparator(';')
    .build();

try (CSVReader reader = new CSVReaderBuilder(
    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
    .withCSVParser(parser)
    .build()) {
  // Process CSV
} catch (IOException | CsvException e) {
  logger.error("Error: {}", e.getMessage());
}
```

### CSV Format Rules
- Delimiter: semicolon (`;`)
- Correct Index: 0-based (0, 1, 2, or 3)
- No headers in CSV files
- Encoding: UTF-8
- Format: `question;option1;option2;option3;option4;correctIndex`

## Logging

### SLF4J Guidelines
- Use appropriate log levels:
  - `logger.info()` - Application flow, important events
  - `logger.warn()` - Potential issues, fallback scenarios
  - `logger.error()` - Errors, exceptions
  - `logger.debug()` - Detailed debugging information

**Example:**
```java
private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

logger.info("Loaded {} questions for {}", questions.size(), technology);
logger.warn("Requested count {} exceeds available questions", count);
logger.error("Error loading questions: {}", e.getMessage());
```

## Exception Handling

### Best Practices
- Catch specific exceptions (not generic `Exception`)
- Log errors with context
- Return appropriate HTTP status codes in controllers
- Don't swallow exceptions silently
- Provide meaningful error messages

**Example:**
```java
try {
  // Code that may throw
} catch (IOException | CsvException e) {
  logger.error("Error loading questions for {}: {}", technology, e.getMessage());
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
}
```

## Validation

### Input Validation
- Validate path variables and request parameters
- Check for null/empty values
- Validate business rules (e.g., valid technology names, counts)
- Return 400 Bad Request for invalid input

**Example:**
```java
if (!isValidTechnology(technology)) {
  return ResponseEntity.badRequest().build();
}

if (count <= 0 || count > maxCount) {
  return ResponseEntity.badRequest().build();
}
```

## Resource Management

### File Handling
- Use `ClassPathResource` for classpath resources
- Always use try-with-resources for streams
- Use `StandardCharsets.UTF_8` for encoding
- Handle missing files gracefully

**Example:**
```java
ClassPathResource resource = new ClassPathResource("data/file.csv");
try (InputStreamReader reader = new InputStreamReader(
    resource.getInputStream(), StandardCharsets.UTF_8)) {
  // Process resource
}
```

## Collections

### Guidelines
- Use appropriate collection types:
  - `List` for ordered collections
  - `Map` for key-value pairs
  - `Set` for unique elements
- Use `Collections.shuffle()` for randomization
- Use `ArrayList` for mutable lists
- Initialize with appropriate capacity when size is known

**Example:**
```java
private final Map<String, List<Question>> questionCache = new HashMap<>();

List<Question> shuffled = new ArrayList<>(allQuestions);
Collections.shuffle(shuffled);
return shuffled.subList(0, count);
```

## Testing

### Unit Test Guidelines
- Use JUnit 5
- Use `@WebMvcTest` for controller tests
- Use Mockito for mocking dependencies
- Name tests descriptively: `testMethodName_Scenario_ExpectedResult`
- Test edge cases and error conditions

**Example:**
```java
@WebMvcTest(QuizController.class)
class QuizControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private QuizService quizService;

  @Test
  void testGetQuestions_ValidRequest_ReturnsQuestions() throws Exception {
    // Arrange
    List<Question> mockQuestions = Arrays.asList(/* ... */);
    when(quizService.getRandomQuestions("spring", 10))
        .thenReturn(mockQuestions);

    // Act & Assert
    mockMvc.perform(get("/api/questions/spring/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(10));
  }
}
```

## Security Considerations

### Current Setup
- No authentication required (per requirements)
- CORS enabled for all origins
- No sensitive data in logs

### Future Considerations
- Add rate limiting for API endpoints
- Implement authentication (Spring Security + JWT)
- Restrict CORS in production
- Add input sanitization
- Validate file uploads if added

## Performance

### Best Practices
- Cache data in memory when appropriate
- Use `@PostConstruct` to load data at startup
- Avoid N+1 queries (when using databases)
- Use connection pooling (when using databases)
- Profile before optimizing

## Checkstyle Compliance

### Configuration
- Uses Google Java Style Guide
- Runs during `validate` phase
- Set to `failsOnError=false` (warnings only)
- Fix warnings before committing

### Common Issues
- Line length > 100 characters
- Missing Javadoc on public methods
- Incorrect indentation (2 spaces)
- Import ordering

## Do's and Don'ts

### DO
- Use Lombok to reduce boilerplate
- Use constructor injection
- Write meaningful log messages
- Validate input at boundaries
- Handle exceptions properly
- Write unit tests for critical paths
- Use appropriate HTTP status codes

### DON'T
- Use field injection (`@Autowired` on fields)
- Swallow exceptions without logging
- Use generic `Exception` catches
- Put business logic in controllers
- Use hardcoded values (use constants)
- Ignore Checkstyle warnings
- Return null (use Optional or empty collections)

## Common Patterns

### Singleton Service with Cache
```java
@Service
public class QuizService {
  private final Map<String, List<Question>> cache = new HashMap<>();

  @PostConstruct
  public void init() {
    // Load data into cache
  }
}
```

### Controller Response Patterns
```java
// Success with data
return ResponseEntity.ok(data);

// Bad request
return ResponseEntity.badRequest().build();

// Not found
return ResponseEntity.notFound().build();

// Created
return ResponseEntity.status(HttpStatus.CREATED).body(data);
```

### Resource Loading
```java
ClassPathResource resource = new ClassPathResource("data/file.csv");
if (!resource.exists()) {
  logger.warn("Resource not found: {}", fileName);
  return Collections.emptyList();
}
```
