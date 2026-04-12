import { Component, OnInit, inject, signal, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CategoriaService } from '../../core/services/categoria.service';
import { Categoria } from '../../core/models';

@Component({
  selector: 'app-admin-categorias',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-categorias.component.html',
  styleUrls: ['./admin-categorias.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminCategoriasComponent implements OnInit {
  private service = inject(CategoriaService);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private readonly reservedSlugs = new Set([
    'about', 'contacto', 'contact', 'login', 'recuperar', 'reset-password',
    'dashboard', 'admin', 'usuarios', 'unauthorized', 'foto', 'api', 'auth'
  ]);

  categorias = signal<Categoria[]>([]);
  editandoId = signal<number | null>(null);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.group({
    nombre: ['', Validators.required],
    slug: ['', Validators.required],
    icono: ['fa-solid fa-camera'],
    color: ['#3498db'],
    orden: [0],
    activo: [true],
  });

  get isEditing() { return this.editandoId() !== null; }

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.service.listarTodas().subscribe({
      next: (cats) => this.categorias.set(cats),
      error: () => {},
    });
  }

  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.error.set(null);

    const slugNormalizado = this.normalizarSlug(this.form.value.slug || '');
    if (!slugNormalizado) {
      this.error.set('El slug es inválido');
      return;
    }

    if (this.reservedSlugs.has(slugNormalizado)) {
      this.error.set('Ese slug está reservado para rutas del sistema');
      return;
    }

    const data: Partial<Categoria> = {
      nombre: this.form.value.nombre!,
      slug: slugNormalizado,
      icono: this.form.value.icono || 'fa-solid fa-camera',
      color: this.form.value.color || '#3498db',
      orden: this.form.value.orden || 0,
      activo: this.form.value.activo ?? true,
    };

    const obs = this.isEditing
      ? this.service.actualizar(this.editandoId()!, data)
      : this.service.crear(data);

    obs.subscribe({
      next: () => {
        this.success.set(this.isEditing ? 'Categoría actualizada correctamente' : 'Categoría creada correctamente');
        this.resetForm();
        this.cargar();
        setTimeout(() => this.success.set(null), 3000);
      },
      error: (err) => {
        this.error.set(err.error?.error || err.error?.message || 'Error al guardar la categoría');
      },
    });
  }

  editar(cat: Categoria) {
    this.editandoId.set(cat.idCategoria);
    this.form.patchValue({
      nombre: cat.nombre,
      slug: cat.slug,
      icono: cat.icono || 'fa-solid fa-camera',
      color: cat.color || '#3498db',
      orden: cat.orden || 0,
      activo: cat.activo,
    });
  }

  eliminar(id: number) {
    if (confirm('¿Eliminar esta categoría?')) {
      this.service.eliminar(id).subscribe(() => this.cargar());
    }
  }

  toggleEstado(cat: Categoria) {
    this.editandoId.set(cat.idCategoria);
    this.form.patchValue({
      nombre: cat.nombre,
      slug: cat.slug,
      icono: cat.icono || 'fa-solid fa-camera',
      color: cat.color || '#3498db',
      orden: cat.orden || 0,
      activo: !cat.activo,
    });
    this.guardar();
  }

  cancelar() {
    this.resetForm();
  }

  resetForm() {
    this.editandoId.set(null);
    this.form.reset({
      nombre: '',
      slug: '',
      icono: 'fa-solid fa-camera',
      color: '#3498db',
      orden: 0,
      activo: true,
    });
    this.cdr.markForCheck();
  }

  private normalizarSlug(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .replace(/[\s,;]+/g, '-')
      .replace(/[^a-z0-9-]/g, '')
      .replace(/-{2,}/g, '-')
      .replace(/^-|-$/g, '');
  }
}
