export type TipoEntrenamiento = 'GRUPAL' | 'INDIVIDUAL' | 'ESPECIFICO';

export interface EntrenamientoEjercicio {
  id: number;
  orden: number;
  ejercicioId: number;
  ejercicioNombre: string;
  ejercicioDescripcion?: string;
  grupoMuscular: string;
  dificultad: string;
  imagenUrl?: string;
  videoUrl?: string;
  series?: number;
  repeticiones?: number;
  duracionSegundos?: number;
  descansoSegundos?: number;
  notas?: string;
}

export interface EntrenamientoEjercicioRequest {
  ejercicioId: number;
  orden: number;
  series?: number;
  repeticiones?: number;
  duracionSegundos?: number;
  descansoSegundos?: number;
  notas?: string;
}

export interface Entrenamiento {
  id: number;
  nombre: string;
  descripcion?: string;
  tipo: TipoEntrenamiento;
  fechaCreacion: string;
  activo: boolean;
  gimnasioId: number;
  entrenadorId?: number;
  entrenadorNombre: string;
  ejercicios: EntrenamientoEjercicio[];
}

export interface EntrenamientoRequest {
  nombre: string;
  descripcion?: string;
  tipo: TipoEntrenamiento;
}

export interface Sesion {
  id: number;
  entrenamientoId: number;
  entrenamientoNombre: string;
  tipoEntrenamiento: TipoEntrenamiento;
  entrenadorNombre: string;
  fechaHora: string;
  duracionMinutos?: number;
  ubicacion?: string;
  capacidadMaxima?: number;
  plazasOcupadas: number;
  ejercicios: EntrenamientoEjercicio[];
  clientesInscritos: any[];
}

export interface SesionRequest {
  fechaHora: string;
  duracionMinutos?: number;
  ubicacion?: string;
  capacidadMaxima?: number;
}

export interface EntrenamientoEspecifico {
  id: number;
  entrenamientoId: number;
  entrenamientoNombre: string;
  entrenadorNombre: string;
  clienteId: number;
  clienteNombre: string;
  fechaAsignacion: string;
  notas?: string;
  completado: boolean;
  ejercicios: EntrenamientoEjercicio[];
}

export interface AsignarClienteRequest {
  clienteId: number;
  notas?: string;
}
