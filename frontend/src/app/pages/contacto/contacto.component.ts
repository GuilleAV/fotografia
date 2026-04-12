import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { PerfilPublicoService } from '../../core/services/perfil-publico.service';

@Component({
  selector: 'app-contacto',
  standalone: true,
  templateUrl: './contacto.component.html',
  styleUrls: ['./contacto.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactoComponent {
  private perfilPublicoService = inject(PerfilPublicoService);

  perfilPublico = toSignal(
    this.perfilPublicoService.obtenerPerfil().pipe(
      catchError(() => of(null))
    ),
    { initialValue: null }
  );

  emailContacto = computed(() => this.perfilPublico()?.emailContacto || 'info@sentirfotografico.com');
}
