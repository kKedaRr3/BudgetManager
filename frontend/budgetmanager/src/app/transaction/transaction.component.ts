import { Component, Input, OnInit } from '@angular/core';
import { Transaction } from '../entities/transaction';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { TransactionService } from '../transaction.service';

@Component({
  selector: 'app-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.css']
})
export class TransactionComponent implements OnInit{
  @Input() categoryId!: number;
  transactions: Transaction[] = []

  newTransaction = {
    description: '',
    amount: 0
  };

  editedIndex: number | null = null;
  editTransactionData = { description: '', amount: 0 };

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.getTransactions()
  }

  public getTransactions(): void{
    this.transactionService.getTransactions(this.categoryId).subscribe(
      (response: Transaction[]) => {
        this.transactions = response;
      }
    ),
    (error: HttpErrorResponse) => {
      alert(error.message)
    }
  }

  addTransaction() {
    if (!this.newTransaction.description.trim() || this.newTransaction.amount === 0) return;

    const transaction = {
      description: this.newTransaction.description,
      amount: this.newTransaction.amount
    } as Transaction;

    this.transactionService.addTransaction(this.categoryId, transaction).subscribe({
      next: (created) => {
        this.transactions.push(created);
        this.newTransaction = { description: '', amount: 0 };
      },
      error: (err) => console.error('Error while adding transaction:', err)
    });
  }

  
  deleteTransaction(index: number) {
    const transaction = this.transactions[index];
    if (!confirm('Are you sure you want to delete this transaction?')) return;
  
    this.transactionService.deleteTransaction(this.categoryId, transaction.id).subscribe({
      next: () => this.transactions.splice(index, 1),
      error: (err) => console.error('Error while deleting transaction:', err)
    });
  }

  startEdit(index: number) {
    const transaction = this.transactions[index];
    this.editedIndex = index;
    this.editTransactionData = {
      description: transaction.description,
      amount: transaction.amount
    };
  }
  
  cancelEdit() {
    this.editedIndex = null;
    this.editTransactionData = { description: '', amount: 0 };
  }
  
  saveEdit(index: number) {
    const updated = {
      ...this.transactions[index],
      description: this.editTransactionData.description,
      amount: this.editTransactionData.amount
    };
  
    this.transactionService.updateTransaction(this.categoryId, this.transactions[index].id, updated).subscribe({
      next: (result) => {
        this.transactions[index] = result;
        this.cancelEdit();
      },
      error: (err) => console.error('Error while editing password:', err)
    });
  }

}
