import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';
import { Usuario } from '../../core/models';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="admin-section">
      <div class="container">
        <h1>Gestionar Usuarios</h1>

        <!-- Formulario -->
        <div class="card form-card">
          <h2>{{ editandoId() ? 'Editar Usuario' : 'Nuevo Usuario' }}</h2>
          <form (ngSubmit)="guardar()" #form="ngForm">
            <div class="form-row">
              <div class="form-group">
                <label>Usuario</label>
                <input type="text" [(ngModel)]="formModel.username" name="username" required />
              </div>
              <div class="form-group">
                <label>Email</label>
                <input type="email" [(ngModel)]="formModel.email" name="email" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Nombre</label>
                <input type="text" [(ngModel)]="formModel.nombre" name="nombre" required />
              </div>
              <div class="form-group">
                <label>Apellido</label>
                <input type="text" [(ngModel)]="formModel.apellido" name="apellido" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Contraseña {{ editandoId() ? '(Vacío = no cambiar)' : '' }}</label>
                <input type="password" [(ngModel)]="formModel.password" name="password" [required]="!editandoId()" />
              </div>
              <div class="form-group">
                <label>Rol</label>
                <select [(ngModel)]="formModel.rol" name="rol" required>
                  <option value="FOTOGRAFO">Fotógrafo</option>
                  <option value="ADMIN">Administrador</option>
                  <option value="SUPER_ADMIN">Super Admin</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" [(ngModel)]="formModel.activo" name="activo" />
                Usuario Activo
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
          <h2>Usuarios del Sistema</h2>
          <div class="table-wrapper">
            <table class="table">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Nombre</th>
                  <th>Rol</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (user of usuarios(); track user.idUsuario) {
                  <tr>
                    <td>{{ user.username }}</td>
                    <td>{{ user.nombre }} {{ user.apellido }}</td>
                    <td><span class="badge badge-role">{{ user.rol }}</span></td>
                    <td>
                      <span class="badge" [class.badge-active]="user.activo" [class.badge-inactive]="!user.activo">
                        {{ user.activo ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                    <td class="actions">
                      <button class="btn-sm btn-edit" (click)="editar(user)" title="Editar">✏️</button>
                      <button class="btn-sm btn-toggle" (click)="toggleEstado(user)" title="Activar/Desactivar">
                        {{ user.activo ? '🔒' : '🔓' }}
                      </button>
                      <button class="btn-sm btn-delete" (click)="eliminar(user.idUsuario)" title="Eliminar">🗑️</button>
                    </td>
                  </tr>
                } @empty {
                  <tr><td colspan="5" class="empty">No hay usuarios creados.</td></tr>
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
    .form-group input, .form-group select { width: 100%; padding: 0.7rem; border: 1px solid #ddd; border-radius: 8px; box-sizing: border-box; font-size: 1rem; }
    .form-group input:focus, .form-group select:focus { outline: none; border-color: #e94560; }
    .checkbox-label { display: flex; align-items: center; gap: 0.5rem; cursor: pointer; }
    .checkbox-label input { width: auto; }
    .form-actions { display: flex; gap: 0.5rem; justify-content: flex-end; margin-top: 0.5rem; }
    .btn-primary { background: #3498db; color: #fff; border: none; padding: 0.7rem 1.2rem; border-radius: 8px; cursor: pointer; font-weight: 600; font-size: 0.95rem; }
    .btn-primary:active { background: #2980b9; }
    .btn-cancel { background: #95a5a6; color: #fff; border: none; padding: 0.7rem 1.2rem; border-radius: 8px; cursor: pointer; }
    .table-wrapper { overflow-x: auto; border-radius: 8px; }
    .table { width: 100%; min-width: 500px; border-collapse: collapse; }
    .table th { text-align: left; padding: 0.7rem; border-bottom: 2px solid #eee; color: #666; font-size: 0.8rem; white-space: nowrap; }
    .table td { padding: 0.7rem; border-bottom: 1px solid #eee; vertical-align: middle; font-size: 0.9rem; }
    .badge { padding: 3px 8px; border-radius: 12px; font-size: 0.7rem; font-weight: 600; }
    .badge-role { background: #e8f4fd; color: #2980b9; }
    .badge-active { background: #d4edda; color: #155724; }
    .badge-inactive { background: #f8d7da; color: #721c24; }
    .actions { display: flex; gap: 0.4rem; }
    .btn-sm { background: #eee; border: none; width: 32px; height: 32px; border-radius: 6px; cursor: pointer; font-size: 1rem; transition: 0.2s; }
    .btn-sm:hover { background: #ddd; }
    .btn-delete:hover { background: #ffcccc; }
    .empty { text-align: center; color: #999; padding: 1.5rem; }

    @media (min-width: 600px) {
      .admin-section { padding: 2rem 1.5rem; }
      .form-row { grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; }
      h1 { font-size: 1.8rem; }
    }
  `]
})
export class AdminUsuariosComponent implements OnInit {
  private service = inject(UsuarioService);

  usuarios = signal<Usuario[]>([]);
  editandoId = signal<number | null>(null);
  formModel = { idUsuario: null as number | null, username: '', email: '', nombre: '', apellido: '', password: '', rol: 'FOTOGRAFO' as string, activo: true };

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listarTodos().subscribe({
      next: (users) => this.usuarios.set(users),
      error: () => {}
    });
  }

  guardar() {
    if (!this.formModel.username || !this.formModel.email) return;

    // Al editar, si la contraseña está vacía, la enviamos como null para no sobreescribirla
    const dataToSend: Partial<Usuario> & { password?: string | null } = {
      username: this.formModel.username,
      email: this.formModel.email,
      nombre: this.formModel.nombre,
      apellido: this.formModel.apellido,
      rol: this.formModel.rol as 'FOTOGRAFO' | 'ADMIN' | 'SUPER_ADMIN',
      activo: this.formModel.activo,
    };

    if (this.formModel.password) {
      dataToSend.password = this.formModel.password;
    }

    const obs = this.editandoId() 
      ? this.service.actualizar(this.formModel.idUsuario!, dataToSend)
      : this.service.crear(dataToSend);

    obs.subscribe({
      next: () => {
        this.resetForm();
        this.cargar();
      },
      error: () => {}
    });
  }

  editar(user: Usuario) {
    this.editandoId.set(user.idUsuario);
    this.formModel = { 
      idUsuario: user.idUsuario, 
      username: user.username, 
      email: user.email, 
      nombre: user.nombre || '', 
      apellido: user.apellido || '', 
      password: '', 
      rol: user.rol || 'FOTOGRAFO', 
      activo: user.activo 
    }; 
  }

  eliminar(id: number) {
    if (confirm('¿Eliminar este usuario?')) {
      this.service.eliminar(id).subscribe(() => this.cargar());
    }
  }

  toggleEstado(user: Usuario) {
    this.editandoId.set(user.idUsuario);
    this.formModel = { 
      idUsuario: user.idUsuario, 
      username: user.username, 
      email: user.email, 
      nombre: user.nombre || '', 
      apellido: user.apellido || '', 
      password: '', 
      rol: user.rol || 'FOTOGRAFO', 
      activo: !user.activo 
    };
    this.guardar();
  }

  cancelar() {
    this.resetForm();
  }

  resetForm() {
    this.editandoId.set(null);
    this.formModel = { idUsuario: null, username: '', email: '', nombre: '', apellido: '', password: '', rol: 'FOTOGRAFO', activo: true };
  }
}
