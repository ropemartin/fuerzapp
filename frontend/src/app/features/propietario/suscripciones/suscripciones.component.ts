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
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { SuscripcionService } from '../../../core/services/suscripcion.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { TipoSuscripcion, Extra, ClienteSuscripcion } from '../../../core/models/suscripcion.model';

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
    { label: 'Dashboard',     icon: 'dashboard',       ruta: '/propietario/dashboard' },
    { label: 'Entrenadores',  icon: 'sports',          ruta: '/propietario/entrenadores' },
    { label: 'Clientes',      icon: 'group',           ruta: '/propietario/clientes' },
    { label: 'Suscripciones', icon: 'card_membership', ruta: '/propietario/suscripciones' }
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
  columnasSuscriptores = ['cliente', 'tipo', 'fechas', 'estado', 'extras'];

  cargando = true;

  constructor(
    private fb: FormBuilder,
    private suscripcionService: SuscripcionService,
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
}
