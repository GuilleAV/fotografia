import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="unauthorized">
      <div class="content">
        <h1>🚫 Acceso Denegado</h1>
        <p>No tenés permisos para acceder a esta sección.</p>
        <a routerLink="/" class="btn-back">Volver al Inicio</a>
      </div>
    </section>
  `,
  styles: [`
    .unauthorized {
      min-height: 60vh;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .content { text-align: center; }
    .content h1 { font-size: 2rem; color: #1a1a2e; margin-bottom: 1rem; }
    .content p { color: #666; margin-bottom: 2rem; }
    .btn-back {
      display: inline-block;
      background: #e94560;
      color: #fff;
      padding: 0.7rem 2rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
    }
  `]
})
export class UnauthorizedComponent {}
