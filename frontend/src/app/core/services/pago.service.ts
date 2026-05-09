import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pago, Factura, CrearSesionPagoRequest, SesionPagoResponse } from '../models/pago.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PagoService {

  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  crearSesionPago(request: CrearSesionPagoRequest): Observable<SesionPagoResponse> {
    return this.http.post<SesionPagoResponse>(`${this.base}/pagos/crear-sesion`, request);
  }

  listarPagosPorCliente(clienteId: number): Observable<Pago[]> {
    return this.http.get<Pago[]>(`${this.base}/pagos/cliente/${clienteId}`);
  }

  listarPagosPorGimnasio(gimnasioId: number): Observable<Pago[]> {
    return this.http.get<Pago[]>(`${this.base}/pagos/gimnasio/${gimnasioId}`);
  }

  obtenerFactura(pagoId: number): Observable<Factura> {
    return this.http.get<Factura>(`${this.base}/pagos/${pagoId}/factura`);
  }

  descargarFacturaPdf(pagoId: number): Observable<Blob> {
    return this.http.get(`${this.base}/pagos/${pagoId}/factura/pdf`, { responseType: 'blob' });
  }

  registrarPagoEfectivo(clienteSuscripcionId: number): Observable<Pago> {
    return this.http.post<Pago>(`${this.base}/pagos/efectivo`, { clienteSuscripcionId });
  }
}
