import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AppInfoService } from '../../services/app-info.service';
import { AppInfo } from '../../models/app-info.model';

/**
 * Footer component displaying application version.
 */
@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit, OnDestroy {
  version = '';
  private readonly destroy$ = new Subject<void>();

  constructor(private readonly appInfoService: AppInfoService) { }

  ngOnInit(): void {
    this.loadAppInfo();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load application information from the backend.
   */
  private loadAppInfo(): void {
    this.appInfoService.getAppInfo()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (appInfo: AppInfo) => {
          this.version = appInfo.version;
        },
        error: (error) => {
          console.error('Failed to load application info:', error);
          this.version = 'Unknown';
        }
      });
  }
}
