import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FotoService } from '../../core/services/foto.service';
import { AuthService } from '../../core/services/auth.service';
import { Foto } from '../../core/models';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-foto-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, SkeletonComponent],
  template: `
    @if (loading()) {
      <section class="detail-section">
        <div class="container">
          <app-skeleton height="60vh" />
          <div style="margin-top: 1.5rem;">
            <app-skeleton width="300px" height="2rem" />
            <app-skeleton width="200px" height="1rem" style="margin-top: 0.5rem;" />
          </div>
        </div>
      </section>
    } @else if (error()) {
      <section class="detail-section">
        <div class="container error-state">
          <h2>Error al cargar la foto</h2>
          <p>{{ error() }}</p>
          <a routerLink="/galeria" class="btn-back">← Volver a la galería</a>
        </div>
      </section>
    } @else if (foto()) {
      <section class="detail-section">
        <div class="container">
          <!-- Breadcrumb -->
          <nav class="breadcrumb">
            <a routerLink="/">Inicio</a>
            <span>/</span>
            <a routerLink="/galeria">Galería</a>
            <span>/</span>
            <span class="current">{{ foto()!.titulo }}</span>
          </nav>

          <!-- Main Image -->
          <div class="image-container" (click)="openLightbox()">
            <img
              [src]="foto()!.rutaWeb || foto()!.urlCompleta || 'assets/placeholder.jpg'"
              [alt]="foto()!.titulo"
              class="main-image"
            />
            <div class="zoom-hint">
              <i class="fas fa-expand"></i> Click para ver en tamaño completo
            </div>
          </div>

          <!-- Info -->
          <div class="info-grid">
            <div class="info-main">
              <h1>{{ foto()!.titulo }}</h1>
              @if (foto()!.descripcion) {
                <p class="description">{{ foto()!.descripcion }}</p>
              }
              <div class="meta-row">
                @if (foto()!.usuarioNombre) {
                  <span class="meta-item">📷 {{ foto()!.usuarioNombre }}</span>
                }
                @if (foto()!.categoriaNombre) {
                  <span class="meta-item">📂 {{ foto()!.categoriaNombre }}</span>
                }
                <span class="meta-item">👁 {{ foto()!.visitas }} visitas</span>
                <span class="meta-item">📅 {{ foto()!.fechaSubida | date:'longDate' }}</span>
              </div>
              @if (foto()!.etiquetas && foto()!.etiquetas!.length > 0) {
                <div class="tags">
                  @for (tag of foto()!.etiquetas; track tag) {
                    <span class="tag">{{ tag }}</span>
                  }
                </div>
              }
            </div>

            <div class="info-sidebar">
              <h3>Det Técnicos</h3>
              <table class="tech-table">
                @if (foto()!.anchoPx && foto()!.altoPx) {
                  <tr>
                    <td>Dimensiones</td>
                    <td>{{ foto()!.anchoPx }} × {{ foto()!.altoPx }} px</td>
                  </tr>
                }
                @if (foto()!.tamanioKb) {
                  <tr>
                    <td>Tamaño</td>
                    <td>{{ foto()!.tamanioKb }} KB</td>
                  </tr>
                }
                <tr>
                  <td>Estado</td>
                  <td>
                    <span class="status-badge" [class]="'status-' + foto()!.estado.toLowerCase()">
                      {{ foto()!.estado }}
                    </span>
                  </td>
                </tr>
              </table>

              @if (auth.isAdmin()) {
                <div class="admin-actions">
                  <h3>Acciones de Admin</h3>
                  @if (foto()!.estado !== 'APROBADA') {
                    <button class="btn-approve" (click)="aprobar()">✓ Aprobar</button>
                  }
                  @if (foto()!.estado !== 'RECHAZADA') {
                    <button class="btn-reject" (click)="rechazar()">✗ Rechazar</button>
                  }
                </div>
              }
            </div>
          </div>
        </div>
      </section>

      <!-- Lightbox Overlay -->
      @if (lightboxOpen()) {
        <div class="lightbox" (click)="closeLightbox()">
          <button class="lightbox-close" (click)="closeLightbox()">✕</button>
          <img
            [src]="foto()!.rutaWeb || foto()!.urlCompleta || 'assets/placeholder.jpg'"
            [alt]="foto()!.titulo"
            (click)="$event.stopPropagation()"
          />
        </div>
      }
    }
  `,
  styles: [`
    .detail-section { padding: 2rem; min-height: 60vh; }
    .container { max-width: 1200px; margin: 0 auto; }
    .breadcrumb {
      display: flex;
      gap: 0.5rem;
      align-items: center;
      font-size: 0.85rem;
      color: #999;
      margin-bottom: 1.5rem;
    }
    .breadcrumb a { color: #666; transition: color 0.2s; }
    .breadcrumb a:hover { color: #e94560; }
    .breadcrumb .current { color: #333; }
    .image-container {
      position: relative;
      border-radius: 12px;
      overflow: hidden;
      cursor: pointer;
      background: #000;
      margin-bottom: 2rem;
    }
    .main-image {
      width: 100%;
      max-height: 80vh;
      object-fit: contain;
      display: block;
    }
    .zoom-hint {
      position: absolute;
      bottom: 1rem;
      right: 1rem;
      background: rgba(0,0,0,0.7);
      color: #fff;
      padding: 0.5rem 1rem;
      border-radius: 6px;
      font-size: 0.85rem;
      opacity: 0;
      transition: opacity 0.2s;
    }
    .image-container:hover .zoom-hint { opacity: 1; }
    .info-grid {
      display: grid;
      grid-template-columns: 1fr 300px;
      gap: 2rem;
    }
    .info-main h1 {
      font-size: 1.8rem;
      color: #1a1a2e;
      margin-bottom: 0.5rem;
    }
    .description {
      color: #555;
      line-height: 1.7;
      margin-bottom: 1rem;
    }
    .meta-row {
      display: flex;
      flex-wrap: wrap;
      gap: 1.5rem;
      color: #666;
      font-size: 0.9rem;
      margin-bottom: 1rem;
    }
    .tags { display: flex; flex-wrap: wrap; gap: 0.5rem; }
    .tag {
      background: #f0f0f0;
      color: #555;
      padding: 3px 10px;
      border-radius: 20px;
      font-size: 0.8rem;
    }
    .info-sidebar {
      background: #fff;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
      height: fit-content;
    }
    .info-sidebar h3 {
      margin: 0 0 1rem;
      font-size: 1rem;
      color: #1a1a2e;
    }
    .tech-table {
      width: 100%;
      border-collapse: collapse;
      font-size: 0.9rem;
    }
    .tech-table td {
      padding: 0.5rem 0;
      border-bottom: 1px solid #f0f0f0;
    }
    .tech-table td:first-child { color: #999; }
    .tech-table td:last-child { font-weight: 500; text-align: right; }
    .status-badge {
      padding: 3px 8px;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 600;
    }
    .status-pendiente { background: #fff3cd; color: #856404; }
    .status-aprobada { background: #d4edda; color: #155724; }
    .status-rechazada { background: #f8d7da; color: #721c24; }
    .admin-actions { margin-top: 1.5rem; }
    .admin-actions h3 { margin-bottom: 0.8rem; }
    .btn-approve, .btn-reject {
      width: 100%;
      padding: 0.6rem;
      border: none;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
      margin-bottom: 0.5rem;
      transition: background 0.2s;
    }
    .btn-approve { background: #27ae60; color: #fff; }
    .btn-approve:hover { background: #219a52; }
    .btn-reject { background: #e74c3c; color: #fff; }
    .btn-reject:hover { background: #c0392b; }
    .error-state { text-align: center; padding: 4rem 2rem; }
    .error-state h2 { color: #e74c3c; margin-bottom: 0.5rem; }
    .error-state p { color: #666; margin-bottom: 1.5rem; }
    .btn-back {
      display: inline-block;
      background: #e94560;
      color: #fff;
      padding: 0.6rem 1.5rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
    }
    /* Lightbox */
    .lightbox {
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,0.95);
      z-index: 9999;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: zoom-out;
    }
    .lightbox img {
      max-width: 95vw;
      max-height: 95vh;
      object-fit: contain;
    }
    .lightbox-close {
      position: absolute;
      top: 1rem;
      right: 1.5rem;
      background: rgba(255,255,255,0.2);
      border: none;
      color: #fff;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      font-size: 1.2rem;
      cursor: pointer;
      transition: background 0.2s;
    }
    .lightbox-close:hover { background: rgba(255,255,255,0.4); }
    @media (max-width: 768px) {
      .info-grid { grid-template-columns: 1fr; }
      .meta-row { gap: 0.8rem; }
    }
  `]
})
export class FotoDetailComponent implements OnInit {
  private fotoService = inject(FotoService);
  private route = inject(ActivatedRoute);
  auth = inject(AuthService);

  foto = signal<Foto | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  lightboxOpen = signal(false);

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error.set('ID de foto no válido');
      this.loading.set(false);
      return;
    }

    this.fotoService.obtenerPorId(id).subscribe({
      next: (foto) => {
        this.foto.set(foto);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la foto');
        this.loading.set(false);
      },
    });
  }

  openLightbox() {
    this.lightboxOpen.set(true);
    document.body.style.overflow = 'hidden';
  }

  closeLightbox() {
    this.lightboxOpen.set(false);
    document.body.style.overflow = '';
  }

  aprobar() {
    const foto = this.foto();
    if (!foto) return;
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'APROBADA' }).subscribe({
      next: (updated) => this.foto.set(updated),
      error: () => {},
    });
  }

  rechazar() {
    const foto = this.foto();
    if (!foto) return;
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'RECHAZADA' }).subscribe({
      next: (updated) => this.foto.set(updated),
      error: () => {},
    });
  }
}
