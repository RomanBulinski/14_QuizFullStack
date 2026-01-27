import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppInfo } from '../models/app-info.model';

/**
 * Service for fetching application information.
 */
@Injectable({
  providedIn: 'root'
})
export class AppInfoService {
  private readonly apiUrl = '/api/info';

  constructor(private readonly http: HttpClient) { }

  /**
   * Get application information including version.
   * @returns Observable of AppInfo
   */
  getAppInfo(): Observable<AppInfo> {
    return this.http.get<AppInfo>(this.apiUrl);
  }
}
