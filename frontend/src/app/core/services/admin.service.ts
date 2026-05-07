import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';
import { environment } from '../../../environments/environment';

export interface UsuarioAdminRequest {
  nombre: string;
  apellidos: string;
  email: string;
  password?: string;
  telefono?: string;
  rol: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {

  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.base}/admin/usuarios`);
  }

  crearUsuario(request: UsuarioAdminRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.base}/admin/usuarios`, request);
  }

  actualizarUsuario(id: number, request: UsuarioAdminRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.base}/admin/usuarios/${id}`, request);
  }

  cambiarEstadoUsuario(id: number, activo: boolean): Observable<void> {
    return this.http.patch<void>(`${this.base}/admin/usuarios/${id}/estado?activo=${activo}`, {});
  }
}
