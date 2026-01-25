import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { QuizApiService } from '../shared/services/quiz-api.service';
import { Question } from '../shared/models/question.model';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.scss']
})
export class QuizComponent implements OnInit {
  technology: string = '';
  count: number = 10;
  questions: Question[] = [];
  currentQuestionIndex: number = 0;
  selectedAnswers: number[] = [];
  selectedAnswer: number | null = null;
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private router: Router,
    private quizApiService: QuizApiService
  ) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { technology: string; count: number };

    if (state) {
      this.technology = state.technology;
      this.count = state.count;
    } else {
      this.router.navigate(['/selection']);
    }
  }

  ngOnInit(): void {
    if (this.technology && this.count) {
      this.loadQuestions();
    }
  }

  loadQuestions(): void {
    this.loading = true;
    this.quizApiService.getQuestions(this.technology, this.count).subscribe({
      next: (questions) => {
        this.questions = questions;
        this.selectedAnswers = new Array(questions.length).fill(-1);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading questions:', error);
        this.error = 'Failed to load questions. Please try again.';
        this.loading = false;
      }
    });
  }

  selectOption(optionIndex: number): void {
    this.selectedAnswer = optionIndex;
  }

  nextQuestion(): void {
    if (this.selectedAnswer !== null) {
      this.selectedAnswers[this.currentQuestionIndex] = this.selectedAnswer;

      if (this.currentQuestionIndex < this.questions.length - 1) {
        this.currentQuestionIndex++;
        this.selectedAnswer = this.selectedAnswers[this.currentQuestionIndex];
        if (this.selectedAnswer === -1) {
          this.selectedAnswer = null;
        }
      } else {
        this.finishQuiz();
      }
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.selectedAnswers[this.currentQuestionIndex] = this.selectedAnswer !== null ? this.selectedAnswer : -1;
      this.currentQuestionIndex--;
      this.selectedAnswer = this.selectedAnswers[this.currentQuestionIndex];
      if (this.selectedAnswer === -1) {
        this.selectedAnswer = null;
      }
    }
  }

  finishQuiz(): void {
    this.router.navigate(['/summary'], {
      state: {
        questions: this.questions,
        answers: this.selectedAnswers,
        technology: this.technology
      }
    });
  }

  get currentQuestion(): Question | null {
    return this.questions[this.currentQuestionIndex] || null;
  }

  get isLastQuestion(): boolean {
    return this.currentQuestionIndex === this.questions.length - 1;
  }

  get canProceed(): boolean {
    return this.selectedAnswer !== null;
  }
}
