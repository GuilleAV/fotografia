import { Component, OnInit, inject, signal, ChangeDetectionStrategy, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FotoService } from '../../core/services/foto.service';
import { FormsModule } from '@angular/forms';
import { CategoriaService } from '../../core/services/categoria.service';
import { Categoria, Foto, FotoEstadoLoteResponse } from '../../core/models';
import { FotoImagenUrlPipe } from '../../core/pipes/foto-imagen-url.pipe';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [DatePipe, FormsModule, FotoImagenUrlPipe],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminComponent implements OnInit {
  private fotoService = inject(FotoService);
  private categoriaService = inject(CategoriaService);

  readonly estadoFilterOptions: Array<'TODOS' | 'PENDIENTE' | 'APROBADA' | 'RECHAZADA'> = [
    'TODOS',
    'PENDIENTE',
    'APROBADA',
    'RECHAZADA',
  ];

  pendientes = signal<Foto[]>([]);
  todas = signal<Foto[]>([]);
  categorias = signal<Categoria[]>([]);
  tieneCategorias = computed(() => this.categorias().length > 0);
  filtroEstado = signal<'TODOS' | 'PENDIENTE' | 'APROBADA' | 'RECHAZADA'>('TODOS');
  filtroCategoria = signal<number | null>(null);
  pageSizeOptions = [10, 20, 50];
  pageSize = signal(10);
  currentPage = signal(1);
  totalFotosGlobal = computed(() => this.todas().length);
  todasFiltradas = computed(() => {
    const estado = this.filtroEstado();
    const categoria = this.filtroCategoria();

    return this.todas().filter((foto) => {
      const cumpleEstado = estado === 'TODOS' || foto.estado === estado;
      const cumpleCategoria = categoria === null || foto.idCategoria === categoria;
      return cumpleEstado && cumpleCategoria;
    });
  });
  totalFotos = computed(() => this.todasFiltradas().length);
  totalPages = computed(() => Math.max(1, Math.ceil(this.totalFotos() / this.pageSize())));
  todasPaginadas = computed(() => {
    const sorted = [...this.todasFiltradas()].sort((a, b) => {
      const dateA = a.fechaSubida ? new Date(a.fechaSubida).getTime() : 0;
      const dateB = b.fechaSubida ? new Date(b.fechaSubida).getTime() : 0;
      if (dateA !== dateB) {
        return dateB - dateA;
      }
      return (b.idFoto || 0) - (a.idFoto || 0);
    });

    const start = (this.currentPage() - 1) * this.pageSize();
    return sorted.slice(start, start + this.pageSize());
  });
  selectedById = signal<Record<number, boolean>>({});
  selectedIds = computed(() =>
    Object.entries(this.selectedById())
      .filter(([, selected]) => selected)
      .map(([id]) => Number(id))
      .filter((id) => Number.isFinite(id))
  );
  selectedCount = computed(() => this.selectedIds().length);
  allPageSelected = computed(() => {
    const pageItems = this.todasPaginadas();
    if (pageItems.length === 0) {
      return false;
    }
    const selected = this.selectedById();
    return pageItems.every((foto) => !!selected[foto.idFoto]);
  });
  allFilteredSelected = computed(() => {
    const filtered = this.todasFiltradas();
    if (filtered.length === 0) {
      return false;
    }
    const selected = this.selectedById();
    return filtered.every((foto) => !!selected[foto.idFoto]);
  });
  pendientesVisibles = computed(() => this.pendientes());
  selectedPendingIds = computed(() => {
    const pendientesSet = new Set(this.pendientesVisibles().map((foto) => foto.idFoto));
    return this.selectedIds().filter((id) => pendientesSet.has(id));
  });
  selectedPendingCount = computed(() => this.selectedPendingIds().length);
  allPendientesVisiblesSelected = computed(() => {
    const visibles = this.pendientesVisibles();
    if (visibles.length === 0) {
      return false;
    }
    const selected = this.selectedById();
    return visibles.every((foto) => !!selected[foto.idFoto]);
  });
  allPendientesSelected = computed(() => {
    const pendientes = this.pendientes();
    if (pendientes.length === 0) {
      return false;
    }
    const selected = this.selectedById();
    return pendientes.every((foto) => !!selected[foto.idFoto]);
  });
  batchProcessing = signal(false);
  batchTotal = signal(0);
  canApplyBatch = computed(() => this.selectedCount() > 0 && !this.batchProcessing());
  canApplyPendingBatch = computed(() => this.selectedPendingCount() > 0 && !this.batchProcessing());
  activeTab = signal<'pendientes' | 'todas'>('pendientes');
  message = signal<string | null>(null);
  messageType = signal<'success' | 'error'>('success');
  fotoPreview = signal<Foto | null>(null);

  categoriaDrafts = signal<Record<number, number | null>>({});
  updatingCategoriaByFoto = signal<Record<number, boolean>>({});
  private messageTimeoutId: ReturnType<typeof setTimeout> | null = null;

  ngOnInit() {
    this.loadCategorias();
    this.loadPendientes();
    this.loadTodas();
  }

  loadCategorias() {
    this.categoriaService.listarActivas().subscribe({
      next: (categorias) => this.categorias.set(categorias),
      error: (err) => console.error('Error loading categorias:', err),
    });
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
      next: (fotos) => {
        this.todas.set(fotos);
        this.sincronizarBorradoresCategoria(fotos);
        this.normalizarSeleccionConFotosActuales(fotos);
        this.ajustarPaginaActual();
      },
      error: (err) => console.error('Error loading todas:', err),
    });
  }

  setPageSize(size: number) {
    const sanitized = this.pageSizeOptions.includes(Number(size)) ? Number(size) : 10;
    this.pageSize.set(sanitized);
    this.currentPage.set(1);
  }

  setFiltroEstado(value: string) {
    const typed = value as 'TODOS' | 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';
    const sanitized = this.estadoFilterOptions.includes(typed) ? typed : 'TODOS';
    this.filtroEstado.set(sanitized);
    this.currentPage.set(1);
  }

  setFiltroCategoria(value: number | null) {
    if (value === null || value === undefined) {
      this.filtroCategoria.set(null);
      this.currentPage.set(1);
      return;
    }

    const parsed = Number(value);
    this.filtroCategoria.set(Number.isFinite(parsed) ? parsed : null);
    this.currentPage.set(1);
  }

  prevPage() {
    this.currentPage.update((value) => Math.max(1, value - 1));
  }

  nextPage() {
    this.currentPage.update((value) => Math.min(this.totalPages(), value + 1));
  }

  aprobar(foto: Foto) {
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'APROBADA' }).subscribe({
      next: () => {
        this.mostrarMensaje(`"${foto.titulo}" aprobada correctamente`, 'success', 3000);
        this.loadPendientes();
        this.loadTodas();
      },
      error: (err) => {
        console.error('Error aprobando foto:', err);
        this.mostrarMensaje('Error al aprobar la foto: ' + (err.error?.error || err.message), 'error', 5000);
      },
    });
  }

  rechazar(foto: Foto) {
    this.fotoService.cambiarEstado(foto.idFoto, { estado: 'RECHAZADA' }).subscribe({
      next: () => {
        this.mostrarMensaje(`"${foto.titulo}" rechazada`, 'success', 3000);
        this.loadPendientes();
        this.loadTodas();
      },
      error: (err) => {
        console.error('Error rechazando foto:', err);
        this.mostrarMensaje('Error al rechazar la foto: ' + (err.error?.error || err.message), 'error', 5000);
      },
    });
  }

  abrirPreview(foto: Foto) {
    this.fotoPreview.set(foto);
  }

  cerrarPreview() {
    this.fotoPreview.set(null);
  }

  isSelected(idFoto: number): boolean {
    return !!this.selectedById()[idFoto];
  }

  toggleSelectFoto(idFoto: number, selected: boolean) {
    this.selectedById.update((actual) => ({
      ...actual,
      [idFoto]: selected,
    }));
  }

  toggleSelectPagina(selected: boolean) {
    const pageItems = this.todasPaginadas();
    this.selectedById.update((actual) => {
      const copia = { ...actual };
      for (const foto of pageItems) {
        copia[foto.idFoto] = selected;
      }
      return copia;
    });
  }

  toggleSelectPendientesVisibles(selected: boolean) {
    const visibles = this.pendientesVisibles();
    this.selectedById.update((actual) => {
      const copia = { ...actual };
      for (const foto of visibles) {
        copia[foto.idFoto] = selected;
      }
      return copia;
    });
  }

  seleccionarTodasFiltradas() {
    const filtered = this.todasFiltradas();
    this.selectedById.update((actual) => {
      const copia = { ...actual };
      for (const foto of filtered) {
        copia[foto.idFoto] = true;
      }
      return copia;
    });
  }

  seleccionarTodasPendientes() {
    const pendientes = this.pendientes();
    this.selectedById.update((actual) => {
      const copia = { ...actual };
      for (const foto of pendientes) {
        copia[foto.idFoto] = true;
      }
      return copia;
    });
  }

  limpiarSeleccion() {
    this.selectedById.set({});
  }

  limpiarSeleccionPendientes() {
    const pendientesSet = new Set(this.pendientes().map((foto) => foto.idFoto));
    this.selectedById.update((actual) => {
      const copia = { ...actual };
      for (const id of pendientesSet) {
        delete copia[id];
      }
      return copia;
    });
  }

  moderarPendientesSeleccion(estado: 'APROBADA' | 'RECHAZADA') {
    this.ejecutarModeracionLote(this.selectedPendingIds(), estado);
  }

  moderarSeleccion(estado: 'APROBADA' | 'RECHAZADA') {
    this.ejecutarModeracionLote(this.selectedIds(), estado);
  }

  private ejecutarModeracionLote(ids: number[], estado: 'APROBADA' | 'RECHAZADA') {
    if (ids.length === 0 || this.batchProcessing()) {
      return;
    }

    const accion = estado === 'APROBADA' ? 'aprobar' : 'rechazar';
    const ok = window.confirm(`Vas a ${accion} ${ids.length} foto(s). ¿Continuar?`);
    if (!ok) {
      return;
    }

    this.batchProcessing.set(true);
    this.batchTotal.set(ids.length);
    this.fotoService.cambiarEstadoLote({ ids, estado }).subscribe({
      next: (res) => {
        this.batchProcessing.set(false);
        this.batchTotal.set(0);
        this.quitarSeleccion(ids);
        this.loadPendientes();
        this.loadTodas();

        const texto = this.mensajeLote(res);
        const tipo: 'success' | 'error' = res.errores > 0 ? 'error' : 'success';
        this.mostrarMensaje(texto, tipo, 6000);
      },
      error: (err) => {
        this.batchProcessing.set(false);
        this.batchTotal.set(0);
        this.mostrarMensaje(err.error?.error || 'Error al procesar moderación por lote', 'error', 6000);
      },
    });
  }

  categoriaSeleccionada(foto: Foto): number | null {
    const borrador = this.categoriaDrafts()[foto.idFoto];
    return borrador !== undefined ? borrador : (foto.idCategoria ?? null);
  }

  actualizarCategoriaBorrador(idFoto: number, idCategoria: number | null) {
    this.categoriaDrafts.update((actual) => ({
      ...actual,
      [idFoto]: idCategoria,
    }));
  }

  hayCambioCategoria(foto: Foto): boolean {
    const idCategoria = this.categoriaSeleccionada(foto);
    if (!idCategoria) {
      return false;
    }
    return idCategoria !== foto.idCategoria;
  }

  puedeGuardarCategoria(foto: Foto): boolean {
    return this.tieneCategorias()
      && this.hayCambioCategoria(foto)
      && !this.guardandoCategoria(foto.idFoto)
      && !this.batchProcessing();
  }

  guardandoCategoria(idFoto: number): boolean {
    return !!this.updatingCategoriaByFoto()[idFoto];
  }

  restablecerCategoriaBorrador(foto: Foto) {
    this.actualizarCategoriaBorrador(foto.idFoto, foto.idCategoria ?? null);
  }

  guardarCategoria(foto: Foto) {
    const idCategoria = this.categoriaSeleccionada(foto);
    if (!this.tieneCategorias()) {
      this.mostrarMensaje('No hay categorías activas disponibles para asignar', 'error', 5000);
      return;
    }

    if (!idCategoria || idCategoria === foto.idCategoria) {
      return;
    }

    this.updatingCategoriaByFoto.update((actual) => ({ ...actual, [foto.idFoto]: true }));

    this.fotoService.actualizar(foto.idFoto, { idCategoria }).subscribe({
      next: () => {
        const categoria = this.categorias().find((cat) => cat.idCategoria === idCategoria);
        this.actualizarCategoriaEnListado(this.todas, foto.idFoto, idCategoria, categoria);
        this.actualizarCategoriaEnListado(this.pendientes, foto.idFoto, idCategoria, categoria);

        this.mostrarMensaje(`Categoría actualizada para "${foto.titulo}"`, 'success', 3000);
        this.limpiarFlagGuardandoCategoria(foto.idFoto);
      },
      error: (err) => {
        this.mostrarMensaje(err.error?.error || 'Error al actualizar categoría', 'error', 5000);
        this.limpiarFlagGuardandoCategoria(foto.idFoto);
      },
    });
  }

  private actualizarCategoriaEnListado(
    source: { update: (fn: (value: Foto[]) => Foto[]) => void },
    idFoto: number,
    idCategoria: number,
    categoria?: Categoria
  ) {
    source.update((fotos) =>
      fotos.map((item) =>
        item.idFoto === idFoto
          ? {
              ...item,
              idCategoria,
              categoriaNombre: categoria?.nombre ?? item.categoriaNombre,
              categoriaSlug: categoria?.slug ?? item.categoriaSlug,
              categoriaColor: categoria?.color ?? item.categoriaColor,
            }
          : item
      )
    );
  }

  private sincronizarBorradoresCategoria(fotos: Foto[]) {
    const borradores: Record<number, number | null> = {};
    for (const foto of fotos) {
      borradores[foto.idFoto] = foto.idCategoria ?? null;
    }
    this.categoriaDrafts.set(borradores);
  }

  private limpiarFlagGuardandoCategoria(idFoto: number) {
    this.updatingCategoriaByFoto.update((actual) => {
      const copia = { ...actual };
      delete copia[idFoto];
      return copia;
    });
  }

  private ajustarPaginaActual() {
    if (this.currentPage() > this.totalPages()) {
      this.currentPage.set(this.totalPages());
    }
  }

  private normalizarSeleccionConFotosActuales(fotos: Foto[]) {
    const idsActuales = new Set(fotos.map((f) => f.idFoto));
    this.selectedById.update((actual) => {
      const normalizado: Record<number, boolean> = {};
      for (const [idStr, selected] of Object.entries(actual)) {
        const id = Number(idStr);
        if (selected && idsActuales.has(id)) {
          normalizado[id] = true;
        }
      }
      return normalizado;
    });
  }

  private quitarSeleccion(ids: number[]) {
    this.selectedById.update((actual) => {
      const copia = { ...actual };
      for (const id of ids) {
        delete copia[id];
      }
      return copia;
    });
  }

  private mensajeLote(res: FotoEstadoLoteResponse): string {
    return `Lote ${res.estadoSolicitado}: ${res.procesadas} procesadas, ${res.omitidas} omitidas, ${res.errores} con error.`;
  }

  private mostrarMensaje(texto: string, tipo: 'success' | 'error', timeoutMs: number) {
    this.messageType.set(tipo);
    this.message.set(texto);
    this.limpiarTimerMensaje();
    this.messageTimeoutId = setTimeout(() => this.message.set(null), timeoutMs);
  }

  private limpiarTimerMensaje() {
    if (this.messageTimeoutId !== null) {
      clearTimeout(this.messageTimeoutId);
      this.messageTimeoutId = null;
    }
  }
}
