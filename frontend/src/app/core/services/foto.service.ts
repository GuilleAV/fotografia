import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Foto, FileUploadResponse, FotoUpdateRequest, FotoEstadoRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class FotoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/fotos`;

  // ============ PÚBLICOS ============

  listarPublicas(): Observable<Foto[]> {
    return this.http.get<Foto[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<Foto> {
    return this.http.get<Foto>(`${this.apiUrl}/${id}`);
  }

  listarPorCategoria(idCategoria: number): Observable<Foto[]> {
    return this.http.get<Foto[]>(`${this.apiUrl}/categoria/${idCategoria}`);
  }

  // ============ AUTENTICADOS ============

  listarMisFotos(): Observable<Foto[]> {
    return this.http.get<Foto[]>(`${this.apiUrl}/mis-fotos`);
  }

  subirFoto(archivo: File, titulo: string, idCategoria: number, descripcion?: string): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('archivo', archivo, archivo.name);
    formData.append('titulo', titulo);
    formData.append('idCategoria', idCategoria.toString());
    if (descripcion) {
      formData.append('descripcion', descripcion);
    }

    return this.http.post<FileUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  actualizar(id: number, data: FotoUpdateRequest): Observable<Foto> {
    return this.http.put<Foto>(`${this.apiUrl}/${id}`, data);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ============ ADMIN ============

  listarTodas(): Observable<Foto[]> {
    return this.http.get<Foto[]>(`${this.apiUrl}/admin/todas`);
  }

  listarPendientes(): Observable<Foto[]> {
    return this.http.get<Foto[]>(`${this.apiUrl}/admin/pendientes`);
  }

  cambiarEstado(id: number, data: FotoEstadoRequest): Observable<Foto> {
    return this.http.patch<Foto>(`${this.apiUrl}/${id}/estado`, data);
  }

  descargarOriginal(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/download`, { responseType: 'blob' });
  }
}
