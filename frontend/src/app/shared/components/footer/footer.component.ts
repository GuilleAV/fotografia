import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { catchError, filter, map, of, startWith } from 'rxjs';
import { PerfilPublicoService } from '../../../core/services/perfil-publico.service';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FooterComponent {
  private router = inject(Router);
  private perfilPublicoService = inject(PerfilPublicoService);

  currentYear = new Date().getFullYear();

  perfilPublico = toSignal(
    this.perfilPublicoService.obtenerPerfil().pipe(
      catchError(() => of(null))
    ),
    { initialValue: null }
  );

  currentUrl = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url)
    ),
    { initialValue: this.router.url }
  );

  isAdminContext = computed(() => {
    const url = this.currentUrl();
    return url.startsWith('/admin') || url.startsWith('/dashboard');
  });

  nombreMarca = computed(() => 'Sentir Fotográfico');
}
