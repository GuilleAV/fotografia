import { Pipe, PipeTransform } from '@angular/core';

/**
 * Convierte un string de clase Font Awesome en un elemento <i> seguro.
 * Ej: "fa-solid fa-leaf" → <i class="fa-solid fa-leaf"></i>
 */
@Pipe({
  name: 'iconoClase',
  standalone: true,
  pure: true,
})
export class IconoClasePipe implements PipeTransform {
  transform(icono: string | null | undefined, fallback: string = 'fa-solid fa-camera'): string {
    if (!icono) return fallback;
    // Si ya tiene prefijo fa-, usarlo tal cual
    if (icono.includes('fa-')) return icono;
    // Si es un emoji o texto simple, devolver fallback
    return fallback;
  }
}
