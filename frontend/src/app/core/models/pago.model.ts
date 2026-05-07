export type EstadoPago = 'COMPLETADO' | 'PENDIENTE' | 'FALLIDO' | 'REEMBOLSADO';

export interface Pago {
  id: number;
  clienteSuscripcionId: number;
  clienteNombre: string;
  clienteEmail: string;
  tipoSuscripcionNombre: string;
  importe: number;
  fecha: string;
  estado: EstadoPago;
  facturaId?: number;
  numeroFactura?: string;
}

export interface Factura {
  id: number;
  pagoId: number;
  numeroFactura: string;
  fechaEmision: string;
  importeTotal: number;
  clienteNombre: string;
  clienteEmail: string;
  tipoSuscripcionNombre: string;
  gimnasioNombre: string;
}

export interface CrearSesionPagoRequest {
  clienteSuscripcionId: number;
  successUrl: string;
  cancelUrl: string;
}

export interface SesionPagoResponse {
  checkoutUrl: string;
  sessionId: string;
}
