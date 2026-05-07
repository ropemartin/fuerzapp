import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Entrenamiento, EntrenamientoRequest,
  EntrenamientoEjercicio, EntrenamientoEjercicioRequest,
  Sesion, SesionRequest,
  EntrenamientoEspecifico, AsignarClienteRequest,
  TipoEntrenamiento
} from '../models/entrenamiento.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EntrenamientoService {

  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ─── Entrenamientos ───────────────────────────────────────────────────────

  listarPorGimnasio(gimnasioId: number, tipo?: TipoEntrenamiento): Observable<Entrenamiento[]> {
    let params = new HttpParams();
    if (tipo) params = params.set('tipo', tipo);
    return this.http.get<Entrenamiento[]>(`${this.base}/gimnasios/${gimnasioId}/entrenamientos`, { params });
  }

  obtener(id: number): Observable<Entrenamiento> {
    return this.http.get<Entrenamiento>(`${this.base}/entrenamientos/${id}`);
  }

  crear(gimnasioId: number, request: EntrenamientoRequest): Observable<Entrenamiento> {
    return this.http.post<Entrenamiento>(`${this.base}/gimnasios/${gimnasioId}/entrenamientos`, request);
  }

  actualizar(id: number, request: EntrenamientoRequest): Observable<Entrenamiento> {
    return this.http.put<Entrenamiento>(`${this.base}/entrenamientos/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/entrenamientos/${id}`);
  }

  // ─── Ejercicios del entrenamiento ─────────────────────────────────────────

  agregarEjercicio(entrenamientoId: number, request: EntrenamientoEjercicioRequest): Observable<EntrenamientoEjercicio> {
    return this.http.post<EntrenamientoEjercicio>(`${this.base}/entrenamientos/${entrenamientoId}/ejercicios`, request);
  }

  actualizarEjercicio(entrenamientoId: number, eeId: number, request: EntrenamientoEjercicioRequest): Observable<EntrenamientoEjercicio> {
    return this.http.put<EntrenamientoEjercicio>(`${this.base}/entrenamientos/${entrenamientoId}/ejercicios/${eeId}`, request);
  }

  eliminarEjercicio(entrenamientoId: number, eeId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/entrenamientos/${entrenamientoId}/ejercicios/${eeId}`);
  }

  // ─── Sesiones ─────────────────────────────────────────────────────────────

  listarSesionesGimnasio(gimnasioId: number): Observable<Sesion[]> {
    return this.http.get<Sesion[]>(`${this.base}/gimnasios/${gimnasioId}/sesiones`);
  }

  listarSesionesPorEntrenamiento(entrenamientoId: number): Observable<Sesion[]> {
    return this.http.get<Sesion[]>(`${this.base}/entrenamientos/${entrenamientoId}/sesiones`);
  }

  crearSesion(entrenamientoId: number, request: SesionRequest): Observable<Sesion> {
    return this.http.post<Sesion>(`${this.base}/entrenamientos/${entrenamientoId}/sesiones`, request);
  }

  actualizarSesion(sesionId: number, request: SesionRequest): Observable<Sesion> {
    return this.http.put<Sesion>(`${this.base}/sesiones/${sesionId}`, request);
  }

  eliminarSesion(sesionId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/sesiones/${sesionId}`);
  }

  asignarClienteASesion(sesionId: number, request: AsignarClienteRequest): Observable<Sesion> {
    return this.http.post<Sesion>(`${this.base}/sesiones/${sesionId}/asignar-cliente`, request);
  }

  // ─── Inscripción cliente (GRUPAL) ─────────────────────────────────────────

  inscribirse(sesionId: number): Observable<void> {
    return this.http.post<void>(`${this.base}/sesiones/${sesionId}/inscripcion`, {});
  }

  cancelarInscripcion(sesionId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/sesiones/${sesionId}/inscripcion`);
  }

  // ─── Específicos ──────────────────────────────────────────────────────────

  asignarEspecifico(entrenamientoId: number, request: AsignarClienteRequest): Observable<EntrenamientoEspecifico> {
    return this.http.post<EntrenamientoEspecifico>(`${this.base}/entrenamientos/${entrenamientoId}/asignar-cliente`, request);
  }

  listarSesionesCliente(clienteId: number): Observable<Sesion[]> {
    return this.http.get<Sesion[]>(`${this.base}/clientes/${clienteId}/sesiones`);
  }

  listarEspecificosCliente(clienteId: number): Observable<EntrenamientoEspecifico[]> {
    return this.http.get<EntrenamientoEspecifico[]>(`${this.base}/clientes/${clienteId}/entrenamientos-especificos`);
  }

  marcarCompletado(especificoId: number, completado: boolean): Observable<EntrenamientoEspecifico> {
    return this.http.patch<EntrenamientoEspecifico>(
      `${this.base}/entrenamientos-especificos/${especificoId}/completado?completado=${completado}`, {}
    );
  }
}
