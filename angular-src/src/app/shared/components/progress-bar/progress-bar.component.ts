import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-progress-bar',
  templateUrl: './progress-bar.component.html',
  styleUrls: ['./progress-bar.component.scss']
})
export class ProgressBarComponent {
  @Input() current: number = 0;
  @Input() total: number = 0;

  get percentage(): number {
    if (this.total === 0) return 0;
    return (this.current / this.total) * 100;
  }
}
