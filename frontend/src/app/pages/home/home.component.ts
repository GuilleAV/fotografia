import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FotoService } from '../../core/services/foto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { PhotoCardComponent } from '../../shared/components/photo-card/photo-card.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { Foto, Categoria } from '../../core/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, PhotoCardComponent, SkeletonComponent],
  template: `
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1>Descubrí el mundo a través de nuestros lentes</h1>
        <p>Fotógrafos profesionales capturando momentos únicos</p>
        <a routerLink="/galeria" class="btn-primary">Explorar Galería</a>
      </div>
    </section>

    <!-- Featured Photos -->
    <section class="section">
      <div class="container">
        <h2 class="section-title">Fotos Destacadas</h2>

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
              <p class="empty">No hay fotos disponibles aún.</p>
            }
          </div>
        }
      </div>
    </section>

    <!-- Categories -->
    <section class="section section-alt">
      <div class="container">
        <h2 class="section-title">Categorías</h2>
        <div class="categories-grid">
          @for (cat of categorias(); track cat.idCategoria) {
            <a [routerLink]="['/galeria']" [queryParams]="{ categoria: cat.idCategoria }" class="category-card">
              <span class="category-icon">{{ cat.icono || '📷' }}</span>
              <h3>{{ cat.nombre }}</h3>
              <p>{{ cat.descripcion }}</p>
            </a>
          }
        </div>
      </div>
    </section>
  `,
  styles: [`
    /* === MOBILE FIRST === */
    .hero {
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
      color: #fff;
      text-align: center;
      padding: 4rem 1.5rem;
    }
    .hero h1 {
      font-size: 1.8rem;
      margin-bottom: 0.75rem;
      font-weight: 700;
      line-height: 1.2;
    }
    .hero p {
      font-size: 1rem;
      color: #ccc;
      margin-bottom: 1.5rem;
    }
    .btn-primary {
      display: inline-block;
      background: #e94560;
      color: #fff;
      padding: 0.85rem 2rem;
      border-radius: 10px;
      text-decoration: none;
      font-weight: 700;
      font-size: 1rem;
      transition: background 0.2s;
    }
    .btn-primary:active { background: #d63851; }
    .section { padding: 2.5rem 1rem; }
    .section-alt { background: #f8f9fa; }
    .container { max-width: 1200px; margin: 0 auto; }
    .section-title {
      text-align: center;
      font-size: 1.4rem;
      color: #1a1a2e;
      margin-bottom: 1.5rem;
    }
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
    .categories-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 0.75rem;
    }
    .category-card {
      background: #fff;
      border-radius: 12px;
      padding: 1rem;
      text-align: center;
      text-decoration: none;
      color: #333;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    }
    .category-card:active {
      transform: scale(0.97);
    }
    .category-icon { font-size: 2rem; display: block; margin-bottom: 0.3rem; }
    .category-card h3 { margin: 0.3rem 0; color: #1a1a2e; font-size: 0.9rem; }
    .category-card p { margin: 0; color: #666; font-size: 0.75rem; line-height: 1.3; }

    /* === TABLET (min-width: 600px) === */
    @media (min-width: 600px) {
      .hero { padding: 5rem 2rem; }
      .hero h1 { font-size: 2.2rem; }
      .section { padding: 3rem 1.5rem; }
      .section-title { font-size: 1.6rem; }
      .photo-grid { grid-template-columns: repeat(2, 1fr); }
      .categories-grid { grid-template-columns: repeat(3, 1fr); gap: 1rem; }
      .category-card { padding: 1.25rem; }
      .category-icon { font-size: 2.2rem; }
      .category-card h3 { font-size: 0.95rem; }
      .category-card p { font-size: 0.8rem; }
      .category-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.12);
      }
      .category-card:active { transform: none; }
      .category-card { transition: transform 0.2s, box-shadow 0.2s; }
    }

    /* === ESCRITORIO (min-width: 900px) === */
    @media (min-width: 900px) {
      .hero { padding: 6rem 2rem; }
      .hero h1 { font-size: 2.5rem; }
      .hero p { font-size: 1.2rem; }
      .section { padding: 4rem 2rem; }
      .section-title { font-size: 1.8rem; margin-bottom: 2rem; }
      .photo-grid { grid-template-columns: repeat(3, 1fr); gap: 1.5rem; }
      .categories-grid { grid-template-columns: repeat(4, 1fr); }
    }
  `]
})
export class HomeComponent implements OnInit {
  private fotoService = inject(FotoService);
  private categoriaService = inject(CategoriaService);

  fotos = signal<Foto[]>([]);
  categorias = signal<Categoria[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.fotoService.listarPublicas().subscribe({
      next: (fotos) => this.fotos.set(fotos),
      error: (err) => this.error.set('Error al cargar las fotos'),
      complete: () => this.loading.set(false),
    });

    this.categoriaService.listarActivas().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {},
    });
  }
}
