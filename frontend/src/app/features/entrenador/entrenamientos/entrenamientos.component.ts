import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { EntrenamientoService } from '../../../core/services/entrenamiento.service';
import { EjercicioService } from '../../../core/services/ejercicio.service';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { Entrenamiento, EntrenamientoRequest, Sesion, TipoEntrenamiento } from '../../../core/models/entrenamiento.model';
import { Ejercicio } from '../../../core/models/ejercicio.model';
import { Usuario } from '../../../core/models/usuario.model';
import { fechaFuturaValidator } from '../../../core/validators/app.validators';

@Component({
  selector: 'app-entrenamientos',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, FormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatTabsModule, MatExpansionModule, MatSnackBarModule,
    MatDividerModule, MatChipsModule, MatTooltipModule,
    MatDialogModule, LayoutComponent
  ],
  templateUrl: './entrenamientos.component.html',
  styleUrl: './entrenamientos.component.scss'
})
export class EntrenamientosComponent implements OnInit {

  private readonly defaultNavItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',         ruta: '/entrenador/dashboard' },
    { label: 'Entrenamientos', icon: 'fitness_center',    ruta: '/entrenador/entrenamientos' },
    { label: 'Ejercicios',     icon: 'sports_gymnastics', ruta: '/entrenador/ejercicios' }
  ];
  navItems: NavItem[] = this.defaultNavItems;

  // Lista
  entrenamientos: Entrenamiento[] = [];
  filtroTipo: TipoEntrenamiento | '' = '';
  tiposDisponibles: TipoEntrenamiento[] = ['GRUPAL', 'INDIVIDUAL', 'ESPECIFICO'];

  // Formulario nuevo entrenamiento
  formEntrenamiento: FormGroup;
  creandoEntrenamiento = false;

  // Entrenamiento seleccionado para gestionar
  seleccionado: Entrenamiento | null = null;
  ejerciciosDisponibles: Ejercicio[] = [];
  clientes: Usuario[] = [];

  // Formulario añadir ejercicio
  formEjercicio: FormGroup;

  // Formulario nueva sesión
  formSesion: FormGroup;
  sesiones: Sesion[] = [];

  // Formulario asignar cliente
  formAsignar: FormGroup;

  cargando = true;

  constructor(
    private fb: FormBuilder,
    private entrenamientoService: EntrenamientoService,
    private ejercicioService: EjercicioService,
    private gimnasioService: GimnasioService,
    public contexto: GimnasioContextService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute
  ) {
    this.formEntrenamiento = this.fb.group({
      nombre: ['', Validators.required],
      descripcion: [''],
      tipo: ['GRUPAL', Validators.required]
    });

    this.formEjercicio = this.fb.group({
      ejercicioId: [null, Validators.required],
      orden: [1, [Validators.required, Validators.min(1)]],
      series: [3, Validators.min(1)],
      repeticiones: [10, Validators.min(1)],
      duracionSegundos: [null, Validators.min(1)],
      descansoSegundos: [60, Validators.min(0)],
      notas: ['']
    });

    this.formSesion = this.fb.group({
      fechaHora: ['', [Validators.required, fechaFuturaValidator]],
      duracionMinutos: [60, Validators.min(1)],
      ubicacion: [''],
      capacidadMaxima: [null, Validators.min(1)]
    });

    this.formAsignar = this.fb.group({
      clienteId: [null, Validators.required],
      notas: ['']
    });
  }

  ngOnInit(): void {
    const routeNavItems = this.route.snapshot.data['navItems'];
    if (routeNavItems) this.navItems = routeNavItems;

    this.cargarEntrenamientos();
    const id = this.contexto.obtenerIdOFail();
    this.ejercicioService.listarPorGimnasio(id).subscribe(d => this.ejerciciosDisponibles = d);
    this.gimnasioService.listarClientes(id).subscribe(d => this.clientes = d);
  }

  // ─── Lista ─────────────────────────────────────────────────────────────────

  cargarEntrenamientos(): void {
    const id = this.contexto.obtenerIdOFail();
    this.cargando = true;
    const tipo = this.filtroTipo || undefined;
    this.entrenamientoService.listarPorGimnasio(id, tipo as TipoEntrenamiento).subscribe({
      next: d => { this.entrenamientos = d; this.cargando = false; },
      error: () => { this.snackBar.open('Error al cargar', 'Cerrar', { duration: 3000 }); this.cargando = false; }
    });
  }

  seleccionar(e: Entrenamiento): void {
    this.seleccionado = e;
    this.cargarSesiones(e.id);
    this.formEjercicio.patchValue({ orden: (e.ejercicios?.length ?? 0) + 1 });
  }

  // ─── Crear entrenamiento ───────────────────────────────────────────────────

  crearEntrenamiento(): void {
    if (this.formEntrenamiento.invalid) return;
    const id = this.contexto.obtenerIdOFail();
    this.entrenamientoService.crear(id, this.formEntrenamiento.value).subscribe({
      next: nuevo => {
        this.snackBar.open('Entrenamiento creado', 'Cerrar', { duration: 3000 });
        this.formEntrenamiento.reset({ tipo: 'GRUPAL' });
        this.creandoEntrenamiento = false;
        this.cargarEntrenamientos();
        this.seleccionar(nuevo);
      },
      error: () => this.snackBar.open('Error al crear', 'Cerrar', { duration: 3000 })
    });
  }

  eliminar(e: Entrenamiento): void {
    this.entrenamientoService.eliminar(e.id).subscribe({
      next: () => {
        this.snackBar.open('Entrenamiento eliminado', 'Cerrar', { duration: 3000 });
        if (this.seleccionado?.id === e.id) this.seleccionado = null;
        this.cargarEntrenamientos();
      }
    });
  }

  // ─── Ejercicios del entrenamiento ──────────────────────────────────────────

  agregarEjercicio(): void {
    if (!this.seleccionado || this.formEjercicio.invalid) return;
    this.entrenamientoService.agregarEjercicio(this.seleccionado.id, this.formEjercicio.value).subscribe({
      next: () => {
        this.snackBar.open('Ejercicio añadido', 'Cerrar', { duration: 3000 });
        this.formEjercicio.patchValue({ orden: (this.seleccionado!.ejercicios.length + 2), ejercicioId: null });
        this.recargarSeleccionado();
      },
      error: () => this.snackBar.open('Error al añadir ejercicio', 'Cerrar', { duration: 3000 })
    });
  }

  quitarEjercicio(eeId: number): void {
    if (!this.seleccionado) return;
    this.entrenamientoService.eliminarEjercicio(this.seleccionado.id, eeId).subscribe({
      next: () => { this.snackBar.open('Ejercicio eliminado', 'Cerrar', { duration: 3000 }); this.recargarSeleccionado(); }
    });
  }

  // ─── Sesiones ──────────────────────────────────────────────────────────────

  cargarSesiones(entrenamientoId: number): void {
    this.entrenamientoService.listarSesionesPorEntrenamiento(entrenamientoId)
      .subscribe(d => this.sesiones = d);
  }

  crearSesion(): void {
    if (!this.seleccionado || this.formSesion.invalid) return;
    const datos = { ...this.formSesion.value };
    // Si es INDIVIDUAL no tiene capacidad máxima
    if (this.seleccionado.tipo === 'INDIVIDUAL') datos.capacidadMaxima = null;

    this.entrenamientoService.crearSesion(this.seleccionado.id, datos).subscribe({
      next: () => {
        this.snackBar.open('Sesión creada', 'Cerrar', { duration: 3000 });
        this.formSesion.reset({ duracionMinutos: 60 });
        this.cargarSesiones(this.seleccionado!.id);
      },
      error: () => this.snackBar.open('Error al crear sesión', 'Cerrar', { duration: 3000 })
    });
  }

  eliminarSesion(sesionId: number): void {
    this.entrenamientoService.eliminarSesion(sesionId).subscribe({
      next: () => { this.snackBar.open('Sesión eliminada', 'Cerrar', { duration: 3000 }); this.cargarSesiones(this.seleccionado!.id); }
    });
  }

  asignarClienteASesion(sesionId: number): void {
    if (this.formAsignar.invalid) return;
    this.entrenamientoService.asignarClienteASesion(sesionId, this.formAsignar.value).subscribe({
      next: () => { this.snackBar.open('Cliente asignado', 'Cerrar', { duration: 3000 }); this.formAsignar.reset(); this.cargarSesiones(this.seleccionado!.id); },
      error: (err) => this.snackBar.open(err.error?.mensaje || 'Error al asignar', 'Cerrar', { duration: 3000 })
    });
  }

  asignarEspecifico(): void {
    if (!this.seleccionado || this.formAsignar.invalid) return;
    this.entrenamientoService.asignarEspecifico(this.seleccionado.id, this.formAsignar.value).subscribe({
      next: () => { this.snackBar.open('Entrenamiento asignado al cliente', 'Cerrar', { duration: 3000 }); this.formAsignar.reset(); },
      error: (err) => this.snackBar.open(err.error?.mensaje || 'Error al asignar', 'Cerrar', { duration: 3000 })
    });
  }

  private recargarSeleccionado(): void {
    if (!this.seleccionado) return;
    this.entrenamientoService.obtener(this.seleccionado.id)
      .subscribe(e => { this.seleccionado = e; });
  }

  nombreEjercicio(id: number): string {
    return this.ejerciciosDisponibles.find(e => e.id === id)?.nombre ?? '—';
  }
}
