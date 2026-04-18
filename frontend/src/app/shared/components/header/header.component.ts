import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { catchError, filter, map, of, startWith } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { PerfilPublicoService } from '../../../core/services/perfil-publico.service';
import { Categoria } from '../../../core/models';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  private router = inject(Router);
  private categoriaService = inject(CategoriaService);
  private perfilPublicoService = inject(PerfilPublicoService);

  auth = inject(AuthService);
  menuOpen = signal(false);

  currentUrl = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url)
    ),
    { initialValue: this.router.url }
  );

  categorias = toSignal(
    this.categoriaService.listarActivas().pipe(
      catchError(() => of([] as Categoria[]))
    ),
    { initialValue: [] as Categoria[] }
  );

  perfilPublico = toSignal(
    this.perfilPublicoService.obtenerPerfil().pipe(
      catchError(() => of(null))
    ),
    { initialValue: null }
  );

  isAdminContext = computed(() => {
    const url = this.currentUrl();
    return url.startsWith('/admin') || url.startsWith('/dashboard');
  });

  dashboardRoute = computed(() => (this.auth.isAdmin() ? '/admin' : '/dashboard'));

  categoriasOrdenadas = computed(() =>
    [...this.categorias()].sort((a, b) => (a.orden ?? 0) - (b.orden ?? 0))
  );

  nombreMarca = computed(() => 'SENTIR FOTOGRAFICO');

  toggleMenu() {
    this.menuOpen.update((isOpen) => !isOpen);
  }

  closeMenu() {
    this.menuOpen.set(false);
  }

  logout() {
    this.closeMenu();
    this.auth.logout();
  }
}
