import { Injectable } from '@angular/core';
import { User } from '../app/entities/user'
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from './environments/environment';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiServerUrl}/api/users`);
  }

  public getUser(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiServerUrl}/api/users/${userId}`);
  }

  public updateUser(userId: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiServerUrl}/api/users/${userId}`, user);
  }

  public deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/api/users/${userId}`);
  }

  private getToken(): string | null {
    return localStorage.getItem('jwt-token')
  }

  public getLoggedInUserId() : number | null{
    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded: any = jwtDecode(token);
      return decoded.id || decoded.userId || null;
    } catch (err) {
      return null;
    }
  }

}
