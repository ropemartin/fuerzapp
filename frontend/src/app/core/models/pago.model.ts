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

export interface FacturaLinea {
  descripcion: string;
  precioUnitario: number;
  cantidad: number;
  subtotal: number;
}

export interface Factura {
  id: number;
  pagoId: number;
  numeroFactura: string;
  fechaEmision: string;

  clienteNombre: string;
  clienteEmail: string;
  tipoSuscripcionNombre: string;

  gimnasioNombre: string;
  gimnasioDireccion?: string;
  gimnasioTelefono?: string;
  gimnasioEmail?: string;
  gimnasioLogoUrl?: string;

  baseImponible: number;
  porcentajeIva: number;
  importeIva: number;
  importeTotal: number;

  lineas: FacturaLinea[];
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
