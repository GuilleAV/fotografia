import { Component, OnInit, inject, signal, DestroyRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FotoService } from '../../core/services/foto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { PhotoCardComponent } from '../../shared/components/photo-card/photo-card.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { Foto, Categoria } from '../../core/models';

@Component({
  selector: 'app-galeria',
  standalone: true,
  imports: [CommonModule, FormsModule, PhotoCardComponent, SkeletonComponent],
  templateUrl: './galeria.component.html',
  styleUrls: ['./galeria.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GaleriaComponent implements OnInit {
  private fotoService = inject(FotoService);
  private categoriaService = inject(CategoriaService);
  private route = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  fotos = signal<Foto[]>([]);
  categorias = signal<Categoria[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  categoriaSeleccionada: number | null = null;

  ngOnInit() {
    // Check query param for category
    this.route.queryParams.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(params => {
      if (params['categoria']) {
        this.categoriaSeleccionada = +params['categoria'];
        this.cargarFotos();
      }
    });

    this.categoriaService.listarActivas().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {},
    });

    this.cargarFotos();
  }

  filtrar() {
    this.cargarFotos();
  }

  private cargarFotos() {
    this.loading.set(true);
    if (this.categoriaSeleccionada) {
      this.fotoService.listarPorCategoria(this.categoriaSeleccionada).subscribe({
        next: (fotos) => {
          this.fotos.set(fotos);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Error al cargar las fotos');
          this.loading.set(false);
        },
      });
    } else {
      this.fotoService.listarPublicas().subscribe({
        next: (fotos) => {
          this.fotos.set(fotos);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Error al cargar las fotos');
          this.loading.set(false);
        },
      });
    }
  }
}
