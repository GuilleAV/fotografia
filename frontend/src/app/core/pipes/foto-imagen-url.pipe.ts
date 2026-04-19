import { Pipe, PipeTransform, inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

/**
 * Pipe para construir la URL de la imagen del backend.
 * Convierte datos de foto en URL del endpoint /fotos/{id}/imagen/{tipo}.
 * 
 * Uso en template:
 *   <img [src]="foto | fotoImagenUrl:'thumb'" />
 *   <img [src]="foto | fotoImagenUrl:'web'" />
 *   <img [src]="foto | fotoImagenUrl:'original'" />
 * 
 * Si la foto no está aprobada, agrega el token como query param
 * para que el backend verifique que el usuario es el dueño.
 */
@Pipe({
  name: 'fotoImagenUrl',
  standalone: true,
  pure: true,
})
export class FotoImagenUrlPipe implements PipeTransform {

  private baseUrl = environment.apiUrl;
  private auth = inject(AuthService);

  transform(
    foto: { idFoto?: number; rutaThumbnail?: string; rutaWeb?: string; rutaArchivo?: string; estado?: string; fechaActualizacion?: string } | null | undefined,
    tipo: 'thumb' | 'web' | 'original' = 'thumb',
    forzarToken = false,
  ): string | null {
    if (!foto?.idFoto) {
      return null;
    }

    // Resolver ruta con fallback para datos legacy
    const ruta = tipo === 'thumb'
      ? (foto.rutaThumbnail || foto.rutaWeb || foto.rutaArchivo)
      : tipo === 'web'
        ? (foto.rutaWeb || foto.rutaArchivo || foto.rutaThumbnail)
        : (foto.rutaArchivo || foto.rutaWeb || foto.rutaThumbnail);

    if (!ruta) {
      return null;
    }

    const url = `${this.baseUrl}/fotos/${foto.idFoto}/imagen/${tipo}`;
    const params = new URLSearchParams();

    // Si la foto no está aprobada, incluir token para verificar permisos
    if (forzarToken || foto.estado !== 'APROBADA') {
      const token = this.auth.getToken();
      if (token) {
        params.set('token', token);
      }
    }

    // Evita efectos de caché al cambiar estado/categoría en vistas admin
    if (forzarToken && foto.fechaActualizacion) {
      params.set('v', foto.fechaActualizacion);
    }

    const query = params.toString();
    return query ? `${url}?${query}` : url;
  }
}
