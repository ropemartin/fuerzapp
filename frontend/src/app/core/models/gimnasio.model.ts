export interface Gimnasio {
  id: number;
  nombre: string;
  direccion?: string;
  ciudad?: string;
  telefono?: string;
  email?: string;
  logoUrl?: string;
  fechaAlta: string;
  activo: boolean;
  propietarioId?: number;
  propietarioNombre: string;
  propietarioEmail: string;
}

export interface GimnasioRequest {
  nombre: string;
  direccion?: string;
  ciudad?: string;
  telefono?: string;
  email?: string;
  logoUrl?: string;
  propietarioId?: number;
}

export interface AltaGimnasioRequest {
  gimnasio: GimnasioRequest;
  propietario: {
    nombre: string;
    apellidos: string;
    email: string;
    password: string;
    telefono?: string;
  };
}
