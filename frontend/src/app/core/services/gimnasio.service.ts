import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Gimnasio, GimnasioRequest, AltaGimnasioRequest } from '../models/gimnasio.model';
import { Usuario, RegistroUsuarioRequest, PerfilUsuarioRequest, SuscripcionActivaInfo } from '../models/usuario.model';
import { Entrenamiento } from '../models/entrenamiento.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GimnasioService {

  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ─── Mis gimnasios (propietario o entrenador autenticado) ────────────────

  misGimnasios(): Observable<Gimnasio[]> {
    return this.http.get<Gimnasio[]>(`${this.base}/gimnasios/mis-gimnasios`);
  }

  // ─── Admin ────────────────────────────────────────────────────────────────

  listarTodos(): Observable<Gimnasio[]> {
    return this.http.get<Gimnasio[]>(`${this.base}/admin/gimnasios`);
  }

  crear(request: AltaGimnasioRequest): Observable<Gimnasio> {
    return this.http.post<Gimnasio>(`${this.base}/admin/gimnasios`, request);
  }

  actualizar(id: number, request: GimnasioRequest): Observable<Gimnasio> {
    return this.http.put<Gimnasio>(`${this.base}/admin/gimnasios/${id}`, request);
  }

  cambiarEstado(id: number, activo: boolean): Observable<void> {
    return this.http.patch<void>(`${this.base}/admin/gimnasios/${id}/estado?activo=${activo}`, {});
  }

  // ─── Detalle ──────────────────────────────────────────────────────────────

  obtener(id: number): Observable<Gimnasio> {
    return this.http.get<Gimnasio>(`${this.base}/gimnasios/${id}`);
  }

  // ─── Entrenadores ─────────────────────────────────────────────────────────

  listarEntrenadores(gimnasioId: number): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.base}/gimnasios/${gimnasioId}/entrenadores`);
  }

  altaEntrenador(gimnasioId: number, request: RegistroUsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.base}/gimnasios/${gimnasioId}/entrenadores`, request);
  }

  bajaEntrenador(gimnasioId: number, entrenadorId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/gimnasios/${gimnasioId}/entrenadores/${entrenadorId}`);
  }

  reactivarEntrenador(gimnasioId: number, entrenadorId: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/gimnasios/${gimnasioId}/entrenadores/${entrenadorId}/reactivar`, {});
  }

  listarEntrenamientosDeEntrenador(gimnasioId: number, entrenadorId: number): Observable<Entrenamiento[]> {
    return this.http.get<Entrenamiento[]>(`${this.base}/gimnasios/${gimnasioId}/entrenadores/${entrenadorId}/entrenamientos`);
  }

  reasignarEntrenador(entrenamientoId: number, nuevoEntrenadorId: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/entrenamientos/${entrenamientoId}/entrenador?nuevoEntrenadorId=${nuevoEntrenadorId}`, {});
  }

  // ─── Clientes ─────────────────────────────────────────────────────────────

  listarClientes(gimnasioId: number): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.base}/gimnasios/${gimnasioId}/clientes`);
  }

  altaCliente(gimnasioId: number, request: RegistroUsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.base}/gimnasios/${gimnasioId}/clientes`, request);
  }

  bajaCliente(gimnasioId: number, clienteId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/gimnasios/${gimnasioId}/clientes/${clienteId}`);
  }

  reactivarCliente(gimnasioId: number, clienteId: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/gimnasios/${gimnasioId}/clientes/${clienteId}/reactivar`, {});
  }

  suscripcionActivaCliente(gimnasioId: number, clienteId: number): Observable<SuscripcionActivaInfo | null> {
    return this.http.get<SuscripcionActivaInfo | null>(`${this.base}/gimnasios/${gimnasioId}/clientes/${clienteId}/suscripcion-activa`);
  }

  actualizarPerfilEntrenador(gimnasioId: number, entrenadorId: number, request: PerfilUsuarioRequest): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.base}/gimnasios/${gimnasioId}/entrenadores/${entrenadorId}/perfil`, request);
  }

  actualizarPerfilCliente(gimnasioId: number, clienteId: number, request: PerfilUsuarioRequest): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.base}/gimnasios/${gimnasioId}/clientes/${clienteId}/perfil`, request);
  }
}
