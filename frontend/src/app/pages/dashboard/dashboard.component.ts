import { ChangeDetectionStrategy, Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FotoService } from '../../core/services/foto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { AuthService } from '../../core/services/auth.service';
import { PhotoCardComponent } from '../../shared/components/photo-card/photo-card.component';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';
import { Foto, Categoria, FileUploadResponse } from '../../core/models';

interface UploadItem {
  id: number;
  file: File;
  titulo: string;
  descripcion: string;
  comentario: string;
  idCategoria: number | null;
}

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
  totalVistas = computed(() => this.fotos().reduce((acc, foto) => acc + (foto.visitas || 0), 0));

  showUpload = false;
  uploading = signal(false);
  uploadMessage = signal<string | null>(null);
  uploadSuccess = signal(false);
  dragActive = signal(false);
  uploadTotal = signal(0);
  uploadProcessed = signal(0);
  uploadSucceeded = signal(0);
  uploadFailed = signal(0);
  uploadCurrentFile = signal<string | null>(null);
  uploadPercent = computed(() => {
    const total = this.uploadTotal();
    if (total <= 0) return 0;
    return Math.round((this.uploadProcessed() / total) * 100);
  });

  quickUploading = signal(false);
  quickMessage = signal<string | null>(null);
  quickSuccess = signal(false);
  quickFile: File | null = null;
  quickData = { titulo: '', descripcion: '', comentario: '', idCategoria: null as number | null };

  bulkData = {
    idCategoria: null as number | null,
    descripcion: '',
    comentario: '',
    prefijoTitulo: '',
    sufijoTitulo: '',
  };

  uploadQueue = signal<UploadItem[]>([]);
  modoLote = signal<'compacto' | 'detalle'>('detalle');
  private uploadItemId = 1;

  toggleUploadPanel() {
    if (this.uploading() || this.quickUploading()) {
      return;
    }
    this.showUpload = !this.showUpload;
  }

  @HostListener('window:beforeunload', ['$event'])
  preventCloseDuringUpload(event: BeforeUnloadEvent) {
    if (this.uploading() || this.quickUploading()) {
      event.preventDefault();
      event.returnValue = 'Hay una subida en curso. Si salís ahora, se interrumpe.';
    }
  }

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
    if (!input.files || input.files.length === 0) {
      return;
    }

    this.procesarArchivos(Array.from(input.files));
    input.value = '';
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.dragActive.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.dragActive.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.dragActive.set(false);

    if (!event.dataTransfer?.files?.length) {
      return;
    }

    this.procesarArchivos(Array.from(event.dataTransfer.files));
  }

  onQuickFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length > 0 ? input.files[0] : null;

    if (!file) {
      return;
    }

    const error = this.validarArchivo(file);
    if (error) {
      this.quickMessage.set(error);
      this.quickSuccess.set(false);
      this.quickFile = null;
      input.value = '';
      return;
    }

    this.quickFile = file;
    this.quickData.titulo = this.quickData.titulo || this.generarTituloDesdeArchivo(file.name);
    this.quickMessage.set(null);
  }

  onQuickUpload() {
    if (!this.quickFile || !this.quickData.titulo.trim() || !this.quickData.idCategoria) {
      this.quickMessage.set('Completá archivo, título y categoría para subir una foto');
      this.quickSuccess.set(false);
      return;
    }

    this.quickUploading.set(true);
    this.quickMessage.set(null);
    this.quickSuccess.set(false);

    this.fotoService.subirFoto(
      this.quickFile,
      this.quickData.titulo,
      this.quickData.idCategoria,
      this.quickData.descripcion,
      this.quickData.comentario
    ).subscribe({
      next: (response) => {
        this.quickMessage.set(response.mensaje);
        this.quickSuccess.set(true);
        this.quickUploading.set(false);
        this.quickFile = null;
        this.quickData = { titulo: '', descripcion: '', comentario: '', idCategoria: null };

        this.fotoService.listarMisFotos().subscribe({
          next: (fotos) => this.fotos.set(fotos),
        });
      },
      error: (err) => {
        this.quickMessage.set(err.error?.error || 'Error al subir la foto');
        this.quickSuccess.set(false);
        this.quickUploading.set(false);
      },
    });
  }

  private procesarArchivos(archivos: File[]) {
    if (!archivos.length) {
      return;
    }

    const nuevos: UploadItem[] = [];
    const errores: string[] = [];

    for (const file of archivos) {
      const error = this.validarArchivo(file);
      if (error) {
        errores.push(`${file.name}: ${error}`);
        continue;
      }

      nuevos.push({
        id: this.uploadItemId++,
        file,
        titulo: this.generarTituloDesdeArchivo(file.name),
        descripcion: '',
        comentario: '',
        idCategoria: null,
      });
    }

    if (nuevos.length) {
      this.uploadQueue.update((actual) => [...actual, ...nuevos]);
    }

    if (errores.length) {
      this.uploadMessage.set(`Algunos archivos se omitieron: ${errores.join(' | ')}`);
      this.uploadSuccess.set(false);
    } else {
      this.uploadMessage.set(null);
    }
  }

  actualizarItem(index: number, changes: Partial<UploadItem>) {
    this.uploadQueue.update((items) =>
      items.map((item, idx) => (idx === index ? { ...item, ...changes } : item))
    );
  }

  eliminarItem(index: number) {
    this.uploadQueue.update((items) => items.filter((_, idx) => idx !== index));
  }

  aplicarMasivoATodas() {
    this.aplicarMasivo(false);
  }

  aplicarMasivoSoloVacias() {
    this.aplicarMasivo(true);
  }

  setModoLote(modo: 'compacto' | 'detalle') {
    this.modoLote.set(modo);
  }

  onUpload() {
    const items = this.uploadQueue();

    if (!items.length) {
      this.uploadMessage.set('Seleccioná al menos una foto para subir');
      this.uploadSuccess.set(false);
      return;
    }

    const incompletos = items.filter((item) => !item.titulo.trim() || !item.idCategoria);
    if (incompletos.length) {
      this.uploadMessage.set('Completá título y categoría en todas las fotos');
      this.uploadSuccess.set(false);
      return;
    }

    this.uploading.set(true);
    this.uploadMessage.set(null);
    this.uploadSuccess.set(false);
    this.uploadTotal.set(items.length);
    this.uploadProcessed.set(0);
    this.uploadSucceeded.set(0);
    this.uploadFailed.set(0);
    this.uploadCurrentFile.set(null);

    this.subirEnLote(0, 0, 0);
  }

  private subirEnLote(index: number, exitosas: number, fallidas: number) {
    const items = this.uploadQueue();

    if (index >= items.length) {
      const mensaje = `Subida finalizada. Exitosas: ${exitosas}. Fallidas: ${fallidas}.`;
      this.uploadMessage.set(mensaje);
      this.uploadSuccess.set(fallidas === 0);
      this.uploading.set(false);
      this.uploadCurrentFile.set(null);

      if (fallidas === 0) {
        this.uploadQueue.set([]);
      }

      this.fotoService.listarMisFotos().subscribe({
        next: (fotos) => this.fotos.set(fotos),
      });
      return;
    }

    const item = items[index];
    this.uploadCurrentFile.set(item.file.name);

    this.fotoService.subirFoto(
      item.file,
      item.titulo,
      item.idCategoria!,
      item.descripcion,
      item.comentario
    ).subscribe({
      next: (_response: FileUploadResponse) => {
        this.uploadProcessed.set(index + 1);
        this.uploadSucceeded.set(exitosas + 1);
        this.uploadFailed.set(fallidas);
        this.subirEnLote(index + 1, exitosas + 1, fallidas);
      },
      error: () => {
        this.uploadProcessed.set(index + 1);
        this.uploadSucceeded.set(exitosas);
        this.uploadFailed.set(fallidas + 1);
        this.subirEnLote(index + 1, exitosas, fallidas + 1);
      },
    });
  }

  private generarTituloDesdeArchivo(nombre: string): string {
    const sinExtension = nombre.replace(/\.[^/.]+$/, '');
    return sinExtension.replace(/[-_]+/g, ' ').trim();
  }

  private aplicarMasivo(soloVacias: boolean) {
    const categoriaGlobal = this.bulkData.idCategoria;
    const descripcionGlobal = this.bulkData.descripcion.trim();
    const comentarioGlobal = this.bulkData.comentario.trim();
    const prefijo = this.bulkData.prefijoTitulo.trim();
    const sufijo = this.bulkData.sufijoTitulo.trim();

    const hayCambios =
      categoriaGlobal !== null ||
      !!descripcionGlobal ||
      !!comentarioGlobal ||
      !!prefijo ||
      !!sufijo;

    if (!hayCambios) {
      this.uploadMessage.set('Completá al menos un campo en acciones masivas para aplicar cambios');
      this.uploadSuccess.set(false);
      return;
    }

    this.uploadQueue.update((items) =>
      items.map((item) => {
        const descripcionVacia = !item.descripcion?.trim();
        const comentarioVacio = !item.comentario?.trim();
        const categoriaVacia = !item.idCategoria;
        const tituloVacio = !item.titulo?.trim();

        const aplicarTitulo = (prefijo || sufijo) && (!soloVacias || tituloVacio);
        const tituloBase = item.titulo?.trim() || this.generarTituloDesdeArchivo(item.file.name);
        const nuevoTitulo = aplicarTitulo
          ? `${prefijo} ${tituloBase} ${sufijo}`.replace(/\s+/g, ' ').trim()
          : item.titulo;

        return {
          ...item,
          titulo: nuevoTitulo,
          idCategoria:
            categoriaGlobal !== null && (!soloVacias || categoriaVacia)
              ? categoriaGlobal
              : item.idCategoria,
          descripcion:
            descripcionGlobal && (!soloVacias || descripcionVacia)
              ? descripcionGlobal
              : item.descripcion,
          comentario:
            comentarioGlobal && (!soloVacias || comentarioVacio)
              ? comentarioGlobal
              : item.comentario,
        };
      })
    );

    this.uploadMessage.set(soloVacias
      ? 'Acciones masivas aplicadas en campos vacíos'
      : 'Acciones masivas aplicadas en toda la tabla');
    this.uploadSuccess.set(true);
  }

  private validarArchivo(file: File): string | null {
    if (!this.ALLOWED_TYPES.includes(file.type)) {
      return 'formato no permitido';
    }

    if (file.size > this.MAX_FILE_SIZE) {
      return 'supera 30MB';
    }

    return null;
  }
}
