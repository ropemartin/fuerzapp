import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { TELEFONO_PATTERN } from '../../../core/validators/app.validators';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { ActivosPipe } from '../../../shared/pipes/activos.pipe';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { AdminService } from '../../../core/services/admin.service';
import { Gimnasio } from '../../../core/models/gimnasio.model';
import { Usuario } from '../../../core/models/usuario.model';
import { AltaGimnasioDialogComponent } from './alta-gimnasio-dialog.component';

@Component({
  selector: 'app-editar-gimnasio-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
    MatSnackBarModule, MatProgressSpinnerModule
  ],
  template: `
    <h2 mat-dialog-title><mat-icon>edit</mat-icon> Editar gimnasio</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form-grid">
        <div class="fila-2">
          <mat-form-field appearance="outline">
            <mat-label>Nombre *</mat-label>
            <input matInput formControlName="nombre" />
            @if (form.get('nombre')?.hasError('required') && form.get('nombre')?.touched) {
              <mat-error>Obligatorio</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Ciudad</mat-label>
            <input matInput formControlName="ciudad" />
          </mat-form-field>
        </div>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Dirección</mat-label>
          <input matInput formControlName="direccion" />
        </mat-form-field>
        <div class="fila-2">
          <mat-form-field appearance="outline">
            <mat-label>Teléfono</mat-label>
            <input matInput formControlName="telefono" />
            @if (form.get('telefono')?.hasError('pattern')) {
              <mat-error>Introduce entre 9 y 15 dígitos</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Email</mat-label>
            <input matInput type="email" formControlName="email" />
            @if (form.get('email')?.hasError('email')) {
              <mat-error>Email no válido</mat-error>
            }
          </mat-form-field>
        </div>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Propietario</mat-label>
          <mat-select formControlName="propietarioId">
            @for (u of propietarios; track u.id) {
              <mat-option [value]="u.id">{{ u.nombre }} {{ u.apellidos }} — {{ u.email }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="cancelar()" [disabled]="cargando">Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()" [disabled]="form.invalid || cargando">
        @if (cargando) { <mat-spinner diameter="18" /> }
        @else { <mat-icon>save</mat-icon> Guardar }
      </button>
    </mat-dialog-actions>
  `,
  styles: [`.fila-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
            .full-width { width: 100%; } .form-grid { display: flex; flex-direction: column; gap: 4px; min-width: 480px; }`]
})
export class EditarGimnasioDialogComponent implements OnInit {
  form: FormGroup;
  cargando = false;
  propietarios: Usuario[] = [];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditarGimnasioDialogComponent>,
    private gimnasioService: GimnasioService,
    private adminService: AdminService,
    private snack: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: Gimnasio
  ) {
    this.form = this.fb.group({
      nombre:        [data.nombre,           Validators.required],
      ciudad:        [data.ciudad        ?? ''],
      direccion:     [data.direccion     ?? ''],
      telefono:      [data.telefono      ?? '', Validators.pattern(TELEFONO_PATTERN)],
      email:         [data.email         ?? '', Validators.email],
      propietarioId: [data.propietarioId ?? null]
    });
  }

  ngOnInit(): void {
    this.adminService.listarUsuarios().subscribe({
      next: users => this.propietarios = users.filter(u => u.rol === 'PROPIETARIO' && u.activo),
      error: () => {}
    });
  }

  guardar(): void {
    if (this.form.invalid) return;
    this.cargando = true;
    this.gimnasioService.actualizar(this.data.id, this.form.value).subscribe({
      next: () => {
        this.snack.open('Gimnasio actualizado', 'Cerrar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.snack.open(err.error?.mensaje || 'Error al guardar', 'Cerrar', { duration: 4000 });
        this.cargando = false;
      }
    });
  }

  cancelar(): void { this.dialogRef.close(false); }
}

@Component({
  selector: 'app-gimnasios',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    MatSnackBarModule,
    MatCardModule,
    MatTooltipModule,
    LayoutComponent,
    ActivosPipe
  ],
  templateUrl: './gimnasios.component.html',
  styleUrl: './gimnasios.component.scss'
})
export class GimnasiosComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Gimnasios', icon: 'fitness_center', ruta: '/admin/gimnasios' },
    { label: 'Usuarios',  icon: 'people',          ruta: '/admin/usuarios' }
  ];

  columnas = ['nombre', 'ciudad', 'propietario', 'estado', 'acciones'];
  gimnasios: Gimnasio[] = [];
  cargando = true;

  constructor(
    private gimnasioService: GimnasioService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarGimnasios();
  }

  cargarGimnasios(): void {
    this.cargando = true;
    this.gimnasioService.listarTodos().subscribe({
      next: data => {
        this.gimnasios = data;
        this.cargando = false;
      },
      error: () => {
        this.mostrarError('Error al cargar los gimnasios');
        this.cargando = false;
      }
    });
  }

  abrirDialogoAlta(): void {
    const ref = this.dialog.open(AltaGimnasioDialogComponent, {
      width: '600px',
      disableClose: true
    });

    ref.afterClosed().subscribe(resultado => {
      if (resultado) this.cargarGimnasios();
    });
  }

  editarGimnasio(gimnasio: Gimnasio): void {
    this.dialog.open(EditarGimnasioDialogComponent, {
      width: '560px',
      disableClose: true,
      data: gimnasio
    }).afterClosed().subscribe(ok => { if (ok) this.cargarGimnasios(); });
  }

  cambiarEstado(gimnasio: Gimnasio): void {
    const nuevoEstado = !gimnasio.activo;
    const accion = nuevoEstado ? 'activado' : 'desactivado';

    this.gimnasioService.cambiarEstado(gimnasio.id, nuevoEstado).subscribe({
      next: () => {
        gimnasio.activo = nuevoEstado;
        this.snackBar.open(`Gimnasio ${accion} correctamente`, 'Cerrar', { duration: 3000 });
      },
      error: () => this.mostrarError('Error al cambiar el estado del gimnasio')
    });
  }

  private mostrarError(msg: string): void {
    this.snackBar.open(msg, 'Cerrar', { duration: 4000, panelClass: 'snack-error' });
  }
}
