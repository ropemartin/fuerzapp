import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { forkJoin } from 'rxjs';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { SuscripcionService } from '../../../core/services/suscripcion.service';
import { EntrenamientoService } from '../../../core/services/entrenamiento.service';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { ClienteSuscripcion } from '../../../core/models/suscripcion.model';
import { Sesion, EntrenamientoEspecifico } from '../../../core/models/entrenamiento.model';

@Component({
  selector: 'app-dashboard-cliente',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    LayoutComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardClienteComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',      ruta: '/cliente/dashboard' },
    { label: 'Entrenamientos', icon: 'fitness_center',  ruta: '/cliente/entrenamientos' },
    { label: 'Mi suscripción', icon: 'card_membership', ruta: '/cliente/suscripcion' }
  ];

  suscripcion: ClienteSuscripcion | null = null;
  proximasSesiones: Sesion[] = [];
  planesEspecificos: EntrenamientoEspecifico[] = [];
  cargando = true;

  constructor(
    private auth: AuthService,
    private suscripcionService: SuscripcionService,
    private entrenamientoService: EntrenamientoService,
    private gimnasioService: GimnasioService,
    private contexto: GimnasioContextService
  ) {}

  ngOnInit(): void {
    const clienteId = this.auth.currentUser?.id;
    if (!clienteId) return;

    // Si no hay gimnasio en contexto, cargamos el del cliente automáticamente
    if (!this.contexto.obtener()) {
      this.gimnasioService.misGimnasios().subscribe({
        next: gimnasios => { if (gimnasios[0]) this.contexto.seleccionar(gimnasios[0]); }
      });
    }

    forkJoin({
      suscripcion: this.suscripcionService.obtenerSuscripcionCliente(clienteId),
      sesiones: this.entrenamientoService.listarSesionesCliente(clienteId),
      especificos: this.entrenamientoService.listarEspecificosCliente(clienteId)
    }).subscribe({
      next: ({ suscripcion, sesiones, especificos }) => {
        this.suscripcion = suscripcion;
        this.proximasSesiones = sesiones
          .filter(s => new Date(s.fechaHora) >= new Date())
          .sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime())
          .slice(0, 5);
        this.planesEspecificos = especificos.filter(e => !e.completado);
        this.cargando = false;
      },
      error: () => { this.cargando = false; }
    });
  }

  get diasRestantes(): number | null {
    if (!this.suscripcion?.fechaFin) return null;
    const diff = new Date(this.suscripcion.fechaFin).getTime() - Date.now();
    return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
  }
}
