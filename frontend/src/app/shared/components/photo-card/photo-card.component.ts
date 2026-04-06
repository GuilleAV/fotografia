import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Foto } from '../../../core/models';

@Component({
  selector: 'app-photo-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="photo-card" [routerLink]="['/foto', foto.idFoto]">
      <div class="photo-wrapper">
        <img
          [src]="foto.rutaWeb || foto.urlCompleta || 'assets/placeholder.jpg'"
          [alt]="foto.titulo"
          loading="lazy"
        />
        @if (foto.estado === 'PENDIENTE') {
          <span class="badge badge-pending">Pendiente</span>
        } @else if (foto.estado === 'RECHAZADA') {
          <span class="badge badge-rejected">Rechazada</span>
        } @else if (foto.destacada) {
          <span class="badge badge-featured">⭐ Destacada</span>
        }
      </div>
      <div class="photo-info">
        <h3>{{ foto.titulo }}</h3>
        @if (foto.categoriaNombre) {
          <span class="category" [style.background]="foto.categoriaColor || '#555'">
            {{ foto.categoriaNombre }}
          </span>
        }
        @if (foto.usuarioNombre) {
          <p class="photographer">📷 {{ foto.usuarioNombre }}</p>
        }
        <div class="photo-meta">
          <span>👁 {{ foto.visitas }}</span>
          <span>📅 {{ foto.fechaSubida | date:'shortDate' }}</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    /* === MOBILE FIRST === */
    .photo-card {
      background: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      cursor: pointer;
    }
    .photo-card:active {
      transform: scale(0.98);
    }
    .photo-wrapper {
      position: relative;
      aspect-ratio: 4/3;
      overflow: hidden;
      background: #f0f0f0;
    }
    .photo-wrapper img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .badge {
      position: absolute;
      top: 8px;
      right: 8px;
      padding: 3px 8px;
      border-radius: 12px;
      font-size: 0.7rem;
      font-weight: 600;
      color: #fff;
    }
    .badge-pending { background: #f39c12; }
    .badge-rejected { background: #e74c3c; }
    .badge-featured { background: #e94560; }
    .photo-info {
      padding: 0.75rem;
    }
    .photo-info h3 {
      margin: 0 0 0.4rem;
      font-size: 0.9rem;
      color: #1a1a2e;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .category {
      display: inline-block;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 0.7rem;
      color: #fff;
      margin-bottom: 0.25rem;
    }
    .photographer {
      margin: 0.25rem 0;
      font-size: 0.8rem;
      color: #666;
    }
    .photo-meta {
      display: flex;
      gap: 0.75rem;
      font-size: 0.75rem;
      color: #999;
      margin-top: 0.3rem;
    }

    /* === ESCRITORIO (min-width: 768px) === */
    @media (min-width: 768px) {
      .photo-card {
        transition: transform 0.2s, box-shadow 0.2s;
      }
      .photo-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.15);
      }
      .photo-card:hover .photo-wrapper img {
        transform: scale(1.05);
      }
      .photo-card:active { transform: none; }
      .photo-wrapper img { transition: transform 0.3s; }
      .photo-info { padding: 1rem; }
      .photo-info h3 { font-size: 1rem; }
      .photographer { font-size: 0.85rem; }
      .photo-meta { font-size: 0.8rem; }
    }
  `]
})
export class PhotoCardComponent {
  @Input({ required: true }) foto!: Foto;
}
