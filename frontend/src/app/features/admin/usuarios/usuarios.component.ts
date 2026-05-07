import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { AdminService, UsuarioAdminRequest } from '../../../core/services/admin.service';
import { Usuario, Rol } from '../../../core/models/usuario.model';
import { NOMBRE_PATTERN, TELEFONO_PATTERN } from '../../../core/validators/app.validators';

@Component({
  selector: 'app-usuario-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
    MatSnackBarModule, MatProgressSpinnerModule
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>{{ data ? 'edit' : 'person_add' }}</mat-icon>
      {{ data ? 'Editar usuario' : 'Nuevo usuario' }}
    </h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form-grid">
        <div class="fila-2">
          <mat-form-field appearance="outline">
            <mat-label>Nombre *</mat-label>
            <input matInput formControlName="nombre" />
            @if (form.get('nombre')?.hasError('required')) {
              <mat-error>Obligatorio</mat-error>
            } @else if (form.get('nombre')?.hasError('pattern')) {
              <mat-error>Solo se permiten letras</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Apellidos *</mat-label>
            <input matInput formControlName="apellidos" />
            @if (form.get('apellidos')?.hasError('required')) {
              <mat-error>Obligatorio</mat-error>
            } @else if (form.get('apellidos')?.hasError('pattern')) {
              <mat-error>Solo se permiten letras</mat-error>
            }
          </mat-form-field>
        </div>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email *</mat-label>
          <input matInput type="email" formControlName="email" />
          @if (form.get('email')?.hasError('email')) {
            <mat-error>Email no válido</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>{{ data ? 'Nueva contraseña (dejar vacío para no cambiar)' : 'Contraseña *' }}</mat-label>
          <input matInput [type]="mostrarPass ? 'text' : 'password'" formControlName="password" />
          <button mat-icon-button matSuffix type="button" (click)="mostrarPass = !mostrarPass">
            <mat-icon>{{ mostrarPass ? 'visibility_off' : 'visibility' }}</mat-icon>
          </button>
          @if (form.get('password')?.hasError('minlength')) {
            <mat-error>Mínimo 6 caracteres</mat-error>
          }
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
            <mat-label>Rol *</mat-label>
            <mat-select formControlName="rol">
              <mat-option value="ADMIN_PLATAFORMA">Admin Plataforma</mat-option>
              <mat-option value="PROPIETARIO">Propietario</mat-option>
              <mat-option value="ENTRENADOR">Entrenador</mat-option>
              <mat-option value="CLIENTE">Cliente</mat-option>
            </mat-select>
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="cancelar()" [disabled]="cargando">Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()" [disabled]="form.invalid || cargando">
        @if (cargando) { <mat-spinner diameter="18" /> }
        @else { <mat-icon>save</mat-icon> {{ data ? 'Guardar' : 'Crear' }} }
      </button>
    </mat-dialog-actions>
  `,
  styles: [`.fila-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
            .full-width { width: 100%; } .form-grid { display: flex; flex-direction: column; gap: 4px; min-width: 480px; }`]
})
export class UsuarioDialogComponent {
  form: FormGroup;
  cargando = false;
  mostrarPass = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UsuarioDialogComponent>,
    private adminService: AdminService,
    private snack: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: Usuario | null
  ) {
    const passValidators = data ? [Validators.minLength(6)] : [Validators.required, Validators.minLength(6)];
    this.form = this.fb.group({
      nombre:    [data?.nombre    ?? '', [Validators.required, Validators.pattern(NOMBRE_PATTERN)]],
      apellidos: [data?.apellidos ?? '', [Validators.required, Validators.pattern(NOMBRE_PATTERN)]],
      email:     [data?.email     ?? '', [Validators.required, Validators.email]],
      password:  ['', passValidators],
      telefono:  [data?.telefono  ?? '', Validators.pattern(TELEFONO_PATTERN)],
      rol:       [data?.rol       ?? '', Validators.required]
    });
  }

  guardar(): void {
    if (this.form.invalid) return;
    this.cargando = true;
    const req: UsuarioAdminRequest = this.form.value;
    const op = this.data
      ? this.adminService.actualizarUsuario(this.data.id, req)
      : this.adminService.crearUsuario(req);

    op.subscribe({
      next: () => {
        this.snack.open(this.data ? 'Usuario actualizado' : 'Usuario creado', 'Cerrar', { duration: 3000 });
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
  selector: 'app-usuarios',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatButtonModule, MatIconModule,
    MatDialogModule, MatSnackBarModule, MatCardModule, MatTooltipModule,
    LayoutComponent
  ],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.scss'
})
export class UsuariosComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Gimnasios', icon: 'fitness_center', ruta: '/admin/gimnasios' },
    { label: 'Usuarios',  icon: 'people',          ruta: '/admin/usuarios' }
  ];

  columnas = ['nombre', 'email', 'rol', 'estado', 'acciones'];
  usuarios: Usuario[] = [];
  cargando = true;

  constructor(
    private adminService: AdminService,
    private dialog: MatDialog,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.cargando = true;
    this.adminService.listarUsuarios().subscribe({
      next: data => { this.usuarios = data; this.cargando = false; },
      error: () => { this.snack.open('Error al cargar usuarios', 'Cerrar', { duration: 3000 }); this.cargando = false; }
    });
  }

  abrirDialogo(usuario: Usuario | null = null): void {
    this.dialog.open(UsuarioDialogComponent, {
      width: '560px',
      disableClose: true,
      data: usuario
    }).afterClosed().subscribe(ok => { if (ok) this.cargar(); });
  }

  cambiarEstado(u: Usuario): void {
    this.adminService.cambiarEstadoUsuario(u.id, !u.activo).subscribe({
      next: () => { u.activo = !u.activo; this.snack.open(`Usuario ${u.activo ? 'activado' : 'desactivado'}`, 'Cerrar', { duration: 3000 }); },
      error: () => this.snack.open('Error al cambiar estado', 'Cerrar', { duration: 3000 })
    });
  }

  rolLabel(rol: Rol): string {
    const map: Record<Rol, string> = {
      ADMIN_PLATAFORMA: 'Admin',
      PROPIETARIO: 'Propietario',
      ENTRENADOR: 'Entrenador',
      CLIENTE: 'Cliente'
    };
    return map[rol] ?? rol;
  }
}
