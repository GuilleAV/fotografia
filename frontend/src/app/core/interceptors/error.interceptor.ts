import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Token inválido o expirado → limpiar y redirect al login
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
        router.navigate(['/login'], { queryParams: { returnUrl: router.url } });
      } else if (error.status === 403) {
        router.navigate(['/unauthorized']);
      } else if (error.status === 0) {
        // Network error — backend no disponible
        console.error('Backend no disponible. Verificá que esté corriendo.');
      } else {
        // Otros errores del servidor
        console.error(`HTTP Error ${error.status}:`, error.message);
      }

      return throwError(() => error);
    })
  );
};
