import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Question } from '../shared/models/question.model';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.scss']
})
export class SummaryComponent implements OnInit {
  questions: Question[] = [];
  answers: number[] = [];
  technology: string = '';
  correctCount: number = 0;
  totalQuestions: number = 0;
  percentage: number = 0;

  constructor(private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as {
      questions: Question[];
      answers: number[];
      technology: string;
    };

    if (state) {
      this.questions = state.questions;
      this.answers = state.answers;
      this.technology = state.technology;
    } else {
      this.router.navigate(['/selection']);
    }
  }

  ngOnInit(): void {
    this.calculateScore();
  }

  calculateScore(): void {
    this.totalQuestions = this.questions.length;
    this.correctCount = 0;

    for (let i = 0; i < this.questions.length; i++) {
      if (this.answers[i] === this.questions[i].correctIndex) {
        this.correctCount++;
      }
    }

    this.percentage = (this.correctCount / this.totalQuestions) * 100;
  }

  get suggestionMessage(): string {
    if (this.percentage < 50) {
      return 'Keep studying! Review the material and try again to improve your score.';
    } else if (this.percentage < 80) {
      return 'Good job! You have a solid understanding, but there\'s room for improvement.';
    } else {
      return 'Excellent work! You have demonstrated strong mastery of the topic!';
    }
  }

  get scoreClass(): string {
    if (this.percentage < 50) {
      return 'low-score';
    } else if (this.percentage < 80) {
      return 'medium-score';
    } else {
      return 'high-score';
    }
  }

  tryAgain(): void {
    this.router.navigate(['/selection']);
  }

  getQuestionResult(index: number): boolean {
    return this.answers[index] === this.questions[index].correctIndex;
  }
}
