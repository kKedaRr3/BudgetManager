import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'jwt-token';

  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<{ token: string }>('http://localhost:8080/api/auth/signin', {
      email, password
    });
  }

  register(email: string, firstName:string, lastName:string, password:string){
    return this.http.post<any>('http://localhost:8080/api/auth/signup', {
      firstName, lastName, email, password 
    })
  }

  saveToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
