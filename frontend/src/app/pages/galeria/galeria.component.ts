import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FotoService } from '../../core/services/foto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { PhotoCardComponent } from '../../shared/components/photo-card/photo-card.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { Foto, Categoria } from '../../core/models';

@Component({
  selector: 'app-galeria',
  standalone: true,
  imports: [CommonModule, FormsModule, PhotoCardComponent, SkeletonComponent],
  template: `
    <section class="galeria-section">
      <div class="container">
        <h1 class="page-title">Galería</h1>

        <!-- Filtros -->
        <div class="filters">
          <select [(ngModel)]="categoriaSeleccionada" (ngModelChange)="filtrar()">
            <option [ngValue]="null">Todas las categorías</option>
            @for (cat of categorias(); track cat.idCategoria) {
              <option [ngValue]="cat.idCategoria">{{ cat.nombre }}</option>
            }
          </select>
        </div>

        <!-- Grid -->
        @if (loading()) {
          <div class="photo-grid">
            @for (_ of [1,2,3,4,5,6]; track _) {
              <div class="card-skeleton">
                <app-skeleton height="210px" />
                <div style="padding: 1rem;">
                  <app-skeleton width="70%" height="1rem" />
                  <app-skeleton width="40%" height="0.8rem" style="margin-top: 0.5rem;" />
                </div>
              </div>
            }
          </div>
        } @else if (error()) {
          <div class="error">{{ error() }}</div>
        } @else {
          <div class="photo-grid">
            @for (foto of fotos(); track foto.idFoto) {
              <app-photo-card [foto]="foto" />
            } @empty {
              <p class="empty">No hay fotos en esta categoría.</p>
            }
          </div>
        }
      </div>
    </section>
  `,
  styles: [`
    /* === MOBILE FIRST === */
    .galeria-section { padding: 1.5rem 1rem; min-height: 60vh; }
    .container { max-width: 1200px; margin: 0 auto; }
    .page-title {
      text-align: center;
      font-size: 1.6rem;
      color: #1a1a2e;
      margin-bottom: 1.5rem;
    }
    .filters {
      display: flex;
      justify-content: center;
      margin-bottom: 1.5rem;
    }
    select {
      width: 100%;
      max-width: 300px;
      padding: 0.85rem 1rem;
      border: 2px solid #e0e0e0;
      border-radius: 10px;
      font-size: 1rem;
      background: #fff;
      cursor: pointer;
    }
    select:focus { outline: none; border-color: #e94560; }
    .photo-grid {
      display: grid;
      grid-template-columns: 1fr;
      gap: 1rem;
    }
    .card-skeleton {
      background: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    .error, .empty {
      text-align: center;
      padding: 2rem;
      color: #666;
      font-size: 1rem;
    }
    .error { color: #e74c3c; }

    /* === TABLET (min-width: 600px) === */
    @media (min-width: 600px) {
      .galeria-section { padding: 2rem 1.5rem; }
      .page-title { font-size: 1.8rem; margin-bottom: 2rem; }
      .photo-grid { grid-template-columns: repeat(2, 1fr); gap: 1.25rem; }
    }

    /* === ESCRITORIO (min-width: 900px) === */
    @media (min-width: 900px) {
      .galeria-section { padding: 2rem; }
      .page-title { font-size: 2rem; }
      .photo-grid { grid-template-columns: repeat(3, 1fr); gap: 1.5rem; }
    }
  `]
})
export class GaleriaComponent implements OnInit {
  private fotoService = inject(FotoService);
  private categoriaService = inject(CategoriaService);
  private route = inject(ActivatedRoute);

  fotos = signal<Foto[]>([]);
  categorias = signal<Categoria[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  categoriaSeleccionada: number | null = null;

  ngOnInit() {
    // Check query param for category
    this.route.queryParams.subscribe(params => {
      if (params['categoria']) {
        this.categoriaSeleccionada = +params['categoria'];
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
