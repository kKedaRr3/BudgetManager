import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { Router } from '@angular/router';
import { User } from '../entities/user';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {
  user = {
    email: '',
    firstName: '',
    lastName: '',
    password: ''
  } as User;

  showConfirmDelete = false;
  confirmPassword: string = '';
  userId: number | null = -1;

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit() {
    this.userId = this.userService.getLoggedInUserId();
    if(this.userId) {
      this.userService.getUser(this.userId).subscribe(userData => {
        this.user.email = userData.email;
        this.user.firstName = userData.firstName;
        this.user.lastName = userData.lastName;
      });
    }
  }

  get passwordsDoNotMatch(): boolean {
    return this.user.password !== this.confirmPassword;
  }

  updateAccount() {
    if (!this.userId) return;
  
    const updatedData: Partial<User> = {
      firstName: this.user.firstName,
      lastName: this.user.lastName
    };
  
    if (this.user.password) {
      if (this.passwordsDoNotMatch) {
        alert('Passwords do not match!');
        return;
      }
      updatedData.password = this.user.password;
    }
  
    this.userService.updateUser(this.userId, updatedData).subscribe(() => {
      alert('The data has been updated.');
      this.confirmPassword = '';
      this.user.password = '';
    });
  }

  confirmDelete() {
    this.showConfirmDelete = true;
  }

  deleteAccount() {
    if (this.userId){
      this.userService.deleteUser(this.userId).subscribe(() => {
        localStorage.removeItem('jwt-token');
        this.router.navigate(['/login']);
      });
    }
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}
