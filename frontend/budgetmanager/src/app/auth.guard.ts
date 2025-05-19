import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const token = localStorage.getItem('jwt-token');

  if (token) {
    return true;
  }

  // Jeśli chcesz mieć przekierowanie, musisz użyć inject()
  const router = inject(Router);
  router.navigate(['/login']);
  return false;
};
