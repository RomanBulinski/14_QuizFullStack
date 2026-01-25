# Angular TypeScript Coding Rules for FullStack Quiz Application

## Code Style

### General Guidelines
- Follow **Angular Style Guide**
- Use **TypeScript** strict mode
- Avoid using `any` type - always provide explicit types
- Use **RxJS** for async operations
- Use **SCSS** for styling with BEM-like structure
- Use **Angular Material** for UI components

### Naming Conventions
- **Components**: PascalCase (e.g., `QuizComponent`, `SelectionComponent`)
- **Component Files**: kebab-case (e.g., `quiz.component.ts`, `selection.component.ts`)
- **Services**: PascalCase with Service suffix (e.g., `QuizApiService`)
- **Interfaces/Models**: PascalCase (e.g., `Question`, `QuizConfig`)
- **Methods**: camelCase (e.g., `getQuestions`, `submitAnswer`)
- **Properties**: camelCase (e.g., `currentQuestion`, `selectedOption`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `API_BASE_URL`, `MAX_QUESTIONS`)

### File Organization

```
angular-src/src/app/
├── selection/                          # Feature module
│   ├── selection.component.ts          # Component logic
│   ├── selection.component.html        # Template
│   ├── selection.component.scss        # Styles
│   └── selection.component.spec.ts     # Tests
├── quiz/                               # Feature module
├── summary/                            # Feature module
└── shared/
    ├── models/                         # TypeScript interfaces
    │   └── question.model.ts
    ├── services/                       # Injectable services
    │   └── quiz-api.service.ts
    └── components/                     # Reusable components
        └── progress-bar/
```

## Software Engineering Principles

### SOLID Principles

#### S - Single Responsibility Principle (SRP)
Each component/service should have one reason to change.

**Example:**
```typescript
// Good: Single responsibility
@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html'
})
export class QuizComponent {
  // Only responsible for quiz presentation
  displayQuestion(): void { }
  handleUserAnswer(): void { }
}

@Injectable({ providedIn: 'root' })
export class QuizApiService {
  // Only responsible for API communication
  getQuestions(): Observable<Question[]> { }
}

@Injectable({ providedIn: 'root' })
export class QuizStateService {
  // Only responsible for state management
  updateScore(): void { }
  getCurrentQuestion(): Question { }
}

// Bad: Multiple responsibilities
@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html'
})
export class QuizComponent {
  displayQuestion(): void { }
  handleUserAnswer(): void { }
  makeHttpRequest(): Observable<Question[]> { }  // API logic
  calculateScore(): number { }                    // Business logic
  saveToLocalStorage(): void { }                  // Storage logic
}
```

#### O - Open/Closed Principle (OCP)
Open for extension, closed for modification.

**Example:**
```typescript
// Good: Use interfaces and abstractions
export interface QuestionLoader {
  load(technology: string, count: number): Observable<Question[]>;
}

@Injectable({ providedIn: 'root' })
export class ApiQuestionLoader implements QuestionLoader {
  load(technology: string, count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.apiUrl}/${technology}/${count}`);
  }
}

@Injectable({ providedIn: 'root' })
export class CachedQuestionLoader implements QuestionLoader {
  load(technology: string, count: number): Observable<Question[]> {
    // Load from cache
  }
}

// Bad: Modifying existing code for new functionality
@Injectable({ providedIn: 'root' })
export class QuestionLoader {
  load(technology: string, count: number, source: string): Observable<Question[]> {
    if (source === 'api') {
      // API logic
    } else if (source === 'cache') {
      // Cache logic - requires modification
    }
  }
}
```

#### L - Liskov Substitution Principle (LSP)
Subtypes must be substitutable for their base types.

**Example:**
```typescript
// Good: Subclasses honor the contract
export abstract class QuestionProvider {
  abstract getQuestions(count: number): Observable<Question[]>;
}

export class OnlineQuestionProvider extends QuestionProvider {
  getQuestions(count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`/api/questions/${count}`);
  }
}

export class OfflineQuestionProvider extends QuestionProvider {
  getQuestions(count: number): Observable<Question[]> {
    return of(this.cachedQuestions.slice(0, count));
  }
}

// Bad: Subclass violates contract
export class BrokenQuestionProvider extends QuestionProvider {
  getQuestions(count: number): Observable<Question[]> {
    throw new Error('Not implemented'); // Violates contract
  }
}
```

#### I - Interface Segregation Principle (ISP)
Many client-specific interfaces are better than one general-purpose interface.

**Example:**
```typescript
// Good: Focused interfaces
export interface QuestionReader {
  getQuestions(technology: string, count: number): Observable<Question[]>;
}

export interface QuestionWriter {
  saveQuestions(questions: Question[]): Observable<void>;
}

export interface QuestionDeleter {
  deleteQuestion(id: string): Observable<void>;
}

// Service implements only what it needs
@Injectable({ providedIn: 'root' })
export class QuizApiService implements QuestionReader {
  getQuestions(technology: string, count: number): Observable<Question[]> { }
}

// Bad: Fat interface
export interface QuestionRepository {
  getQuestions(technology: string, count: number): Observable<Question[]>;
  saveQuestions(questions: Question[]): Observable<void>;
  deleteQuestion(id: string): Observable<void>;
  updateQuestion(question: Question): Observable<void>;
  archiveQuestion(id: string): Observable<void>;
}

// Service forced to implement unused methods
@Injectable({ providedIn: 'root' })
export class ReadOnlyQuizService implements QuestionRepository {
  getQuestions(): Observable<Question[]> { }
  saveQuestions(): Observable<void> {
    throw new Error('Not supported'); // Forced to implement
  }
  deleteQuestion(): Observable<void> {
    throw new Error('Not supported');
  }
  // ... more unused methods
}
```

#### D - Dependency Inversion Principle (DIP)
Depend on abstractions, not concretions.

**Example:**
```typescript
// Good: Depend on abstractions
export interface QuestionService {
  getQuestions(tech: string, count: number): Observable<Question[]>;
}

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html'
})
export class QuizComponent {
  constructor(
    @Inject('QuestionService') private questionService: QuestionService
  ) {}
}

// Bad: Depend on concrete implementation
@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html'
})
export class QuizComponent {
  constructor(
    private apiService: HttpQuizApiService // Concrete class
  ) {}
}
```

### DRY (Don't Repeat Yourself)
Eliminate code duplication.

**Example:**
```typescript
// Good: Extract common logic
@Injectable({ providedIn: 'root' })
export class QuizApiService {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getSpringQuestions(count: number): Observable<Question[]> {
    return this.getQuestions('spring', count);
  }

  getAngularQuestions(count: number): Observable<Question[]> {
    return this.getQuestions('angular', count);
  }

  private getQuestions(technology: string, count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.baseUrl}/questions/${technology}/${count}`)
      .pipe(
        retry(2),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('API Error:', error);
    return throwError(() => new Error('Failed to load questions'));
  }
}

// Bad: Repeated logic
@Injectable({ providedIn: 'root' })
export class QuizApiService {
  getSpringQuestions(count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.baseUrl}/questions/spring/${count}`)
      .pipe(
        retry(2),
        catchError(error => {
          console.error('API Error:', error);
          return throwError(() => new Error('Failed to load questions'));
        })
      );
  }

  getAngularQuestions(count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.baseUrl}/questions/angular/${count}`)
      .pipe(
        retry(2), // Duplicated logic
        catchError(error => {
          console.error('API Error:', error);
          return throwError(() => new Error('Failed to load questions'));
        })
      );
  }
}
```

### KISS (Keep It Simple, Stupid)
Simplicity should be a key goal in design.

**Example:**
```typescript
// Good: Simple and clear
isValidCount(count: number): boolean {
  return count === 10 || count === 20 || count === 30;
}

// Bad: Overcomplicated
isValidCount(count: number): boolean {
  const validCounts = new Set([10, 20, 30]);
  return Array.from(validCounts)
    .filter(c => c === count)
    .reduce((acc, val) => acc || val === count, false);
}
```

### YAGNI (You Aren't Gonna Need It)
Don't implement features until they're actually needed.

**Example:**
```typescript
// Good: Only implement current requirements
export interface Question {
  question: string;
  options: string[];
  correctIndex: number;
}

@Injectable({ providedIn: 'root' })
export class QuizApiService {
  getQuestions(technology: string, count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.baseUrl}/questions/${technology}/${count}`);
  }
}

// Bad: Over-engineering for future needs
export interface Question {
  id: string;                    // Not needed yet
  question: string;
  options: string[];
  correctIndex: number;
  difficulty?: 'easy' | 'medium' | 'hard';  // Not needed yet
  tags?: string[];               // Not needed yet
  explanation?: string;          // Not needed yet
  category?: string;             // Not needed yet
  author?: string;               // Not needed yet
  version?: number;              // Not needed yet
}

@Injectable({ providedIn: 'root' })
export class QuizApiService {
  getQuestions(
    technology: string,
    count: number,
    difficulty?: string,         // Not needed yet
    tags?: string[],             // Not needed yet
    sortBy?: string,             // Not needed yet
    filter?: FilterCriteria      // Not needed yet
  ): Observable<Question[]> {
    // Complex implementation for features that may never be used
  }
}
```

### Separation of Concerns
Separate different responsibilities into distinct modules.

**Example:**
```typescript
// Good: Clear separation

// Presentation layer (Component)
@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html'
})
export class QuizComponent {
  questions$ = this.quizService.questions$;

  constructor(private quizService: QuizStateService) {}

  selectAnswer(index: number): void {
    this.quizService.selectAnswer(index);
  }
}

// State management layer (Service)
@Injectable({ providedIn: 'root' })
export class QuizStateService {
  private questionsSubject = new BehaviorSubject<Question[]>([]);
  questions$ = this.questionsSubject.asObservable();

  constructor(private apiService: QuizApiService) {}

  loadQuestions(technology: string, count: number): void {
    this.apiService.getQuestions(technology, count)
      .subscribe(questions => this.questionsSubject.next(questions));
  }
}

// API layer (Service)
@Injectable({ providedIn: 'root' })
export class QuizApiService {
  getQuestions(technology: string, count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.baseUrl}/questions/${technology}/${count}`);
  }
}

// Bad: Mixed concerns
@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html'
})
export class QuizComponent {
  questions: Question[] = [];

  constructor(private http: HttpClient) {}

  loadQuestions(technology: string, count: number): void {
    // Mixing presentation, state, and API logic
    this.http.get<Question[]>(`http://localhost:8080/api/questions/${technology}/${count}`)
      .pipe(
        map(questions => questions.slice(0, count)),
        catchError(error => {
          console.error(error);
          return of([]);
        })
      )
      .subscribe(questions => {
        this.questions = questions;
        localStorage.setItem('questions', JSON.stringify(questions));
      });
  }
}
```

### Fail Fast
Validate inputs early and fail fast.

**Example:**
```typescript
// Good: Validate early
@Injectable({ providedIn: 'root' })
export class QuizApiService {
  getQuestions(technology: string, count: number): Observable<Question[]> {
    if (!technology || technology.trim() === '') {
      return throwError(() => new Error('Technology is required'));
    }
    if (count <= 0 || count > 100) {
      return throwError(() => new Error('Count must be between 1 and 100'));
    }
    // Continue with valid data
    return this.http.get<Question[]>(`${this.baseUrl}/questions/${technology}/${count}`);
  }
}

// Bad: Late validation
@Injectable({ providedIn: 'root' })
export class QuizApiService {
  getQuestions(technology: string, count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.baseUrl}/questions/${technology}/${count}`)
      .pipe(
        map(questions => questions.slice(0, count)),  // May fail with invalid count
        map(questions => {
          if (!technology) {  // Too late to validate
            throw new Error('Technology is required');
          }
          return questions;
        })
      );
  }
}
```

### Composition Over Inheritance
Favor object composition over class inheritance.

**Example:**
```typescript
// Good: Use composition
@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html'
})
export class QuizComponent {
  // Compose functionality from multiple services
  constructor(
    private apiService: QuizApiService,
    private stateService: QuizStateService,
    private validatorService: ValidationService
  ) {}

  loadQuestions(technology: string, count: number): void {
    if (this.validatorService.isValid(technology, count)) {
      this.apiService.getQuestions(technology, count)
        .subscribe(q => this.stateService.setQuestions(q));
    }
  }
}

// Bad: Deep inheritance
export abstract class BaseQuizComponent {
  abstract loadQuestions(): void;
}

export abstract class CachedQuizComponent extends BaseQuizComponent {
  protected cache: Map<string, Question[]>;
}

export class OnlineQuizComponent extends CachedQuizComponent {
  // Tightly coupled to parent classes
}
```

### Immutability
Prefer immutable data structures.

**Example:**
```typescript
// Good: Immutable state updates
export interface QuizState {
  questions: Question[];
  currentIndex: number;
  answers: number[];
}

export class QuizComponent {
  private state: QuizState = {
    questions: [],
    currentIndex: 0,
    answers: []
  };

  nextQuestion(): void {
    this.state = {
      ...this.state,
      currentIndex: this.state.currentIndex + 1
    };
  }

  addAnswer(answer: number): void {
    this.state = {
      ...this.state,
      answers: [...this.state.answers, answer]
    };
  }
}

// Bad: Mutating state directly
export class QuizComponent {
  state = {
    questions: [],
    currentIndex: 0,
    answers: []
  };

  nextQuestion(): void {
    this.state.currentIndex++; // Direct mutation
  }

  addAnswer(answer: number): void {
    this.state.answers.push(answer); // Direct mutation
  }
}
```

### Pure Functions
Functions should not have side effects and return the same output for the same input.

**Example:**
```typescript
// Good: Pure function
calculateScore(questions: Question[], answers: number[]): number {
  return questions.reduce((score, question, index) => {
    return question.correctIndex === answers[index] ? score + 1 : score;
  }, 0);
}

getPercentage(score: number, total: number): number {
  return (score / total) * 100;
}

// Bad: Impure function with side effects
calculateScore(questions: Question[], answers: number[]): number {
  this.lastScore = 0; // Side effect: modifies external state
  console.log('Calculating score'); // Side effect: I/O
  localStorage.setItem('calculating', 'true'); // Side effect: storage

  questions.forEach((question, index) => {
    if (question.correctIndex === answers[index]) {
      this.lastScore++; // Modifying external state
    }
  });

  return this.lastScore;
}
```

## TypeScript Rules

### Type Safety
- Always specify return types for methods
- Always specify types for parameters
- Use interfaces for object shapes
- Use enums for fixed sets of values
- Avoid `any` - use `unknown` if type is truly unknown

**Example:**
```typescript
// Good
getQuestions(technology: string, count: number): Observable<Question[]> {
  return this.http.get<Question[]>(`${this.apiUrl}/${technology}/${count}`);
}

// Bad
getQuestions(technology, count) {
  return this.http.get(`${this.apiUrl}/${technology}/${count}`);
}
```

### Interfaces and Models
- Create interfaces for all data structures
- Use `interface` for data shapes
- Use `class` when you need methods/logic
- Keep models in `shared/models/` directory

**Example:**
```typescript
export interface Question {
  question: string;
  options: string[];
  correctIndex: number;
  userAnswer?: number;
}

export interface QuizState {
  technology: string;
  count: number;
  questions: Question[];
  currentIndex: number;
}
```

### Enums
```typescript
export enum Technology {
  Spring = 'spring',
  Angular = 'angular'
}

export enum QuizStatus {
  NotStarted = 'not-started',
  InProgress = 'in-progress',
  Completed = 'completed'
}
```

## Component Rules

### Component Structure
- Keep components focused and single-purpose
- Use OnPush change detection when possible
- Implement lifecycle hooks explicitly
- Unsubscribe from observables in `ngOnDestroy`

**Example:**
```typescript
@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuizComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  constructor(
    private quizService: QuizApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadQuestions();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadQuestions(): void {
    this.quizService.getQuestions('spring', 10)
      .pipe(takeUntil(this.destroy$))
      .subscribe(questions => {
        // Handle questions
      });
  }
}
```

### Component Properties
- Use access modifiers: `public`, `private`, `protected`
- Order: public properties, private properties, constructor, lifecycle hooks, public methods, private methods
- Initialize properties when declaring or in constructor

**Example:**
```typescript
export class QuizComponent {
  // Public properties (used in template)
  questions: Question[] = [];
  currentIndex = 0;
  selectedOption: number | null = null;

  // Private properties
  private readonly apiUrl = environment.apiUrl;
  private destroy$ = new Subject<void>();

  constructor(private quizService: QuizApiService) {}

  ngOnInit(): void { }

  // Public methods
  selectOption(index: number): void { }

  submitAnswer(): void { }

  // Private methods
  private loadQuestions(): void { }
}
```

## Service Rules

### Service Structure
- Annotate with `@Injectable({ providedIn: 'root' })`
- Keep services stateless when possible
- Use HttpClient for API calls
- Handle errors appropriately with RxJS operators

**Example:**
```typescript
@Injectable({
  providedIn: 'root'
})
export class QuizApiService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getQuestions(technology: string, count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.apiUrl}/questions/${technology}/${count}`)
      .pipe(
        retry(2),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('API Error:', error);
    return throwError(() => new Error('Failed to load questions'));
  }
}
```

## RxJS Best Practices

### Observable Management
- Always unsubscribe from observables
- Use `takeUntil` pattern for automatic unsubscription
- Use `async` pipe in templates when possible
- Use appropriate operators: `map`, `filter`, `switchMap`, etc.

**Example:**
```typescript
// Using takeUntil pattern
private destroy$ = new Subject<void>();

ngOnInit(): void {
  this.quizService.getQuestions('spring', 10)
    .pipe(takeUntil(this.destroy$))
    .subscribe(questions => {
      this.questions = questions;
    });
}

ngOnDestroy(): void {
  this.destroy$.next();
  this.destroy$.complete();
}

// Using async pipe (no manual subscription needed)
questions$ = this.quizService.getQuestions('spring', 10);

// Template: <div *ngFor="let question of questions$ | async">
```

### Common Operators
```typescript
// Transform data
this.http.get<Question[]>(url)
  .pipe(
    map(questions => questions.slice(0, 10)),
    tap(questions => console.log('Loaded:', questions.length)),
    catchError(error => of([]))
  );

// Switch to new observable
this.searchTerm$
  .pipe(
    debounceTime(300),
    distinctUntilChanged(),
    switchMap(term => this.search(term))
  );

// Combine multiple observables
combineLatest([this.tech$, this.count$])
  .pipe(
    switchMap(([tech, count]) => this.getQuestions(tech, count))
  );
```

## Template Rules

### Template Syntax
- Use structural directives: `*ngIf`, `*ngFor`, `*ngSwitch`
- Use property binding: `[property]="value"`
- Use event binding: `(event)="handler($event)"`
- Use two-way binding sparingly: `[(ngModel)]="property"`

**Example:**
```html
<!-- Structural directives -->
<div *ngIf="questions.length > 0; else loading">
  <div *ngFor="let question of questions; let i = index; trackBy: trackByIndex">
    {{ i + 1 }}. {{ question.question }}
  </div>
</div>

<ng-template #loading>
  <mat-spinner></mat-spinner>
</ng-template>

<!-- Property binding -->
<button [disabled]="selectedOption === null">Submit</button>

<!-- Event binding -->
<button (click)="submitAnswer()">Submit</button>

<!-- Two-way binding -->
<input [(ngModel)]="searchTerm" />
```

### Template Best Practices
- Keep logic out of templates
- Use `trackBy` with `*ngFor` for performance
- Use `ng-container` for grouping without extra DOM elements
- Use `ng-template` for conditional content
- Avoid complex expressions in templates

**Example:**
```typescript
// Component
trackByIndex(index: number, item: any): number {
  return index;
}

trackByQuestionId(index: number, question: Question): string {
  return question.id;
}

// Template
<div *ngFor="let question of questions; trackBy: trackByIndex">
  {{ question.question }}
</div>
```

## Routing

### Router Configuration
- Use `HashLocationStrategy` for SPA compatibility
- Pass data via router state
- Use route guards for authentication (if needed)

**Example:**
```typescript
// Navigation with state
this.router.navigate(['/quiz'], {
  state: {
    technology: this.selectedTech,
    count: this.selectedCount,
    questions: this.questions
  }
});

// Receiving state
const navigation = this.router.getCurrentNavigation();
const state = navigation?.extras.state as QuizState;
if (state) {
  this.questions = state.questions;
}
```

### Route Configuration
```typescript
const routes: Routes = [
  { path: '', redirectTo: '/selection', pathMatch: 'full' },
  { path: 'selection', component: SelectionComponent },
  { path: 'quiz', component: QuizComponent },
  { path: 'summary', component: SummaryComponent },
  { path: '**', redirectTo: '/selection' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    useHash: true  // HashLocationStrategy
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

## Styling (SCSS)

### SCSS Guidelines
- Use BEM-like naming: `.block__element--modifier`
- Scope styles to component
- Use Angular Material theme variables
- Avoid deep selectors (`::ng-deep`) when possible

**Example:**
```scss
// quiz.component.scss
.quiz {
  padding: 20px;

  &__header {
    margin-bottom: 20px;
    font-size: 1.5rem;
  }

  &__question {
    margin-bottom: 15px;
  }

  &__options {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  &__option {
    padding: 10px;
    border: 1px solid #ccc;
    cursor: pointer;

    &--selected {
      background-color: #e3f2fd;
      border-color: #2196f3;
    }

    &--correct {
      background-color: #c8e6c9;
      border-color: #4caf50;
    }

    &--incorrect {
      background-color: #ffcdd2;
      border-color: #f44336;
    }
  }
}
```

## Angular Material

### Usage Guidelines
- Import only needed modules
- Use Material components consistently
- Customize theme when needed
- Follow Material Design guidelines

**Example:**
```typescript
// Import specific modules
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatRadioModule } from '@angular/material/radio';

// Use in template
<mat-card class="quiz__card">
  <mat-card-content>
    <mat-radio-group [(ngModel)]="selectedOption">
      <mat-radio-button *ngFor="let option of question.options; let i = index" [value]="i">
        {{ option }}
      </mat-radio-button>
    </mat-radio-group>
  </mat-card-content>
  <mat-card-actions>
    <button mat-raised-button color="primary" (click)="submitAnswer()">Submit</button>
  </mat-card-actions>
</mat-card>
```

## Forms

### Reactive Forms (Preferred)
```typescript
// Component
export class SelectionComponent implements OnInit {
  selectionForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.selectionForm = this.fb.group({
      technology: ['spring', Validators.required],
      count: [10, [Validators.required, Validators.min(10), Validators.max(30)]]
    });
  }

  onSubmit(): void {
    if (this.selectionForm.valid) {
      const { technology, count } = this.selectionForm.value;
      // Process form
    }
  }
}

// Template
<form [formGroup]="selectionForm" (ngSubmit)="onSubmit()">
  <mat-form-field>
    <mat-select formControlName="technology">
      <mat-option value="spring">Spring</mat-option>
      <mat-option value="angular">Angular</mat-option>
    </mat-select>
  </mat-form-field>
  <button mat-button type="submit" [disabled]="!selectionForm.valid">Start Quiz</button>
</form>
```

## Testing

### Unit Test Guidelines
- Use Jasmine and Karma
- Test components, services, and pipes
- Mock dependencies with spies
- Use `TestBed` for component testing

**Example:**
```typescript
describe('QuizApiService', () => {
  let service: QuizApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [QuizApiService]
    });
    service = TestBed.inject(QuizApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch questions', () => {
    const mockQuestions: Question[] = [
      { question: 'Test?', options: ['A', 'B'], correctIndex: 0 }
    ];

    service.getQuestions('spring', 10).subscribe(questions => {
      expect(questions.length).toBe(1);
      expect(questions[0].question).toBe('Test?');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/questions/spring/10');
    expect(req.request.method).toBe('GET');
    req.flush(mockQuestions);
  });
});
```

## Error Handling

### HTTP Error Handling
```typescript
getQuestions(technology: string, count: number): Observable<Question[]> {
  return this.http.get<Question[]>(`${this.apiUrl}/questions/${technology}/${count}`)
    .pipe(
      retry(2),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          console.error('Questions not found');
          return of([]);
        }
        if (error.status === 400) {
          console.error('Invalid request');
          return throwError(() => new Error('Invalid parameters'));
        }
        return throwError(() => new Error('Server error'));
      })
    );
}
```

## Performance Optimization

### Change Detection
- Use `OnPush` change detection strategy
- Use `trackBy` with `*ngFor`
- Avoid expensive operations in templates
- Use `async` pipe for observables

### Lazy Loading
```typescript
const routes: Routes = [
  {
    path: 'quiz',
    loadChildren: () => import('./quiz/quiz.module').then(m => m.QuizModule)
  }
];
```

## Do's and Don'ts

### DO
- Use TypeScript strict mode
- Type everything explicitly
- Use RxJS operators for async operations
- Unsubscribe from observables
- Use Angular Material components
- Use async pipe in templates
- Write unit tests
- Follow Angular style guide
- Use OnPush change detection
- Use trackBy with ngFor

### DON'T
- Use `any` type
- Subscribe in templates (use async pipe)
- Forget to unsubscribe
- Put business logic in templates
- Use inline styles (use SCSS)
- Mutate state directly
- Use `::ng-deep` unnecessarily
- Ignore TypeScript errors
- Skip error handling
- Use `var` (use `const` or `let`)

## Common Patterns

### Loading State Pattern
```typescript
export class QuizComponent {
  isLoading = false;
  error: string | null = null;
  questions: Question[] = [];

  loadQuestions(): void {
    this.isLoading = true;
    this.error = null;

    this.quizService.getQuestions('spring', 10)
      .pipe(
        finalize(() => this.isLoading = false)
      )
      .subscribe({
        next: questions => this.questions = questions,
        error: err => this.error = err.message
      });
  }
}
```

### State Management Pattern
```typescript
export interface QuizState {
  questions: Question[];
  currentIndex: number;
  userAnswers: number[];
}

export class QuizComponent {
  private state: QuizState = {
    questions: [],
    currentIndex: 0,
    userAnswers: []
  };

  get currentQuestion(): Question {
    return this.state.questions[this.state.currentIndex];
  }

  nextQuestion(): void {
    this.state = {
      ...this.state,
      currentIndex: this.state.currentIndex + 1
    };
  }
}
```

### Safe Navigation
```typescript
// Use optional chaining
const questionText = this.currentQuestion?.question ?? 'No question';

// Use nullish coalescing
const count = this.selectedCount ?? 10;

// Use type guards
if (this.questions && this.questions.length > 0) {
  // Safe to access
}
```
