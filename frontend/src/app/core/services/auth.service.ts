import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, Rol } from '../models/usuario.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'fuerzapp_token';
  private readonly USER_KEY  = 'fuerzapp_user';

  private usuarioSubject = new BehaviorSubject<LoginResponse | null>(this.cargarUsuario());
  usuario$ = this.usuarioSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  cambiarPassword(passwordActual: string, passwordNueva: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/cambiar-password`, { passwordActual, passwordNueva });
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap(response => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(response));
        this.usuarioSubject.next(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.usuarioSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUsuario(): LoginResponse | null {
    return this.usuarioSubject.value;
  }

  get currentUser(): LoginResponse | null {
    return this.usuarioSubject.value;
  }

  getRol(): Rol | null {
    return this.usuarioSubject.value?.rol ?? null;
  }

  estaAutenticado(): boolean {
    return !!this.getToken();
  }

  private cargarUsuario(): LoginResponse | null {
    const data = localStorage.getItem(this.USER_KEY);
    return data ? JSON.parse(data) : null;
  }
}
