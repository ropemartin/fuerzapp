import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ejercicio, EjercicioRequest, GrupoMuscular } from '../models/ejercicio.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EjercicioService {

  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarPorGimnasio(gimnasioId: number, grupoMuscular?: GrupoMuscular): Observable<Ejercicio[]> {
    let params = new HttpParams();
    if (grupoMuscular) params = params.set('grupoMuscular', grupoMuscular);
    return this.http.get<Ejercicio[]>(`${this.base}/gimnasios/${gimnasioId}/ejercicios`, { params });
  }

  crear(gimnasioId: number, request: EjercicioRequest): Observable<Ejercicio> {
    return this.http.post<Ejercicio>(`${this.base}/gimnasios/${gimnasioId}/ejercicios`, request);
  }

  actualizar(id: number, request: EjercicioRequest): Observable<Ejercicio> {
    return this.http.put<Ejercicio>(`${this.base}/ejercicios/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/ejercicios/${id}`);
  }
}
