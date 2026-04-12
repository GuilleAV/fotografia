import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, Renderer2, computed, inject, signal } from '@angular/core';
import { CommonModule, DOCUMENT } from '@angular/common';
import { FotoService } from '../../core/services/foto.service';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { FotoImagenUrlPipe } from '../../core/pipes/foto-imagen-url.pipe';
import { Foto } from '../../core/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, SkeletonComponent, FotoImagenUrlPipe],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent implements OnInit {
  private fotoService = inject(FotoService);
  private renderer = inject(Renderer2);
  private document = inject(DOCUMENT);
  private destroyRef = inject(DestroyRef);

  fotos = signal<Foto[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  lightboxOpen = signal(false);
  indiceActual = signal(0);
  touchStartX = signal<number | null>(null);
  private readonly swipeThreshold = 45;

  heroFoto = computed(() => this.fotos()[0] ?? null);
  fotosMosaico = computed(() => this.fotos().slice(1));
  fotoActual = computed(() => this.fotos()[this.indiceActual()] ?? null);
  puedeIrAnterior = computed(() => this.indiceActual() > 0);
  puedeIrSiguiente = computed(() => this.indiceActual() < this.fotos().length - 1);
  contador = computed(() => {
    const total = this.fotos().length;
    return total ? `${this.indiceActual() + 1} / ${total}` : null;
  });

  ngOnInit() {
    this.fotoService.listarPublicas().subscribe({
      next: (fotos) => {
        this.fotos.set(fotos);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las fotos públicas');
        this.loading.set(false);
      },
    });

    this.destroyRef.onDestroy(() => {
      this.renderer.removeClass(this.document.body, 'fullscreen-photo');
      this.renderer.removeStyle(this.document.body, 'overflow');
    });
  }

  abrirLightbox(indice: number) {
    this.indiceActual.set(indice);
    this.lightboxOpen.set(true);
    this.renderer.addClass(this.document.body, 'fullscreen-photo');
    this.renderer.setStyle(this.document.body, 'overflow', 'hidden');
    window.setTimeout(() => {
      const overlay = this.document.querySelector('.home-lightbox') as HTMLElement | null;
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
