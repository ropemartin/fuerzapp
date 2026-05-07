import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Gimnasio } from '../models/gimnasio.model';

@Injectable({ providedIn: 'root' })
export class GimnasioContextService {

  private readonly KEY = 'fuerzapp_gimnasio';
  private gimnasioSubject = new BehaviorSubject<Gimnasio | null>(this.cargar());

  gimnasio$ = this.gimnasioSubject.asObservable();

  seleccionar(gimnasio: Gimnasio): void {
    localStorage.setItem(this.KEY, JSON.stringify(gimnasio));
    this.gimnasioSubject.next(gimnasio);
  }

  obtener(): Gimnasio | null {
    return this.gimnasioSubject.value;
  }

  obtenerIdOFail(): number {
    const g = this.obtener();
    if (!g) throw new Error('No hay gimnasio seleccionado');
    return g.id;
  }

  limpiar(): void {
    localStorage.removeItem(this.KEY);
    this.gimnasioSubject.next(null);
  }

  private cargar(): Gimnasio | null {
    const data = localStorage.getItem(this.KEY);
    return data ? JSON.parse(data) : null;
  }
}
