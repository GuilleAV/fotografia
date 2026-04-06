import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FotoService } from '../../core/services/foto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { AuthService } from '../../core/services/auth.service';
import { PhotoCardComponent } from '../../shared/components/photo-card/photo-card.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { Foto, Categoria, FileUploadResponse } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, PhotoCardComponent, SkeletonComponent],
  template: `
    <section class="dashboard">
      <div class="container">
        <div class="dashboard-header">
          <div>
            <h1>Subir Fotos</h1>
            <p>Gestioná tu portfolio fotográfico</p>
          </div>
          <button class="btn-upload" (click)="showUpload = !showUpload">
            {{ showUpload ? 'Cancelar' : '+ Subir Foto' }}
          </button>
        </div>

        <!-- Upload Form -->
        @if (showUpload) {
          <div class="upload-card">
            <h2>Subir Nueva Foto</h2>
            <form (ngSubmit)="onUpload()" #uploadForm="ngForm">
              @if (uploadMessage()) {
                <div [class]="uploadSuccess() ? 'alert-success' : 'alert-error'">
                  {{ uploadMessage() }}
                </div>
              }

              <div class="form-row">
                <div class="form-group">
                  <label for="archivo">Archivo</label>
                  <input
                    id="archivo"
                    type="file"
                    accept="image/jpeg,image/png,image/webp"
                    (change)="onFileSelected($event)"
                    required
                  />
                  <small>JPG, PNG o WebP. Máximo 10MB.</small>
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label for="titulo">Título</label>
                  <input
                    id="titulo"
                    type="text"
                    [(ngModel)]="uploadData.titulo"
                    name="titulo"
                    required
                    placeholder="Título de la foto"
                  />
                </div>
                <div class="form-group">
                  <label for="categoria">Categoría</label>
                  <select
                    id="categoria"
                    [(ngModel)]="uploadData.idCategoria"
                    name="categoria"
                    required
                  >
                    <option [ngValue]="null">Seleccionar...</option>
                    @for (cat of categorias(); track cat.idCategoria) {
                      <option [ngValue]="cat.idCategoria">{{ cat.nombre }}</option>
                    }
                  </select>
                </div>
              </div>

              <div class="form-group">
                <label for="descripcion">Descripción</label>
                <textarea
                  id="descripcion"
                  [(ngModel)]="uploadData.descripcion"
                  name="descripcion"
                  placeholder="Descripción opcional"
                  rows="3"
                ></textarea>
              </div>

              <button type="submit" class="btn-submit" [disabled]="uploading()">
                {{ uploading() ? 'Subiendo...' : 'Subir Foto' }}
              </button>
            </form>
          </div>
        }

        <!-- Mis Fotos -->
        <h2 class="section-title">Fotos Subidas</h2>

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
        } @else {
          <div class="photo-grid">
            @for (foto of fotos(); track foto.idFoto) {
              <app-photo-card [foto]="foto" />
            } @empty {
              <p class="empty">No subiste fotos todavía. ¡Empezá ahora!</p>
            }
          </div>
        }
      </div>
    </section>
  `,
  styles: [`
    /* === MOBILE FIRST === */
    .dashboard { padding: 1.5rem 1rem; min-height: 60vh; }
    .container { max-width: 1200px; margin: 0 auto; }
    .dashboard-header {
      display: flex;
      flex-direction: column;
      gap: 1rem;
      margin-bottom: 1.5rem;
    }
    .dashboard-header h1 { margin: 0; color: #1a1a2e; font-size: 1.5rem; }
    .dashboard-header p { margin: 0.25rem 0 0; color: #666; font-size: 0.9rem; }
    .btn-upload {
      background: #e94560;
      color: #fff;
      border: none;
      padding: 0.85rem 1.5rem;
      border-radius: 10px;
      font-size: 1rem;
      font-weight: 700;
      cursor: pointer;
      transition: background 0.2s;
      width: 100%;
      text-align: center;
    }
    .btn-upload:active { background: #d63851; }
    .upload-card {
      background: #fff;
      border-radius: 12px;
      padding: 1.5rem 1rem;
      margin-bottom: 1.5rem;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    .upload-card h2 { margin: 0 0 1rem; color: #1a1a2e; font-size: 1.3rem; }
    .form-row {
      display: grid;
      grid-template-columns: 1fr;
      gap: 0;
    }
    .form-group { margin-bottom: 1rem; }
    .form-group label {
      display: block;
      margin-bottom: 0.4rem;
      font-weight: 600;
      color: #333;
      font-size: 0.9rem;
    }
    .form-group input, .form-group select, .form-group textarea {
      width: 100%;
      padding: 0.85rem 1rem;
      border: 2px solid #e0e0e0;
      border-radius: 10px;
      font-size: 1rem;
      box-sizing: border-box;
      font-family: inherit;
      background: #fafafa;
    }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
      outline: none;
      border-color: #e94560;
      box-shadow: 0 0 0 3px rgba(233, 69, 96, 0.1);
      background: #fff;
    }
    .form-group small { color: #999; font-size: 0.8rem; display: block; margin-top: 0.25rem; }
    .btn-submit {
      background: #e94560;
      color: #fff;
      border: none;
      padding: 0.9rem 2rem;
      border-radius: 10px;
      font-size: 1.05rem;
      font-weight: 700;
      cursor: pointer;
      transition: background 0.2s;
      width: 100%;
    }
    .btn-submit:active:not(:disabled) { background: #d63851; }
    .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert-success {
      background: #d4edda; color: #155724; padding: 0.8rem; border-radius: 8px; margin-bottom: 1rem;
      border-left: 4px solid #27ae60;
    }
    .alert-error {
      background: #fde8e8; color: #c0392b; padding: 0.8rem; border-radius: 8px; margin-bottom: 1rem;
      border-left: 4px solid #e74c3c;
    }
    .section-title { margin: 1.5rem 0 1rem; color: #1a1a2e; font-size: 1.3rem; }
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
    .loading, .empty {
      text-align: center;
      padding: 2rem;
      color: #666;
      font-size: 1rem;
    }

    /* === TABLET (min-width: 600px) === */
    @media (min-width: 600px) {
      .dashboard { padding: 2rem 1.5rem; }
      .dashboard-header {
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
      }
      .btn-upload { width: auto; }
      .upload-card { padding: 2rem; }
      .form-row { grid-template-columns: 1fr 1fr; gap: 1rem; }
      .btn-submit { width: auto; }
      .photo-grid { grid-template-columns: repeat(2, 1fr); gap: 1.25rem; }
    }

    /* === ESCRITORIO (min-width: 900px) === */
    @media (min-width: 900px) {
      .dashboard { padding: 2rem; }
      .dashboard-header h1 { font-size: 1.8rem; }
      .photo-grid { grid-template-columns: repeat(3, 1fr); gap: 1.5rem; }
    }
  `]
})
export class DashboardComponent implements OnInit {
  private fotoService = inject(FotoService);
  private categoriaService = inject(CategoriaService);
  authService = inject(AuthService);

  fotos = signal<Foto[]>([]);
  categorias = signal<Categoria[]>([]);
  loading = signal(true);

  showUpload = false;
  uploading = signal(false);
  uploadMessage = signal<string | null>(null);
  uploadSuccess = signal(false);
  selectedFile: File | null = null;
  uploadData = { titulo: '', descripcion: '', idCategoria: null as number | null };

  ngOnInit() {
    this.fotoService.listarMisFotos().subscribe({
      next: (fotos) => {
        this.fotos.set(fotos);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });

    this.categoriaService.listarActivas().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {},
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onUpload() {
    if (!this.selectedFile || !this.uploadData.titulo || !this.uploadData.idCategoria) {
      this.uploadMessage.set('Completá todos los campos obligatorios');
      this.uploadSuccess.set(false);
      return;
    }

    this.uploading.set(true);
    this.uploadMessage.set(null);

    this.fotoService.subirFoto(
      this.selectedFile,
      this.uploadData.titulo,
      this.uploadData.idCategoria,
      this.uploadData.descripcion
    ).subscribe({
      next: (response) => {
        this.uploadMessage.set(response.mensaje);
        this.uploadSuccess.set(true);
        this.uploading.set(false);
        this.selectedFile = null;
        this.uploadData = { titulo: '', descripcion: '', idCategoria: null };

        // Refresh list
        this.fotoService.listarMisFotos().subscribe({
          next: (fotos) => this.fotos.set(fotos),
        });
      },
      error: (err) => {
        this.uploadMessage.set(err.error?.error || 'Error al subir la foto');
        this.uploadSuccess.set(false);
        this.uploading.set(false);
      },
    });
  }
}
