import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FotoService } from '../../core/services/foto.service';
import { Foto, FotoUpdateRequest } from '../../core/models';
import { FotoImagenUrlPipe } from '../../core/pipes/foto-imagen-url.pipe';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [DatePipe, FotoImagenUrlPipe],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminComponent implements OnInit {
  private fotoService = inject(FotoService);

  pendientes = signal<Foto[]>([]);
  todas = signal<Foto[]>([]);
  activeTab = signal<'pendientes' | 'todas'>('pendientes');
  message = signal<string | null>(null);

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
      error: (err) => console.error('Error loading pendientes:', err),
    });
  }

  loadTodas() {
    this.fotoService.listarTodas().subscribe({
      next: (fotos) => this.todas.set(fotos),
      error: (err) => console.error('Error loading todas:', err),
    });
  }

  aprobar(foto: Foto) {
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'APROBADA' }).subscribe({
      next: () => {
        this.message.set(`"${foto.titulo}" aprobada correctamente`);
        this.loadPendientes();
        this.loadTodas();
        setTimeout(() => this.message.set(null), 3000);
      },
      error: (err) => {
        console.error('Error aprobando foto:', err);
        this.message.set('Error al aprobar la foto: ' + (err.error?.error || err.message));
        setTimeout(() => this.message.set(null), 5000);
      },
    });
  }

  rechazar(foto: Foto) {
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'RECHAZADA' }).subscribe({
      next: () => {
        this.message.set(`"${foto.titulo}" rechazada`);
        this.loadPendientes();
        this.loadTodas();
        setTimeout(() => this.message.set(null), 3000);
      },
      error: (err) => {
        console.error('Error rechazando foto:', err);
        this.message.set('Error al rechazar la foto: ' + (err.error?.error || err.message));
        setTimeout(() => this.message.set(null), 5000);
      },
    });
  }

  // Toggle destacada (mostrar en sección destacadas)
  toggleDestacada(foto: Foto) {
    const nuevaDestacada = !foto.destacada;
    const data: FotoUpdateRequest = { destacada: nuevaDestacada };
    
    this.fotoService.actualizar(foto.idFoto, data).subscribe({
      next: () => {
        this.message.set(nuevaDestacada 
          ? `"${foto.titulo}" ahora es destacada` 
          : `"${foto.titulo}" ya no es destacada`);
        this.loadTodas();
        setTimeout(() => this.message.set(null), 3000);
      },
      error: (err) => {
        this.message.set('Error al actualizar: ' + (err.error?.error || err.message));
        setTimeout(() => this.message.set(null), 5000);
      },
    });
  }

  // Toggle orden del carousel (1-5, o null para quitar)
  setOrden(foto: Foto, orden: number | null) {
    const data: FotoUpdateRequest = { orden: orden };
    
    this.fotoService.actualizar(foto.idFoto, data).subscribe({
      next: () => {
        if (orden === null) {
          this.message.set(`"${foto.titulo}" removida del carousel`);
        } else {
          this.message.set(`"${foto.titulo}" asignada al carousel (posición ${orden})`);
        }
        this.loadTodas();
        setTimeout(() => this.message.set(null), 3000);
      },
      error: (err) => {
        this.message.set('Error al actualizar: ' + (err.error?.error || err.message));
        setTimeout(() => this.message.set(null), 5000);
      },
    });
  }
}