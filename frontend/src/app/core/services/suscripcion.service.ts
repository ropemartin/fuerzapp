import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  TipoSuscripcion, TipoSuscripcionRequest,
  Extra, ExtraRequest,
  ClienteSuscripcion, ClienteSuscripcionRequest
} from '../models/suscripcion.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SuscripcionService {

  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ─── Tipos de suscripción ─────────────────────────────────────────────────

  listarTipos(gimnasioId: number): Observable<TipoSuscripcion[]> {
    return this.http.get<TipoSuscripcion[]>(`${this.base}/gimnasios/${gimnasioId}/suscripciones`);
  }

  crearTipo(gimnasioId: number, request: TipoSuscripcionRequest): Observable<TipoSuscripcion> {
    return this.http.post<TipoSuscripcion>(`${this.base}/gimnasios/${gimnasioId}/suscripciones`, request);
  }

  actualizarTipo(gimnasioId: number, tipoId: number, request: TipoSuscripcionRequest): Observable<TipoSuscripcion> {
    return this.http.put<TipoSuscripcion>(`${this.base}/gimnasios/${gimnasioId}/suscripciones/${tipoId}`, request);
  }

  cambiarEstadoTipo(gimnasioId: number, tipoId: number, activo: boolean): Observable<void> {
    return this.http.patch<void>(
      `${this.base}/gimnasios/${gimnasioId}/suscripciones/${tipoId}/estado?activo=${activo}`, {}
    );
  }

  // ─── Extras ──────────────────────────────────────────────────────────────

  listarExtras(gimnasioId: number): Observable<Extra[]> {
    return this.http.get<Extra[]>(`${this.base}/gimnasios/${gimnasioId}/extras`);
  }

  crearExtra(gimnasioId: number, request: ExtraRequest): Observable<Extra> {
    return this.http.post<Extra>(`${this.base}/gimnasios/${gimnasioId}/extras`, request);
  }

  actualizarExtra(gimnasioId: number, extraId: number, request: ExtraRequest): Observable<Extra> {
    return this.http.put<Extra>(`${this.base}/gimnasios/${gimnasioId}/extras/${extraId}`, request);
  }

  // ─── Suscripciones de clientes ───────────────────────────────────────────

  listarSuscriptores(gimnasioId: number): Observable<ClienteSuscripcion[]> {
    return this.http.get<ClienteSuscripcion[]>(`${this.base}/gimnasios/${gimnasioId}/suscriptores`);
  }

  obtenerSuscripcionCliente(clienteId: number): Observable<ClienteSuscripcion> {
    return this.http.get<ClienteSuscripcion>(`${this.base}/clientes/${clienteId}/suscripcion`);
  }

  asignarSuscripcion(clienteId: number, request: ClienteSuscripcionRequest): Observable<ClienteSuscripcion> {
    return this.http.post<ClienteSuscripcion>(`${this.base}/clientes/${clienteId}/suscripcion`, request);
  }

  agregarExtra(clienteId: number, extraId: number): Observable<ClienteSuscripcion> {
    return this.http.post<ClienteSuscripcion>(
      `${this.base}/clientes/${clienteId}/suscripcion/extras/${extraId}`, {}
    );
  }
}
