import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-selection',
  templateUrl: './selection.component.html',
  styleUrls: ['./selection.component.scss']
})
export class SelectionComponent {
  selectedTechnology: string | null = null;
  selectedCount: number = 10;

  questionCounts = [10, 20, 30];

  constructor(private router: Router) {}

  selectTechnology(technology: string): void {
    this.selectedTechnology = technology;
  }

  selectCount(count: number): void {
    this.selectedCount = count;
  }

  startQuiz(): void {
    if (this.selectedTechnology) {
      this.router.navigate(['/quiz'], {
        state: {
          technology: this.selectedTechnology,
          count: this.selectedCount
        }
      });
    }
  }
}
