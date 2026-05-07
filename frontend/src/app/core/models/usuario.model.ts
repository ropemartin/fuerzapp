export type Rol = 'ADMIN_PLATAFORMA' | 'PROPIETARIO' | 'ENTRENADOR' | 'CLIENTE';

export interface Usuario {
  id: number;
  nombre: string;
  apellidos: string;
  email: string;
  telefono?: string;
  activo: boolean;
  fechaRegistro: string;
  rol: Rol;
  activoEnGimnasio?: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  token: string;
  email: string;
  nombre: string;
  rol: Rol;
}

export interface RegistroUsuarioRequest {
  nombre: string;
  apellidos: string;
  email: string;
  password: string;
  telefono?: string;
}

export interface PerfilUsuarioRequest {
  nombre: string;
  apellidos: string;
  email: string;
  telefono?: string;
}

export interface SuscripcionActivaInfo {
  tipoNombre: string;
  fechaFin: string;
}
