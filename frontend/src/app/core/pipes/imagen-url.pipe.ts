import { Pipe, PipeTransform } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Pipe para construir la URL de la imagen del backend.
 * Convierte ruta de archivo (ej: "1775598861544_c775cc4e_thumb.jpg") en URL completa.
 * 
 * Uso en template:
 *   {{ foto.rutaThumbnail | imagenUrl:'thumb' }}
 *   {{ foto.rutaWeb | imagenUrl:'web' }}
 *   {{ foto.rutaArchivo | imagenUrl:'original' }}
 * 
 * Output: http://localhost:8080/portfolio-backend/api/fotos/42/imagen/thumb
 */
@Pipe({
  name: 'imagenUrl',
  standalone: true,
  pure: true,
})
export class ImagenUrlPipe implements PipeTransform {

  private baseUrl = environment.apiUrl;

  transform(rutaArchivo: string | undefined | null, tipo: 'thumb' | 'web' | 'original' = 'thumb', idFoto?: number): string | null {
    if (!rutaArchivo) {
      return null;
    }

    // Si tenemos el ID de la foto, usamos el endpoint nuevo
    if (idFoto) {
      return `${this.baseUrl}/fotos/${idFoto}/imagen/${tipo}`;
    }

    // Fallback: construcción manual (para datos antiguos)
    return `${this.baseUrl}/fotos/imagen/${rutaArchivo}`;
  }
}