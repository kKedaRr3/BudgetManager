import { Injectable } from '@angular/core';
import { Category } from '../app/entities/category'
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from './environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  public getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiServerUrl}/api/categories`);
  }

  public getCategory(categoryId: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiServerUrl}/api/categories/${categoryId}`);
  }

  public addCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(`${this.apiServerUrl}/api/categories`, category);
  }

  public updateCategory(categoryId: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.apiServerUrl}/api/categories/${categoryId}`, category);
  }

  public deleteCategory(categoryId: number): Observable<Category> {
    return this.http.delete<Category>(`${this.apiServerUrl}/api/categories/${categoryId}`);
  }

}

