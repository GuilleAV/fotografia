import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <section class="reset-section">
      <div class="reset-card">
        <div class="reset-header">
          <h1>🔒 Nueva Contraseña</h1>
          <p>Ingresá tu nueva contraseña para acceder a tu cuenta.</p>
        </div>

        @if (exito()) {
          <div class="success-box">
            <div class="success-icon">✅</div>
            <h3>¡Contraseña actualizada!</h3>
            <p>Tu contraseña fue cambiada correctamente.</p>
            <a routerLink="/login" class="btn-back">Iniciar Sesión</a>
          </div>
        } @else {
          <form (ngSubmit)="onSubmit()" #form="ngForm">
            @if (error()) {
              <div class="alert-error">{{ error() }}</div>
            }

            <div class="form-group">
              <label for="password">Nueva Contraseña</label>
              <input
                id="password"
                type="password"
                [(ngModel)]="password"
                name="password"
                required
                minlength="6"
                placeholder="Mínimo 6 caracteres"
                autocomplete="new-password"
              />
            </div>

            <div class="form-group">
              <label for="confirm">Confirmar Contraseña</label>
              <input
                id="confirm"
                type="password"
                [(ngModel)]="confirm"
                name="confirm"
                required
                placeholder="Repetí la contraseña"
                autocomplete="new-password"
              />
            </div>

            <button type="submit" class="btn-submit" [disabled]="loading()">
              @if (loading()) {
                <span class="spinner"></span> Guardando...
              } @else {
                Guardar Contraseña
              }
            </button>
          </form>
        }
      </div>
    </section>
  `,
  styles: [`
    .reset-section {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 1.5rem;
      background: #f8f9fa;
    }
    .reset-card {
      background: #fff;
      border-radius: 16px;
      padding: 2rem 1.5rem;
      width: 100%;
      max-width: 400px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.08);
    }
    .reset-header {
      text-align: center;
      margin-bottom: 2rem;
    }
    .reset-header h1 {
      margin: 0 0 0.5rem;
      color: #1a1a2e;
      font-size: 1.5rem;
    }
    .reset-header p {
      margin: 0;
      color: #666;
      font-size: 0.9rem;
      line-height: 1.5;
    }
    .form-group {
      margin-bottom: 1.2rem;
    }
    .form-group label {
      display: block;
      margin-bottom: 0.4rem;
      font-weight: 600;
      color: #333;
      font-size: 0.9rem;
    }
    .form-group input {
      width: 100%;
      padding: 0.85rem 1rem;
      border: 2px solid #e0e0e0;
      border-radius: 10px;
      font-size: 1rem;
      transition: border-color 0.2s, box-shadow 0.2s;
      box-sizing: border-box;
      background: #fafafa;
    }
    .form-group input:focus {
      outline: none;
      border-color: #e94560;
      box-shadow: 0 0 0 3px rgba(233, 69, 96, 0.1);
      background: #fff;
    }
    .btn-submit {
      width: 100%;
      padding: 0.9rem;
      background: #e94560;
      color: #fff;
      border: none;
      border-radius: 10px;
      font-size: 1.05rem;
      font-weight: 700;
      cursor: pointer;
      transition: background 0.2s, transform 0.1s;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
    }
    .btn-submit:hover:not(:disabled) { background: #d63851; }
    .btn-submit:active:not(:disabled) { transform: scale(0.98); }
    .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert-error {
      background: #fde8e8;
      color: #c0392b;
      padding: 0.8rem 1rem;
      border-radius: 8px;
      margin-bottom: 1rem;
      font-size: 0.9rem;
      border-left: 4px solid #e74c3c;
    }
    .success-box {
      text-align: center;
      padding: 1rem 0;
    }
    .success-icon { font-size: 3rem; margin-bottom: 1rem; }
    .success-box h3 { color: #27ae60; margin: 0 0 0.5rem; }
    .success-box p { color: #666; font-size: 0.9rem; margin-bottom: 1.5rem; }
    .btn-back {
      display: inline-block;
      background: #1a1a2e;
      color: #fff;
      padding: 0.7rem 1.5rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
    }
    .spinner {
      width: 18px;
      height: 18px;
      border: 2px solid rgba(255,255,255,0.3);
      border-top-color: #fff;
      border-radius: 50%;
      animation: spin 0.6s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }

    @media (min-width: 600px) {
      .reset-card { padding: 2.5rem; }
      .reset-header h1 { font-size: 1.8rem; }
    }
  `]
})
export class ResetPasswordComponent {
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  password = '';
  confirm = '';
  loading = signal(false);
  error = signal<string | null>(null);
  exito = signal(false);

  onSubmit() {
    if (this.password.length < 6) {
      this.error.set('La contraseña debe tener al menos 6 caracteres');
      return;
    }
    if (this.password !== this.confirm) {
      this.error.set('Las contraseñas no coinciden');
      return;
    }

    const token = this.route.snapshot.queryParams['token'];
    if (!token) {
      this.error.set('Token de recuperación no válido. Volvé a solicitar el link.');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.authService.resetPassword(token, this.password).subscribe({
      next: () => {
        this.exito.set(true);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.message || 'Error al restablecer la contraseña');
        this.loading.set(false);
      },
    });
  }
}
