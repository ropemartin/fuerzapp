export type GrupoMuscular =
  | 'PECHO' | 'ESPALDA' | 'PIERNAS' | 'HOMBROS'
  | 'BICEPS' | 'TRICEPS' | 'ABDOMEN' | 'GLUTEOS'
  | 'CARDIO' | 'OTRO';

export type Dificultad = 'PRINCIPIANTE' | 'INTERMEDIO' | 'AVANZADO';

export interface Ejercicio {
  id: number;
  nombre: string;
  descripcion?: string;
  grupoMuscular: GrupoMuscular;
  dificultad: Dificultad;
  videoUrl?: string;
  imagenUrl?: string;
  esPredefinido: boolean;
  gimnasioId?: number;
  creadoPorNombre: string;
}

export interface EjercicioRequest {
  nombre: string;
  descripcion?: string;
  grupoMuscular: GrupoMuscular;
  dificultad: Dificultad;
  videoUrl?: string;
  imagenUrl?: string;
}
