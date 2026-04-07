import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
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
