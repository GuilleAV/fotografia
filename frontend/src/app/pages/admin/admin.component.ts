import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FotoService } from '../../core/services/foto.service';
import { Foto } from '../../core/models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="admin-section">
      <div class="container">
        <h1>Panel de Administración</h1>

        <!-- Tabs -->
        <div class="tabs">
          <button
            [class.active]="activeTab() === 'pendientes'"
            (click)="setTab('pendientes')"
          >
            Pendientes ({{ pendientes().length }})
          </button>
          <button
            [class.active]="activeTab() === 'todas'"
            (click)="setTab('todas')"
          >
            Todas las Fotos ({{ todas().length }})
          </button>
        </div>

        <!-- Pendientes -->
        @if (activeTab() === 'pendientes') {
          <div class="moderation-queue">
            @for (foto of pendientes(); track foto.idFoto) {
              <div class="moderation-card">
                <div class="moderation-preview">
                  <img [src]="foto.rutaWeb || foto.urlCompleta || 'assets/placeholder.jpg'" [alt]="foto.titulo" />
                </div>
                <div class="moderation-info">
                  <h3>{{ foto.titulo }}</h3>
                  @if (foto.descripcion) {
                    <p>{{ foto.descripcion }}</p>
                  }
                  <div class="meta">
                    <span>📷 {{ foto.usuarioNombre || 'Desconocido' }}</span>
                    <span>📅 {{ foto.fechaSubida | date:'short' }}</span>
                    <span>📂 {{ foto.categoriaNombre }}</span>
                  </div>
                  <div class="actions">
                    <button class="btn-approve" (click)="aprobar(foto)">
                      ✓ Aprobar
                    </button>
                    <button class="btn-reject" (click)="rechazar(foto)">
                      ✗ Rechazar
                    </button>
                  </div>
                </div>
              </div>
            } @empty {
              <p class="empty">No hay fotos pendientes de moderación 🎉</p>
            }
          </div>
        }

        <!-- Todas -->
        @if (activeTab() === 'todas') {
          <div class="table-wrapper">
            <table class="photo-table">
              <thead>
                <tr>
                  <th>Foto</th>
                  <th>Título</th>
                  <th>Fotógrafo</th>
                  <th>Categoría</th>
                  <th>Estado</th>
                  <th>Visitas</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                @for (foto of todas(); track foto.idFoto) {
                  <tr>
                    <td>
                      <img class="table-thumb" [src]="foto.rutaThumbnail || 'assets/placeholder.jpg'" [alt]="foto.titulo" />
                    </td>
                    <td>{{ foto.titulo }}</td>
                    <td>{{ foto.usuarioNombre || '—' }}</td>
                    <td>{{ foto.categoriaNombre || '—' }}</td>
                    <td>
                      <span class="status-badge" [class]="'status-' + foto.estado.toLowerCase()">
                        {{ foto.estado }}
                      </span>
                    </td>
                    <td>{{ foto.visitas }}</td>
                    <td>{{ foto.fechaSubida | date:'shortDate' }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>
    </section>
  `,
  styles: [`
    /* === MOBILE FIRST === */
    .admin-section { padding: 1.5rem 1rem; min-height: 60vh; }
    .container { max-width: 1200px; margin: 0 auto; }
    h1 { color: #1a1a2e; margin-bottom: 1rem; font-size: 1.5rem; }
    .tabs {
      display: flex;
      gap: 0.25rem;
      margin-bottom: 1.5rem;
      border-bottom: 2px solid #e0e0e0;
      overflow-x: auto;
    }
    .tabs button {
      padding: 0.7rem 1rem;
      background: none;
      border: none;
      border-bottom: 3px solid transparent;
      cursor: pointer;
      font-size: 0.9rem;
      color: #666;
      transition: all 0.2s;
      margin-bottom: -2px;
      white-space: nowrap;
      font-weight: 500;
    }
    .tabs button:active { color: #e94560; }
    .tabs button.active {
      color: #e94560;
      border-bottom-color: #e94560;
      font-weight: 700;
    }
    .moderation-queue {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }
    .moderation-card {
      display: flex;
      flex-direction: column;
      background: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    .moderation-preview {
      width: 100%;
      min-height: 180px;
      background: #f0f0f0;
    }
    .moderation-preview img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .moderation-info {
      padding: 1rem;
    }
    .moderation-info h3 { margin: 0 0 0.4rem; color: #1a1a2e; font-size: 1rem; }
    .moderation-info p { margin: 0 0 0.5rem; color: #666; font-size: 0.85rem; }
    .meta {
      display: flex;
      flex-wrap: wrap;
      gap: 0.75rem;
      font-size: 0.8rem;
      color: #999;
      margin-bottom: 1rem;
    }
    .actions { display: flex; gap: 0.5rem; }
    .btn-approve, .btn-reject {
      flex: 1;
      color: #fff;
      border: none;
      padding: 0.75rem 1rem;
      border-radius: 8px;
      cursor: pointer;
      font-weight: 700;
      font-size: 0.9rem;
      transition: opacity 0.2s;
    }
    .btn-approve { background: #27ae60; }
    .btn-approve:active { opacity: 0.8; }
    .btn-reject { background: #e74c3c; }
    .btn-reject:active { opacity: 0.8; }
    .empty { text-align: center; padding: 2rem; color: #666; font-size: 1rem; }

    /* Tabla: scroll horizontal en móvil */
    .table-wrapper {
      overflow-x: auto;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    .photo-table {
      width: 100%;
      min-width: 600px;
      border-collapse: collapse;
      background: #fff;
    }
    .photo-table th {
      background: #1a1a2e;
      color: #fff;
      padding: 0.7rem 0.75rem;
      text-align: left;
      font-size: 0.8rem;
      white-space: nowrap;
    }
    .photo-table td {
      padding: 0.5rem 0.75rem;
      border-bottom: 1px solid #f0f0f0;
      font-size: 0.85rem;
    }
    .table-thumb {
      width: 50px;
      height: 35px;
      object-fit: cover;
      border-radius: 4px;
    }
    .status-badge {
      padding: 3px 6px;
      border-radius: 4px;
      font-size: 0.7rem;
      font-weight: 600;
      white-space: nowrap;
    }
    .status-pendiente { background: #fff3cd; color: #856404; }
    .status-aprobada { background: #d4edda; color: #155724; }
    .status-rechazada { background: #f8d7da; color: #721c24; }

    /* === TABLET (min-width: 600px) === */
    @media (min-width: 600px) {
      .admin-section { padding: 2rem 1.5rem; }
      h1 { font-size: 1.8rem; }
      .tabs button { font-size: 1rem; padding: 0.7rem 1.5rem; }
      .moderation-card { flex-direction: row; }
      .moderation-preview { width: 250px; min-height: 180px; }
      .moderation-info { padding: 1.5rem; }
      .moderation-info h3 { font-size: 1.1rem; }
      .actions { flex: none; }
      .btn-approve, .btn-reject { flex: none; padding: 0.5rem 1.2rem; }
    }

    /* === ESCRITORIO (min-width: 900px) === */
    @media (min-width: 900px) {
      .admin-section { padding: 2rem; }
    }
  `]
})
export class AdminComponent implements OnInit {
  private fotoService = inject(FotoService);

  pendientes = signal<Foto[]>([]);
  todas = signal<Foto[]>([]);
  activeTab = signal<'pendientes' | 'todas'>('pendientes');

  ngOnInit() {
    this.loadPendientes();
    this.loadTodas();
  }

  setTab(tab: 'pendientes' | 'todas') {
    this.activeTab.set(tab);
  }

  loadPendientes() {
    this.fotoService.listarPendientes().subscribe({
      next: (fotos) => this.pendientes.set(fotos),
      error: () => {},
    });
  }

  loadTodas() {
    this.fotoService.listarTodas().subscribe({
      next: (fotos) => this.todas.set(fotos),
      error: () => {},
    });
  }

  aprobar(foto: Foto) {
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'APROBADA' }).subscribe({
      next: () => {
        this.loadPendientes();
        this.loadTodas();
      },
      error: () => {},
    });
  }

  rechazar(foto: Foto) {
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'RECHAZADA' }).subscribe({
      next: () => {
        this.loadPendientes();
        this.loadTodas();
      },
      error: () => {},
    });
  }
}
