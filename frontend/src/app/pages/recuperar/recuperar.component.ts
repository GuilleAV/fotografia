import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-recuperar',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './recuperar.component.html',
  styleUrls: ['./recuperar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecuperarComponent {
  private authService = inject(AuthService);

  email = '';
  loading = signal(false);
  error = signal<string | null>(null);
  enviado = signal(false);

  onSubmit() {
    if (!this.email || !this.email.includes('@')) {
      this.error.set('Ingresá un email válido');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.authService.solicitarRecuperacion(this.email).subscribe({
      next: () => {
        this.enviado.set(true);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.message || 'Error al enviar el email');
        this.loading.set(false);
      },
    });
  }
}
