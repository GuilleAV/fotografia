import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';
import { Usuario } from '../../core/models';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-usuarios.component.html',
  styleUrls: ['./admin-usuarios.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminUsuariosComponent implements OnInit {
  private service = inject(UsuarioService);
  private fb = inject(FormBuilder);

  usuarios = signal<Usuario[]>([]);
  editandoId = signal<number | null>(null);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    nombre: ['', Validators.required],
    apellido: ['', Validators.required],
    password: [''],
    rol: ['FOTOGRAFO', Validators.required],
    activo: [true],
  });

  get isEditing() { return this.editandoId() !== null; }
  get passwordRequired() { return !this.isEditing && this.form.get('password'); }

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listarTodos().subscribe({
      next: (users) => this.usuarios.set(users),
      error: () => {},
    });
  }

  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // Validar password solo en creación
    if (!this.isEditing && !this.form.value.password) {
      this.error.set('La contraseña es obligatoria');
      return;
    }

    this.error.set(null);

    const dataToSend: Partial<Usuario> & { password?: string | null } = {
      username: this.form.value.username!,
      email: this.form.value.email!,
      nombre: this.form.value.nombre!,
      apellido: this.form.value.apellido!,
      rol: this.form.value.rol as 'FOTOGRAFO' | 'ADMIN' | 'SUPER_ADMIN',
      activo: this.form.value.activo ?? true,
    };

    if (this.form.value.password) {
      dataToSend.password = this.form.value.password;
    }

    const obs = this.isEditing
      ? this.service.actualizar(this.editandoId()!, dataToSend)
      : this.service.crear(dataToSend);

    obs.subscribe({
      next: () => {
        this.success.set(this.isEditing ? 'Usuario actualizado correctamente' : 'Usuario creado correctamente');
        this.resetForm();
        this.cargar();
        setTimeout(() => this.success.set(null), 3000);
      },
      error: (err) => {
        this.error.set(err.error?.error || err.error?.message || 'Error al guardar el usuario');
      },
    });
  }

  editar(user: Usuario) {
    this.editandoId.set(user.idUsuario);
    this.form.patchValue({
      username: user.username,
      email: user.email,
      nombre: user.nombre || '',
      apellido: user.apellido || '',
      password: '',
      rol: user.rol || 'FOTOGRAFO',
      activo: user.activo,
    });
  }

  eliminar(id: number) {
    if (confirm('¿Eliminar este usuario?')) {
      this.service.eliminar(id).subscribe(() => this.cargar());
    }
  }

  toggleEstado(user: Usuario) {
    this.editandoId.set(user.idUsuario);
    this.form.patchValue({
      username: user.username,
      email: user.email,
      nombre: user.nombre || '',
      apellido: user.apellido || '',
      password: '',
      rol: user.rol || 'FOTOGRAFO',
      activo: !user.activo,
    });
    this.guardar();
  }

  cancelar() {
    this.resetForm();
  }

  resetForm() {
    this.editandoId.set(null);
    this.form.reset({
      username: '',
      email: '',
      nombre: '',
      apellido: '',
      password: '',
      rol: 'FOTOGRAFO',
      activo: true,
    });
  }
}
