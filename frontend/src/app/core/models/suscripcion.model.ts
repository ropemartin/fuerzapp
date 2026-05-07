export type EstadoSuscripcion = 'ACTIVA' | 'VENCIDA' | 'CANCELADA' | 'PENDIENTE';

export interface TipoSuscripcion {
  id: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  duracionDias: number;
  activo: boolean;
  gimnasioId: number;
}

export interface TipoSuscripcionRequest {
  nombre: string;
  descripcion?: string;
  precio: number;
  duracionDias: number;
}

export interface Extra {
  id: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  activo: boolean;
  gimnasioId: number;
}

export interface ExtraRequest {
  nombre: string;
  descripcion?: string;
  precio: number;
}

export interface ClienteSuscripcion {
  id: number;
  clienteId: number;
  clienteNombre: string;
  clienteEmail: string;
  tipoSuscripcionId: number;
  tipoSuscripcionNombre: string;
  precio: number;
  fechaInicio: string;
  fechaFin: string;
  estado: EstadoSuscripcion;
  extras: Extra[];
}

export interface ClienteSuscripcionRequest {
  tipoSuscripcionId: number;
  fechaInicio: string;
}
