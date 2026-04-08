import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
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
  imports: [FormsModule, PhotoCardComponent, SkeletonComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  private fotoService = inject(FotoService);
  private categoriaService = inject(CategoriaService);
  authService = inject(AuthService);

  // Límite máximo de 30MB para archivos
  private readonly MAX_FILE_SIZE = 30 * 1024 * 1024; // 30MB en bytes
  private readonly ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif'];

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
      const file = input.files[0];
      
      // Validación de tipo
      if (!this.ALLOWED_TYPES.includes(file.type)) {
        this.uploadMessage.set('Tipo de archivo no permitido. Solo se aceptan: JPEG, PNG, WebP, GIF');
        this.uploadSuccess.set(false);
        this.selectedFile = null;
        return;
      }

      // Validación de tamaño (30MB)
      if (file.size > this.MAX_FILE_SIZE) {
        this.uploadMessage.set('El archivo excede el límite máximo de 30MB');
        this.uploadSuccess.set(false);
        this.selectedFile = null;
        return;
      }

      this.selectedFile = file;
      this.uploadMessage.set(null);
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