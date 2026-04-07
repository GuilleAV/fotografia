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

  transform(foto: { idFoto?: number; rutaThumbnail?: string; rutaWeb?: string; rutaArchivo?: string; estado?: string } | null | undefined, tipo: 'thumb' | 'web' | 'original' = 'thumb'): string | null {
    if (!foto?.idFoto) {
      return null;
    }

    // Verificar que la ruta exista para el tipo solicitado
    const ruta = tipo === 'thumb' ? foto.rutaThumbnail
      : tipo === 'web' ? foto.rutaWeb
      : foto.rutaArchivo;

    if (!ruta) {
      return null;
    }

    let url = `${this.baseUrl}/fotos/${foto.idFoto}/imagen/${tipo}`;

    // Si la foto no está aprobada, incluir token para verificar permisos
    if (foto.estado !== 'APROBADA') {
      const token = this.auth.getToken();
      if (token) {
        url += `?token=${token}`;
      }
    }

    return url;
  }
}
