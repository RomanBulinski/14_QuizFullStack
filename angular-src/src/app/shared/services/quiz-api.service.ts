import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Question } from '../models/question.model';

@Injectable({
  providedIn: 'root'
})
export class QuizApiService {
  private apiUrl = '/api/questions';

  constructor(private http: HttpClient) { }

  getQuestions(technology: string, count: number): Observable<Question[]> {
    return this.http.get<Question[]>(`${this.apiUrl}/${technology}/${count}`);
  }
}
