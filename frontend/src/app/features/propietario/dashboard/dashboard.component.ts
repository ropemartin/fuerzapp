import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { AuthService } from '../../../core/services/auth.service';
import { Gimnasio, GimnasioRequest } from '../../../core/models/gimnasio.model';
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
    MatInputModule,
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
    { label: 'Suscripciones',  icon: 'card_membership', ruta: '/propietario/suscripciones' }
  ];

  misGimnasios: Gimnasio[] = [];
  gimnasioSeleccionado: Gimnasio | null = null;
  stats = { entrenadores: 0, clientes: 0 };
  cargando = true;
  editandoGimnasio = false;
  guardando = false;
  formGimnasio: GimnasioRequest = { nombre: '' };

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

  toggleEditarGimnasio(): void {
    if (!this.editandoGimnasio && this.gimnasioSeleccionado) {
      const g = this.gimnasioSeleccionado;
      this.formGimnasio = {
        nombre: g.nombre,
        direccion: g.direccion,
        ciudad: g.ciudad,
        telefono: g.telefono,
        email: g.email,
        logoUrl: g.logoUrl,
        porcentajeIva: g.porcentajeIva ?? 0
      };
    }
    this.editandoGimnasio = !this.editandoGimnasio;
  }

  guardarGimnasio(): void {
    if (!this.gimnasioSeleccionado || !this.formGimnasio.nombre?.trim()) return;
    this.guardando = true;

    this.gimnasioService.actualizarDatos(this.gimnasioSeleccionado.id, this.formGimnasio).subscribe({
      next: actualizado => {
        // Actualizar el gimnasio seleccionado y la lista
        const idx = this.misGimnasios.findIndex(g => g.id === actualizado.id);
        if (idx !== -1) this.misGimnasios[idx] = actualizado;
        this.gimnasioSeleccionado = actualizado;
        this.contexto.seleccionar(actualizado);
        this.editandoGimnasio = false;
        this.guardando = false;
        this.snackBar.open('Datos del gimnasio actualizados', 'Cerrar', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Error al guardar los cambios', 'Cerrar', { duration: 3000 });
        this.guardando = false;
      }
    });
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
