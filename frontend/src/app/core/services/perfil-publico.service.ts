import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PerfilPublico } from '../models';

@Injectable({ providedIn: 'root' })
export class PerfilPublicoService {
  private http = inject(HttpClient);
  private perfilPublicoUrl = `${environment.apiUrl}/usuarios/publico/perfil`;
  private perfilAdminUrl = `${environment.apiUrl}/usuarios/admin/perfil-publico`;

  obtenerPerfil(): Observable<PerfilPublico> {
    return this.http.get<PerfilPublico>(this.perfilPublicoUrl);
  }

  actualizarPerfil(data: Partial<PerfilPublico>): Observable<PerfilPublico> {
    return this.http.put<PerfilPublico>(this.perfilAdminUrl, data);
  }
}
