import { Injectable } from '@angular/core';
import { Transaction } from '../app/entities/transaction'
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from './environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  public getTransactions(categoryId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiServerUrl}/api/transactions/${categoryId}`);
  }

  public getTransaction(categoryId: number, transactionId: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiServerUrl}/api/transactions/${categoryId}/${transactionId}`);
  }

  public addTransaction(categoryId: number, transaction: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.apiServerUrl}/api/transactions/${categoryId}`, transaction);
  }

  public updateTransaction(categoryId: number, transactionId: number, transaction: Transaction): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.apiServerUrl}/api/transactions/${categoryId}/${transactionId}`, transaction);
  }

  public deleteTransaction(categoryId: number, transactionId: number): Observable<Transaction> {
    return this.http.delete<Transaction>(`${this.apiServerUrl}/api/transactions/${categoryId}/${transactionId}`);
  }

}
