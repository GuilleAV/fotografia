import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, Usuario } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private userKey = 'auth_user';

  // Signals para estado reactivo
  private _token = signal<string | null>(this.getTokenFromStorage());
  private _user = signal<Usuario | null>(this.getUserFromStorage());

  // Computed signals
  isLoggedIn = computed(() => !!this._token());
  user = computed(() => this._user());
  userRole = computed(() => this._user()?.rol ?? null);
  isAdmin = computed(() => {
    const rol = this.userRole();
    return rol === 'ADMIN' || rol === 'SUPER_ADMIN';
  });
  isFotografo = computed(() => this.userRole() === 'FOTOGRAFO');

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest) {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        this._token.set(response.token);
        this._user.set(response.usuario);
        localStorage.setItem(this.tokenKey, response.token);
        localStorage.setItem(this.userKey, JSON.stringify(response.usuario));
      }),
      catchError((error) => {
        const message = error.error?.error || 'Error al iniciar sesión';
        return throwError(() => new Error(message));
      })
    );
  }

  /**
   * Solicita recuperación de contraseña por email.
   */
  solicitarRecuperacion(email: string) {
    return this.http.post<{ mensaje: string }>(`${this.apiUrl}/recuperar`, { email }).pipe(
      catchError((error) => {
        const message = error.error?.error || 'Error al solicitar recuperación';
        return throwError(() => new Error(message));
      })
    );
  }

  /**
   * Resetea la contraseña usando el token recibido por email.
   */
  resetPassword(token: string, password: string) {
    return this.http.post<{ mensaje: string }>(`${this.apiUrl}/reset`, { token, password }).pipe(
      catchError((error) => {
        const message = error.error?.error || 'Error al restablecer la contraseña';
        return throwError(() => new Error(message));
      })
    );
  }

  logout() {
    const token = this._token();
    if (token) {
      this.http.post(`${this.apiUrl}/logout`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      }).subscribe({
        error: () => {} // Ignore errors on logout
      });
    }
    this._token.set(null);
    this._user.set(null);
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this._token();
  }

  private getTokenFromStorage(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private getUserFromStorage(): Usuario | null {
    const data = localStorage.getItem(this.userKey);
    return data ? JSON.parse(data) : null;
  }
}
