import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { SuscripcionService } from '../../../core/services/suscripcion.service';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { PagoService } from '../../../core/services/pago.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { TipoSuscripcion, Extra, ClienteSuscripcion } from '../../../core/models/suscripcion.model';
import { Usuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-suscripciones',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatCardModule,
    MatTooltipModule,
    MatChipsModule,
    LayoutComponent
  ],
  templateUrl: './suscripciones.component.html',
  styleUrl: './suscripciones.component.scss'
})
export class SuscripcionesComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',       ruta: '/propietario/dashboard' },
    { label: 'Entrenadores',   icon: 'sports',          ruta: '/propietario/entrenadores' },
    { label: 'Clientes',       icon: 'group',           ruta: '/propietario/clientes' },
    { label: 'Suscripciones',  icon: 'card_membership', ruta: '/propietario/suscripciones' },
    { label: 'Entrenamientos', icon: 'fitness_center',  ruta: '/propietario/entrenamientos' },
    { label: 'Configuración',  icon: 'settings',        ruta: '/propietario/configuracion' }
  ];

  // Tipos de suscripción
  tipos: TipoSuscripcion[] = [];
  columnasTipos = ['nombre', 'precio', 'duracion', 'acciones'];
  formTipo: FormGroup;
  editandoTipo: TipoSuscripcion | null = null;

  // Extras
  extras: Extra[] = [];
  columnasExtras = ['nombre', 'precio', 'acciones'];
  formExtra: FormGroup;
  editandoExtra: Extra | null = null;

  // Suscriptores
  suscriptores: ClienteSuscripcion[] = [];
  columnasSuscriptores = ['cliente', 'tipo', 'fechas', 'estado', 'extras', 'acciones'];

  // Asignar suscripción
  clientes: Usuario[] = [];
  formAsignar: FormGroup;
  asignando = false;

  cargando = true;

  constructor(
    private fb: FormBuilder,
    private suscripcionService: SuscripcionService,
    private gimnasioService: GimnasioService,
    private pagoService: PagoService,
    public contexto: GimnasioContextService,
    private snackBar: MatSnackBar
  ) {
    this.formTipo = this.fb.group({
      nombre:       ['', Validators.required],
      descripcion:  [''],
      precio:       [null, [Validators.required, Validators.min(0.01)]],
      duracionDias: [30,   [Validators.required, Validators.min(1)]]
    });

    this.formExtra = this.fb.group({
      nombre:      ['', Validators.required],
      descripcion: [''],
      precio:      [null, [Validators.required, Validators.min(0.01)]]
    });

    this.formAsignar = this.fb.group({
      clienteId:         [null, Validators.required],
      tipoSuscripcionId: [null, Validators.required],
      fechaInicio:       [new Date().toISOString().slice(0, 10), Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    const id = this.contexto.obtenerIdOFail();
    this.cargando = true;

    this.suscripcionService.listarTipos(id).subscribe(d => { this.tipos = d; this.cargando = false; });
    this.suscripcionService.listarExtras(id).subscribe(d => this.extras = d);
    this.suscripcionService.listarSuscriptores(id).subscribe(d => this.suscriptores = d);
    this.gimnasioService.listarClientes(id).subscribe(d => this.clientes = d);
  }

  // ─── Tipos ────────────────────────────────────────────────────────────────

  guardarTipo(): void {
    if (this.formTipo.invalid) return;
    const id = this.contexto.obtenerIdOFail();
    const op = this.editandoTipo
      ? this.suscripcionService.actualizarTipo(id, this.editandoTipo.id, this.formTipo.value)
      : this.suscripcionService.crearTipo(id, this.formTipo.value);

    op.subscribe({
      next: () => {
        this.snackBar.open('Tipo de suscripción guardado', 'Cerrar', { duration: 3000 });
        this.formTipo.reset({ duracionDias: 30 });
        this.editandoTipo = null;
        this.suscripcionService.listarTipos(id).subscribe(d => this.tipos = d);
      },
      error: () => this.snackBar.open('Error al guardar', 'Cerrar', { duration: 3000 })
    });
  }

  editarTipo(tipo: TipoSuscripcion): void {
    this.editandoTipo = tipo;
    this.formTipo.patchValue(tipo);
  }

  desactivarTipo(tipo: TipoSuscripcion): void {
    const id = this.contexto.obtenerIdOFail();
    this.suscripcionService.cambiarEstadoTipo(id, tipo.id, false).subscribe({
      next: () => {
        this.snackBar.open('Tipo desactivado', 'Cerrar', { duration: 3000 });
        this.suscripcionService.listarTipos(id).subscribe(d => this.tipos = d);
      }
    });
  }

  cancelarEdicionTipo(): void {
    this.editandoTipo = null;
    this.formTipo.reset({ duracionDias: 30 });
  }

  // ─── Extras ───────────────────────────────────────────────────────────────

  guardarExtra(): void {
    if (this.formExtra.invalid) return;
    const id = this.contexto.obtenerIdOFail();
    const op = this.editandoExtra
      ? this.suscripcionService.actualizarExtra(id, this.editandoExtra.id, this.formExtra.value)
      : this.suscripcionService.crearExtra(id, this.formExtra.value);

    op.subscribe({
      next: () => {
        this.snackBar.open('Extra guardado', 'Cerrar', { duration: 3000 });
        this.formExtra.reset();
        this.editandoExtra = null;
        this.suscripcionService.listarExtras(id).subscribe(d => this.extras = d);
      },
      error: () => this.snackBar.open('Error al guardar', 'Cerrar', { duration: 3000 })
    });
  }

  editarExtra(extra: Extra): void {
    this.editandoExtra = extra;
    this.formExtra.patchValue(extra);
  }

  cancelarEdicionExtra(): void {
    this.editandoExtra = null;
    this.formExtra.reset();
  }

  // ─── Asignar suscripción y registrar pago en efectivo ─────────────────────

  asignarYCobrar(): void {
    if (this.formAsignar.invalid) return;
    const gymId = this.contexto.obtenerIdOFail();
    const { clienteId, tipoSuscripcionId, fechaInicio } = this.formAsignar.value;
    this.asignando = true;

    this.suscripcionService.asignarSuscripcion(clienteId, { tipoSuscripcionId, fechaInicio }).subscribe({
      next: suscripcion => {
        this.pagoService.registrarPagoEfectivo(suscripcion.id).subscribe({
          next: () => {
            this.snackBar.open('Suscripción asignada y pago registrado', 'Cerrar', { duration: 3000 });
            this.formAsignar.patchValue({ fechaInicio: new Date().toISOString().slice(0, 10) });
            this.formAsignar.get('clienteId')?.reset();
            this.formAsignar.get('tipoSuscripcionId')?.reset();
            this.asignando = false;
            this.suscripcionService.listarSuscriptores(gymId).subscribe(d => this.suscriptores = d);
          },
          error: () => {
            this.snackBar.open('Suscripción creada pero error al registrar el pago', 'Cerrar', { duration: 4000 });
            this.asignando = false;
            this.suscripcionService.listarSuscriptores(gymId).subscribe(d => this.suscriptores = d);
          }
        });
      },
      error: () => {
        this.snackBar.open('Error al asignar la suscripción', 'Cerrar', { duration: 3000 });
        this.asignando = false;
      }
    });
  }

  registrarPagoEfectivo(s: ClienteSuscripcion): void {
    const gymId = this.contexto.obtenerIdOFail();
    this.pagoService.registrarPagoEfectivo(s.id).subscribe({
      next: () => {
        this.snackBar.open('Pago en efectivo registrado', 'Cerrar', { duration: 3000 });
        this.suscripcionService.listarSuscriptores(gymId).subscribe(d => this.suscriptores = d);
      },
      error: () => this.snackBar.open('Error al registrar el pago', 'Cerrar', { duration: 3000 })
    });
  }
}
