import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { PerfilPublicoService } from '../../core/services/perfil-publico.service';

@Component({
  selector: 'app-about',
  standalone: true,
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AboutComponent {
  private perfilPublicoService = inject(PerfilPublicoService);

  perfilPublico = toSignal(
    this.perfilPublicoService.obtenerPerfil().pipe(
      catchError(() => of(null))
    ),
    { initialValue: null }
  );

  nombreMarca = computed(() => 'Sentir Fotográfico');
  nombreCompleto = computed(() => this.perfilPublico()?.nombreCompleto || 'Equipo Sentir Fotográfico');
}
