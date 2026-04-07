import { Component, OnInit, inject, signal, Renderer2, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FotoService } from '../../core/services/foto.service';
import { AuthService } from '../../core/services/auth.service';
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
  private renderer = inject(Renderer2);
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
    this.renderer.setStyle(document.body, 'overflow', 'hidden');
  }

  closeLightbox() {
    this.lightboxOpen.set(false);
    this.renderer.removeStyle(document.body, 'overflow');
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
