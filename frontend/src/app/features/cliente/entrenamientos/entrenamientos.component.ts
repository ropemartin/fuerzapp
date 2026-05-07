import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTooltipModule } from '@angular/material/tooltip';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { AuthService } from '../../../core/services/auth.service';
import { EntrenamientoService } from '../../../core/services/entrenamiento.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { Sesion, EntrenamientoEspecifico } from '../../../core/models/entrenamiento.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-entrenamientos-cliente',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatExpansionModule,
    MatTooltipModule,
    LayoutComponent
  ],
  templateUrl: './entrenamientos.component.html',
  styleUrl: './entrenamientos.component.scss'
})
export class EntrenamientosClienteComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',      ruta: '/cliente/dashboard' },
    { label: 'Entrenamientos', icon: 'fitness_center',  ruta: '/cliente/entrenamientos' },
    { label: 'Mi suscripción', icon: 'card_membership', ruta: '/cliente/suscripcion' }
  ];

  sesionesGrupales: Sesion[] = [];
  miasSesiones: Sesion[] = [];
  planesEspecificos: EntrenamientoEspecifico[] = [];
  cargando = true;

  constructor(
    private auth: AuthService,
    private entrenamientoService: EntrenamientoService,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    const clienteId = this.auth.currentUser?.id;
    if (!clienteId) return;

    forkJoin({
      sesiones: this.entrenamientoService.listarSesionesCliente(clienteId),
      especificos: this.entrenamientoService.listarEspecificosCliente(clienteId)
    }).subscribe({
      next: ({ sesiones, especificos }) => {
        this.miasSesiones = sesiones
          .filter(s => s.tipoEntrenamiento === 'INDIVIDUAL')
          .sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime());
        this.sesionesGrupales = sesiones
          .filter(s => s.tipoEntrenamiento === 'GRUPAL')
          .sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime());
        this.planesEspecificos = especificos
          .sort((a, b) => new Date(b.fechaAsignacion).getTime() - new Date(a.fechaAsignacion).getTime());
        this.cargando = false;
      },
      error: () => { this.snack.open('Error al cargar entrenamientos', 'Cerrar', { duration: 3000 }); this.cargando = false; }
    });
  }

  marcarCompletado(plan: EntrenamientoEspecifico): void {
    this.entrenamientoService.marcarCompletado(plan.id, !plan.completado).subscribe({
      next: updated => {
        plan.completado = updated.completado;
        this.snack.open(updated.completado ? 'Marcado como completado' : 'Marcado como pendiente', 'Cerrar', { duration: 2000 });
      },
      error: () => this.snack.open('Error al actualizar', 'Cerrar', { duration: 3000 })
    });
  }

  esProxima(fechaHora: string): boolean {
    return new Date(fechaHora) >= new Date();
  }
}
