import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { forkJoin } from 'rxjs';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { SuscripcionService } from '../../../core/services/suscripcion.service';
import { PagoService } from '../../../core/services/pago.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { ClienteSuscripcion, TipoSuscripcion, Extra } from '../../../core/models/suscripcion.model';
import { Pago } from '../../../core/models/pago.model';

@Component({
  selector: 'app-suscripcion-cliente',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSnackBarModule,
    MatTableModule,
    MatTooltipModule,
    LayoutComponent
  ],
  templateUrl: './suscripcion.component.html',
  styleUrl: './suscripcion.component.scss'
})
export class SuscripcionClienteComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',      ruta: '/cliente/dashboard' },
    { label: 'Entrenamientos', icon: 'fitness_center',  ruta: '/cliente/entrenamientos' },
    { label: 'Mi suscripción', icon: 'card_membership', ruta: '/cliente/suscripcion' }
  ];

  suscripcion: ClienteSuscripcion | null = null;
  tiposDisponibles: TipoSuscripcion[] = [];
  extrasDisponibles: Extra[] = [];
  pagos: Pago[] = [];
  cargando = true;
  pagando = false;
  pagoExitoso = false;
  columnasFactura = ['fecha', 'plan', 'importe', 'estado', 'factura'];

  constructor(
    private auth: AuthService,
    private suscripcionService: SuscripcionService,
    private pagoService: PagoService,
    public contexto: GimnasioContextService,
    private route: ActivatedRoute,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Detectar retorno de Stripe
    this.pagoExitoso = this.route.snapshot.queryParamMap.get('pago') === 'exito';

    this.cargar();
  }

  cargar(): void {
    const clienteId = this.auth.currentUser?.id;
    const gimnasioId = this.contexto.obtener()?.id;
    if (!clienteId || !gimnasioId) return;

    forkJoin({
      tipos: this.suscripcionService.listarTipos(gimnasioId),
      extras: this.suscripcionService.listarExtras(gimnasioId),
      pagos: this.pagoService.listarPagosPorCliente(clienteId)
    }).subscribe({
      next: ({ tipos, extras, pagos }) => {
        this.tiposDisponibles = tipos.filter(t => t.activo);
        this.extrasDisponibles = extras.filter(e => e.activo);
        this.pagos = pagos.sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime());
        this.cargando = false;
      },
      error: () => { this.snack.open('Error al cargar datos', 'Cerrar', { duration: 3000 }); this.cargando = false; }
    });

    this.suscripcionService.obtenerSuscripcionCliente(clienteId).subscribe({
      next: s => this.suscripcion = s,
      error: () => {} // puede no tener suscripción aún
    });
  }

  pagar(): void {
    if (!this.suscripcion) return;
    this.pagando = true;

    const successUrl = `${window.location.origin}/cliente/suscripcion?pago=exito`;
    const cancelUrl  = `${window.location.origin}/cliente/suscripcion?pago=cancelado`;

    this.pagoService.crearSesionPago({
      clienteSuscripcionId: this.suscripcion.id,
      successUrl,
      cancelUrl
    }).subscribe({
      next: res => {
        window.location.href = res.checkoutUrl;
      },
      error: () => {
        this.snack.open('Error al crear sesión de pago', 'Cerrar', { duration: 3000 });
        this.pagando = false;
      }
    });
  }

  agregarExtra(extraId: number): void {
    const clienteId = this.auth.currentUser?.id;
    if (!clienteId) return;

    this.suscripcionService.agregarExtra(clienteId, extraId).subscribe({
      next: updated => {
        this.suscripcion = updated;
        this.snack.open('Extra añadido', 'Cerrar', { duration: 2000 });
      },
      error: () => this.snack.open('Error al añadir extra', 'Cerrar', { duration: 3000 })
    });
  }

  tieneExtra(extraId: number): boolean {
    return this.suscripcion?.extras?.some(e => e.id === extraId) ?? false;
  }

  descargarFactura(pago: Pago): void {
    if (!pago.facturaId) return;
    this.pagoService.descargarFacturaPdf(pago.id).subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `factura-${pago.numeroFactura}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => this.snack.open('Error al descargar factura', 'Cerrar', { duration: 3000 })
    });
  }

  get diasRestantes(): number | null {
    if (!this.suscripcion?.fechaFin) return null;
    const diff = new Date(this.suscripcion.fechaFin).getTime() - Date.now();
    return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
  }
}
