import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { forkJoin } from 'rxjs';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { EntrenamientoService } from '../../../core/services/entrenamiento.service';
import { Gimnasio } from '../../../core/models/gimnasio.model';

@Component({
  selector: 'app-dashboard-entrenador',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatSelectModule, MatFormFieldModule, MatSnackBarModule,
    LayoutComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardEntrenadorComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',       icon: 'dashboard',      ruta: '/entrenador/dashboard' },
    { label: 'Entrenamientos',  icon: 'fitness_center', ruta: '/entrenador/entrenamientos' },
    { label: 'Ejercicios',      icon: 'sports_gymnastics', ruta: '/entrenador/ejercicios' }
  ];

  misGimnasios: Gimnasio[] = [];
  gimnasioSeleccionado: Gimnasio | null = null;
  stats = { entrenamientos: 0, sesionesHoy: 0 };
  proximasSesiones: any[] = [];
  cargando = true;

  constructor(
    private gimnasioService: GimnasioService,
    public contexto: GimnasioContextService,
    private entrenamientoService: EntrenamientoService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.gimnasioService.misGimnasios().subscribe({
      next: gimnasios => {
        this.misGimnasios = gimnasios.filter(g => g.activo);

        const ctx = this.contexto.obtener();
        if (ctx && this.misGimnasios.find(g => g.id === ctx.id)) {
          this.gimnasioSeleccionado = ctx;
          this.cargarStats();
        } else if (this.misGimnasios.length === 1) {
          this.seleccionarGimnasio(this.misGimnasios[0]);
        } else {
          this.cargando = false;
        }
      },
      error: () => { this.snackBar.open('Error al cargar gimnasios', 'Cerrar', { duration: 3000 }); this.cargando = false; }
    });
  }

  seleccionarGimnasio(g: Gimnasio): void {
    this.gimnasioSeleccionado = g;
    this.contexto.seleccionar(g);
    this.cargarStats();
  }

  irA(ruta: string): void { this.router.navigate([ruta]); }

  private cargarStats(): void {
    if (!this.gimnasioSeleccionado) return;
    const id = this.gimnasioSeleccionado.id;
    this.cargando = true;

    forkJoin({
      entrenamientos: this.entrenamientoService.listarPorGimnasio(id),
      sesiones: this.entrenamientoService.listarSesionesGimnasio(id)
    }).subscribe({
      next: ({ entrenamientos, sesiones }) => {
        this.stats.entrenamientos = entrenamientos.length;
        const hoy = new Date().toDateString();
        this.stats.sesionesHoy = sesiones.filter(s => new Date(s.fechaHora).toDateString() === hoy).length;
        this.proximasSesiones = sesiones.slice(0, 5);
        this.cargando = false;
      },
      error: () => { this.snackBar.open('Error al cargar estadísticas', 'Cerrar', { duration: 3000 }); this.cargando = false; }
    });
  }
}
