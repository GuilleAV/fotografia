import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FotoService } from '../../core/services/foto.service';
import { Foto } from '../../core/models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
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
