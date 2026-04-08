export interface Usuario {
  idUsuario: number;
  username: string;
  email: string;
  nombre: string;
  apellido: string;
  nombreCompleto: string;
  rol: 'SUPER_ADMIN' | 'ADMIN' | 'FOTOGRAFO';
  activo: boolean;
  fotoPerfil?: string;
  cantidadFotos?: number;
  password?: string; // Para formularios de creación/edición
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  expiresAt: string;
  usuario: Usuario;
  mensaje: string;
}

export interface Foto {
  idFoto: number;
  titulo: string;
  descripcion?: string;
  nombreArchivo: string;
  rutaArchivo?: string;
  rutaThumbnail?: string;
  rutaWeb?: string;
  urlCompleta?: string;
  tamanioKb?: number;
  anchoPx?: number;
  altoPx?: number;
  destacada: boolean;
  orden?: number; // Para carousel
  activo: boolean;
  estado: 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';
  visitas: number;
  fechaSubida: string;
  fechaActualizacion?: string;
  idCategoria: number;
  categoriaNombre?: string;
  categoriaSlug?: string;
  categoriaColor?: string;
  categoriaIcono?: string;
  idUsuario?: number;
  usuarioNombre?: string;
  usuarioUsername?: string;
  etiquetas?: string[];
}

export interface Categoria {
  idCategoria: number;
  nombre: string;
  slug: string;
  descripcion?: string;
  icono?: string;
  color?: string;
  orden: number;
  activo: boolean;
}

export interface FileUploadResponse {
  idFoto: number;
  titulo: string;
  nombreArchivo: string;
  rutaOriginal: string;
  rutaThumbnail: string;
  rutaWeb: string;
  estado: string;
  mensaje: string;
  exitoso: boolean;
}

export interface FotoUpdateRequest {
  titulo?: string;
  descripcion?: string;
  idCategoria?: number;
  destacada?: boolean;
  orden?: number | null; // 1-5 para carousel, null para quitar
  activo?: boolean;
}

export interface FotoEstadoRequest {
  estado: 'APROBADA' | 'RECHAZADA';
  comentario?: string;
}

export interface ErrorResponse {
  error: string;
}
