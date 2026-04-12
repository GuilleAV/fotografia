import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FotoService } from '../../core/services/foto.service';
import { Foto } from '../../core/models';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { FotoImagenUrlPipe } from '../../core/pipes/foto-imagen-url.pipe';

@Component({
  selector: 'app-foto-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, SkeletonComponent, FotoImagenUrlPipe],
  templateUrl: './foto-detail.component.html',
  styleUrls: ['./foto-detail.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FotoDetailComponent implements OnInit {
  private fotoService = inject(FotoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  foto = signal<Foto | null>(null);
  fotosCategoria = signal<Foto[]>([]);
  indiceActual = signal(0);

  loading = signal(true);
  error = signal<string | null>(null);
  imageVisible = signal(false);
  switching = signal(false);
  touchStartX = signal<number | null>(null);
  fromContext = signal<string | null>(null);

  private readonly swipeThreshold = 45;

  tituloCategoria = computed(() => {
    const foto = this.foto();
    return foto?.categoriaNombre || foto?.categoriaSlug || 'Colección';
  });

  puedeIrAnterior = computed(() => this.indiceActual() > 0);
  puedeIrSiguiente = computed(() => this.indiceActual() < this.fotosCategoria().length - 1);

  contador = computed(() => {
    const total = this.fotosCategoria().length;
    if (!total) {
      return null;
    }
    return `${this.indiceActual() + 1} / ${total}`;
  });

  backLink = computed(() => {
    if (this.fromContext() === 'dashboard') {
      return ['/dashboard'];
    }

    const slug = this.foto()?.categoriaSlug;
    return slug ? ['/', slug] : ['/'];
  });

  backLabel = computed(() => {
    if (this.fromContext() === 'dashboard') {
      return 'Volver al dashboard';
    }
    return this.tituloCategoria();
  });

  ngOnInit() {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      this.fromContext.set(params.get('from'));
    });

    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const id = Number(params.get('id'));
      if (!id) {
        this.error.set('ID de foto no válido');
        this.loading.set(false);
        return;
      }

      this.cargarFoto(id);
    });
  }

  irAnterior() {
    if (!this.puedeIrAnterior()) {
      return;
    }
    const anterior = this.fotosCategoria()[this.indiceActual() - 1];
    this.navegarA(anterior.idFoto);
  }

  irSiguiente() {
    if (!this.puedeIrSiguiente()) {
      return;
    }
    const siguiente = this.fotosCategoria()[this.indiceActual() + 1];
    this.navegarA(siguiente.idFoto);
  }

  abrirFoto(fotoId: number) {
    this.navegarA(fotoId);
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

  private cargarFoto(id: number) {
    this.loading.set(true);
    this.imageVisible.set(false);
    this.error.set(null);

    this.fotoService.obtenerPorId(id).subscribe({
      next: (foto) => {
        this.foto.set(foto);
        this.cargarColeccionCategoria(foto);
      },
      error: () => {
        this.error.set('No se pudo cargar la foto');
        this.fotosCategoria.set([]);
        this.switching.set(false);
        this.loading.set(false);
      },
    });
  }

  private cargarColeccionCategoria(foto: Foto) {
    const slug = foto.categoriaSlug;

    if (!slug) {
      this.fotosCategoria.set([foto]);
      this.indiceActual.set(0);
      this.switching.set(false);
      this.loading.set(false);
      this.marcarImagenVisible();
      return;
    }

    this.fotoService.listarPorCategoriaSlug(slug).subscribe({
      next: (fotos) => {
        const lista = fotos.length ? fotos : [foto];
        this.fotosCategoria.set(lista);

        const index = lista.findIndex((item) => item.idFoto === foto.idFoto);
        this.indiceActual.set(index >= 0 ? index : 0);
        this.switching.set(false);
        this.loading.set(false);
        this.marcarImagenVisible();
      },
      error: () => {
        this.fotosCategoria.set([foto]);
        this.indiceActual.set(0);
        this.switching.set(false);
        this.loading.set(false);
        this.marcarImagenVisible();
      },
    });
  }

  private navegarA(fotoId: number) {
    if (this.foto()?.idFoto === fotoId) {
      return;
    }

    this.switching.set(true);
    this.imageVisible.set(false);

    window.setTimeout(() => {
      this.router.navigate(['/foto', fotoId], {
        queryParams: this.fromContext() ? { from: this.fromContext() } : undefined,
      });
    }, 120);
  }

  private marcarImagenVisible() {
    window.requestAnimationFrame(() => {
      this.imageVisible.set(true);
    });
  }
}
