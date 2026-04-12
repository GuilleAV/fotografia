import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PerfilPublicoService } from '../../core/services/perfil-publico.service';

@Component({
  selector: 'app-admin-perfil-publico',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-perfil-publico.component.html',
  styleUrls: ['./admin-perfil-publico.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminPerfilPublicoComponent implements OnInit {
  private perfilService = inject(PerfilPublicoService);
  private fb = inject(FormBuilder);

  loading = signal(true);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.group({
    nombreMarca: ['', [Validators.required, Validators.minLength(2)]],
    emailContacto: ['', [Validators.required, Validators.email]],
    fotoPerfil: [''],
    socialInstagram: [''],
    socialYoutube: [''],
    socialThreads: [''],
  });

  ngOnInit() {
    this.cargarPerfil();
  }

  cargarPerfil() {
    this.loading.set(true);
    this.error.set(null);

    this.perfilService.obtenerPerfil().subscribe({
      next: (perfil) => {
        this.form.patchValue({
          nombreMarca: perfil.nombreMarca || perfil.nombreCompleto || '',
          emailContacto: perfil.emailContacto || '',
          fotoPerfil: perfil.fotoPerfil || '',
          socialInstagram: perfil.socialInstagram || '',
          socialYoutube: perfil.socialYoutube || '',
          socialThreads: perfil.socialThreads || '',
        });
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el perfil público');
        this.loading.set(false);
      },
    });
  }

  guardar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.error.set(null);
    this.success.set(null);

    this.perfilService.actualizarPerfil({
      nombreMarca: this.form.value.nombreMarca || '',
      emailContacto: this.form.value.emailContacto || '',
      fotoPerfil: this.form.value.fotoPerfil || '',
      socialInstagram: this.form.value.socialInstagram || '',
      socialYoutube: this.form.value.socialYoutube || '',
      socialThreads: this.form.value.socialThreads || '',
    }).subscribe({
      next: (perfil) => {
        this.form.patchValue({
          nombreMarca: perfil.nombreMarca || '',
          emailContacto: perfil.emailContacto || '',
          fotoPerfil: perfil.fotoPerfil || '',
          socialInstagram: perfil.socialInstagram || '',
          socialYoutube: perfil.socialYoutube || '',
          socialThreads: perfil.socialThreads || '',
        });
        this.success.set('Perfil público actualizado correctamente');
        setTimeout(() => this.success.set(null), 3000);
      },
      error: (err) => {
        this.error.set(err.error || 'No se pudo actualizar el perfil público');
      },
    });
  }
}
