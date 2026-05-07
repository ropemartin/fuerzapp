import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { AuthService } from '../../../core/services/auth.service';
import { Gimnasio } from '../../../core/models/gimnasio.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard-propietario',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatDividerModule,
    MatSnackBarModule,
    LayoutComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardPropietarioComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',       ruta: '/propietario/dashboard' },
    { label: 'Entrenadores',   icon: 'sports',          ruta: '/propietario/entrenadores' },
    { label: 'Clientes',       icon: 'group',           ruta: '/propietario/clientes' },
    { label: 'Suscripciones',  icon: 'card_membership', ruta: '/propietario/suscripciones' },
    { label: 'Entrenamientos', icon: 'fitness_center',  ruta: '/propietario/entrenamientos' },
    { label: 'Configuración',  icon: 'settings',        ruta: '/propietario/configuracion' }
  ];

  misGimnasios: Gimnasio[] = [];
  gimnasioSeleccionado: Gimnasio | null = null;
  stats = { entrenadores: 0, clientes: 0 };
  cargando = true;

  constructor(
    private gimnasioService: GimnasioService,
    public contexto: GimnasioContextService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.gimnasioService.misGimnasios().subscribe({
      next: todos => {
        this.misGimnasios = todos.filter(g => g.activo);

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
      error: () => {
        this.snackBar.open('Error al cargar los gimnasios', 'Cerrar', { duration: 3000 });
        this.cargando = false;
      }
    });
  }

  seleccionarGimnasio(gimnasio: Gimnasio): void {
    this.gimnasioSeleccionado = gimnasio;
    this.contexto.seleccionar(gimnasio);
    this.cargarStats();
  }

  irA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  private cargarStats(): void {
    if (!this.gimnasioSeleccionado) return;
    const id = this.gimnasioSeleccionado.id;
    this.cargando = true;

    forkJoin({
      entrenadores: this.gimnasioService.listarEntrenadores(id),
      clientes: this.gimnasioService.listarClientes(id)
    }).subscribe({
      next: ({ entrenadores, clientes }) => {
        this.stats.entrenadores = entrenadores.length;
        this.stats.clientes = clientes.length;
        this.cargando = false;
      },
      error: () => {
        this.snackBar.open('Error al cargar estadísticas', 'Cerrar', { duration: 3000 });
        this.cargando = false;
      }
    });
  }
}
