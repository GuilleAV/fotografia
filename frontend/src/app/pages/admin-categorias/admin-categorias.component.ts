import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoriaService } from '../../core/services/categoria.service';
import { Categoria } from '../../core/models';

@Component({
  selector: 'app-admin-categorias',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="admin-section">
      <div class="container">
        <h1>Gestionar Categorías</h1>

        <!-- Formulario -->
        <div class="card form-card">
          <h2>{{ editandoId() ? 'Editar Categoría' : 'Nueva Categoría' }}</h2>
          <form (ngSubmit)="guardar()" #form="ngForm">
            <div class="form-row">
              <div class="form-group">
                <label>Nombre</label>
                <input type="text" [(ngModel)]="formModel.nombre" name="nombre" required />
              </div>
              <div class="form-group">
                <label>Slug (URL)</label>
                <input type="text" [(ngModel)]="formModel.slug" name="slug" required placeholder="ej: paisajes" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Icono (Emoji)</label>
                <input type="text" [(ngModel)]="formModel.icono" name="icono" placeholder="📷" />
              </div>
              <div class="form-group">
                <label>Color</label>
                <input type="color" [(ngModel)]="formModel.color" name="color" style="height: 42px; width: 100%;" />
              </div>
              <div class="form-group">
                <label>Orden</label>
                <input type="number" [(ngModel)]="formModel.orden" name="orden" />
              </div>
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" [(ngModel)]="formModel.activo" name="activo" />
                Categoría Activa
              </label>
            </div>
            <div class="form-actions">
              <button type="button" class="btn-cancel" (click)="cancelar()" *ngIf="editandoId()">Cancelar</button>
              <button type="submit" class="btn-primary">{{ editandoId() ? 'Actualizar' : 'Crear' }}</button>
            </div>
          </form>
        </div>

        <!-- Lista -->
        <div class="card list-card">
          <h2>Categorías Existentes</h2>
          <div class="table-wrapper">
            <table class="table">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Slug</th>
                  <th>Orden</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (cat of categorias(); track cat.idCategoria) {
                  <tr>
                    <td><span class="icon">{{ cat.icono }}</span> {{ cat.nombre }}</td>
                    <td>{{ cat.slug }}</td>
                    <td>{{ cat.orden }}</td>
                    <td>
                      <span class="badge" [class.badge-active]="cat.activo" [class.badge-inactive]="!cat.activo">
                        {{ cat.activo ? 'Activa' : 'Inactiva' }}
                      </span>
                    </td>
                    <td class="actions">
                      <button class="btn-sm btn-edit" (click)="editar(cat)" title="Editar">✏️</button>
                      <button class="btn-sm btn-toggle" (click)="toggleEstado(cat)" title="Activar/Desactivar">
                        {{ cat.activo ? '🔒' : '🔓' }}
                      </button>
                      <button class="btn-sm btn-delete" (click)="eliminar(cat.idCategoria)" title="Eliminar">🗑️</button>
                    </td>
                  </tr>
                } @empty {
                  <tr><td colspan="5" class="empty">No hay categorías creadas.</td></tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .admin-section { padding: 1.5rem 1rem; min-height: 60vh; background: #f4f6f9; }
    .container { max-width: 1000px; margin: 0 auto; }
    h1 { color: #1a1a2e; margin-bottom: 1.5rem; font-size: 1.5rem; }
    h2 { margin-top: 0; color: #333; font-size: 1.1rem; }
    .card { background: #fff; padding: 1.25rem; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); margin-bottom: 1.5rem; }
    .form-row { display: grid; grid-template-columns: 1fr; gap: 0.75rem; }
    .form-group { margin-bottom: 0.75rem; }
    .form-group label { display: block; font-size: 0.85rem; font-weight: 600; color: #555; margin-bottom: 0.3rem; }
    .form-group input { width: 100%; padding: 0.7rem; border: 1px solid #ddd; border-radius: 8px; box-sizing: border-box; font-size: 1rem; }
    .form-group input:focus { outline: none; border-color: #e94560; }
    .checkbox-label { display: flex; align-items: center; gap: 0.5rem; cursor: pointer; }
    .checkbox-label input { width: auto; }
    .form-actions { display: flex; gap: 0.5rem; justify-content: flex-end; margin-top: 0.5rem; }
    .btn-primary { background: #2ecc71; color: #fff; border: none; padding: 0.7rem 1.2rem; border-radius: 8px; cursor: pointer; font-weight: 600; font-size: 0.95rem; }
    .btn-primary:active { background: #27ae60; }
    .btn-cancel { background: #95a5a6; color: #fff; border: none; padding: 0.7rem 1.2rem; border-radius: 8px; cursor: pointer; }
    .table-wrapper { overflow-x: auto; border-radius: 8px; }
    .table { width: 100%; min-width: 500px; border-collapse: collapse; }
    .table th { text-align: left; padding: 0.7rem; border-bottom: 2px solid #eee; color: #666; font-size: 0.8rem; white-space: nowrap; }
    .table td { padding: 0.7rem; border-bottom: 1px solid #eee; vertical-align: middle; font-size: 0.9rem; }
    .icon { font-size: 1.2rem; margin-right: 0.5rem; }
    .badge { padding: 3px 8px; border-radius: 12px; font-size: 0.7rem; font-weight: 600; }
    .badge-active { background: #d4edda; color: #155724; }
    .badge-inactive { background: #f8d7da; color: #721c24; }
    .actions { display: flex; gap: 0.4rem; }
    .btn-sm { background: #eee; border: none; width: 32px; height: 32px; border-radius: 6px; cursor: pointer; font-size: 1rem; transition: 0.2s; }
    .btn-sm:hover { background: #ddd; }
    .btn-delete:hover { background: #ffcccc; }
    .empty { text-align: center; color: #999; padding: 1.5rem; }

    @media (min-width: 600px) {
      .admin-section { padding: 2rem 1.5rem; }
      .form-row { grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 1rem; }
      h1 { font-size: 1.8rem; }
    }
  `]
})
export class AdminCategoriasComponent implements OnInit {
  private service = inject(CategoriaService);

  categorias = signal<Categoria[]>([]);
  editandoId = signal<number | null>(null);
  formModel = { idCategoria: null as number | null, nombre: '', slug: '', icono: '📷', color: '#3498db', orden: 0, activo: true };

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listarTodas().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {}
    });
  }

  guardar() {
    if (!this.formModel.nombre || !this.formModel.slug) return;

    const data: Partial<Categoria> = {
      nombre: this.formModel.nombre,
      slug: this.formModel.slug,
      icono: this.formModel.icono,
      color: this.formModel.color,
      orden: this.formModel.orden,
      activo: this.formModel.activo,
    };

    const obs = this.editandoId() 
      ? this.service.actualizar(this.formModel.idCategoria!, data)
      : this.service.crear(data);

    obs.subscribe({
      next: () => {
        this.resetForm();
        this.cargar();
      },
      error: () => {}
    });
  }

  editar(cat: Categoria) {
    this.editandoId.set(cat.idCategoria);
    this.formModel = { 
      idCategoria: cat.idCategoria, 
      nombre: cat.nombre, 
      slug: cat.slug, 
      icono: cat.icono || '📷', 
      color: cat.color || '#3498db', 
      orden: cat.orden || 0, 
      activo: cat.activo 
    };
  }

  eliminar(id: number) {
    if (confirm('¿Eliminar esta categoría?')) {
      this.service.eliminar(id).subscribe(() => this.cargar());
    }
  }

  toggleEstado(cat: Categoria) {
    this.editandoId.set(cat.idCategoria);
    this.formModel = { 
      idCategoria: cat.idCategoria, 
      nombre: cat.nombre, 
      slug: cat.slug, 
      icono: cat.icono || '📷', 
      color: cat.color || '#3498db', 
      orden: cat.orden || 0, 
      activo: !cat.activo 
    };
    this.guardar();
  }

  cancelar() {
    this.resetForm();
  }

  resetForm() {
    this.editandoId.set(null);
    this.formModel = { idCategoria: null, nombre: '', slug: '', icono: '📷', color: '#3498db', orden: 0, activo: true };
  }
}
