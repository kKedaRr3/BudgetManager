import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})

export class RegisterComponent {
  username = '';
  firstName = '';
  lastName = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    this.auth.register(this.username, this.firstName, this.lastName, this.password).subscribe(
      {
        next: () => {
          this.router.navigate(['/login'])
        },
        error: () => {
          this.error = 'Something went wrong';
        }
      }
    )
  }
}