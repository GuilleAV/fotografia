import { ChangeDetectionStrategy, Component, DestroyRef, Renderer2, computed, inject, signal } from '@angular/core';
import { CommonModule, DOCUMENT } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FotoService } from '../../core/services/foto.service';
import { FotoImagenUrlPipe } from '../../core/pipes/foto-imagen-url.pipe';
import { Foto } from '../../core/models';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-categoria',
  standalone: true,
  imports: [CommonModule, RouterLink, SkeletonComponent, FotoImagenUrlPipe],
  templateUrl: './categoria.component.html',
  styleUrls: ['./categoria.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoriaComponent {
  private route = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);
  private fotoService = inject(FotoService);
  private renderer = inject(Renderer2);
  private document = inject(DOCUMENT);

  categoriaTitulo = signal('Categoría');
  fotos = signal<Foto[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  lightboxOpen = signal(false);
  indiceActual = signal(0);
  touchStartX = signal<number | null>(null);
  private readonly swipeThreshold = 45;

  fotoActual = computed(() => this.fotos()[this.indiceActual()] ?? null);
  puedeIrAnterior = computed(() => this.indiceActual() > 0);
  puedeIrSiguiente = computed(() => this.indiceActual() < this.fotos().length - 1);
  contador = computed(() => {
    const total = this.fotos().length;
    if (!total) {
      return null;
    }
    return `${this.indiceActual() + 1} / ${total}`;
  });

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const slug = params.get('slug');
      if (!slug) {
        this.error.set('Categoría no válida');
        this.loading.set(false);
        return;
      }
      this.cargarCategoria(slug);
    });

    this.destroyRef.onDestroy(() => {
      this.renderer.removeClass(this.document.body, 'fullscreen-photo');
      this.renderer.removeStyle(this.document.body, 'overflow');
    });
  }

  private cargarCategoria(slug: string) {
    this.loading.set(true);
    this.error.set(null);
    this.categoriaTitulo.set(this.formatearSlug(slug));

    this.fotoService.listarPorCategoriaSlug(slug).subscribe({
      next: (fotos) => {
        this.fotos.set(fotos);
        if (fotos.length > 0 && fotos[0].categoriaNombre) {
          this.categoriaTitulo.set(fotos[0].categoriaNombre);
        }
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la categoría solicitada');
        this.fotos.set([]);
        this.loading.set(false);
      },
    });
  }

  private formatearSlug(slug: string): string {
    return slug
      .replace(/-/g, ' ')
      .trim()
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }

  abrirLightbox(indice: number) {
    this.indiceActual.set(indice);
    this.lightboxOpen.set(true);
    this.renderer.addClass(this.document.body, 'fullscreen-photo');
    this.renderer.setStyle(this.document.body, 'overflow', 'hidden');
    window.setTimeout(() => {
      const overlay = this.document.querySelector('.lightbox') as HTMLElement | null;
      overlay?.focus();
    }, 0);
  }

  cerrarLightbox() {
    this.lightboxOpen.set(false);
    this.renderer.removeClass(this.document.body, 'fullscreen-photo');
    this.renderer.removeStyle(this.document.body, 'overflow');
  }

  irAnterior() {
    if (!this.puedeIrAnterior()) {
      return;
    }
    this.indiceActual.update((value) => value - 1);
  }

  irSiguiente() {
    if (!this.puedeIrSiguiente()) {
      return;
    }
    this.indiceActual.update((value) => value + 1);
  }

  onTouchStart(event: TouchEvent) {
    if (!event.touches.length) {
      return;
    }
    this.touchStartX.set(event.touches[0].clientX);
  }

  onTouchEnd(event: TouchEvent) {
    const startX = this.touchStartX();
    this.touchStartX.set(null);

    if (startX === null || !event.changedTouches.length) {
      return;
    }

    const endX = event.changedTouches[0].clientX;
    const delta = endX - startX;
    if (Math.abs(delta) < this.swipeThreshold) {
      return;
    }

    if (delta > 0) {
      this.irAnterior();
    } else {
      this.irSiguiente();
    }
  }
}
