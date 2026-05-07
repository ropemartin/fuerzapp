import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
import { forkJoin, of } from 'rxjs';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { EditarPerfilDialogComponent } from '../../../shared/components/editar-perfil-dialog/editar-perfil-dialog.component';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { AltaUsuarioDialogComponent } from '../../../shared/components/alta-usuario-dialog/alta-usuario-dialog.component';
import { Usuario } from '../../../core/models/usuario.model';
import { Entrenamiento } from '../../../core/models/entrenamiento.model';

@Component({
  selector: 'app-reasignar-dialog',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatDialogModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatSelectModule
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon color="warn">warning</mat-icon>
      Reasignar entrenamientos
    </h2>
    <mat-dialog-content>
      <p class="aviso">Este entrenador tiene <strong>{{ data.entrenamientos.length }}</strong> entrenamiento(s) activo(s). Asigna cada uno a otro entrenador antes de continuar:</p>
      @if (data.entrenadores.length === 0) {
        <p class="sin-entrenadores">No hay otros entrenadores activos en este gimnasio.</p>
      } @else {
        @for (e of data.entrenamientos; track e.id) {
          <div class="fila-entrenamiento">
            <span class="nombre-entrenamiento">{{ e.nombre }}</span>
            <mat-form-field appearance="outline" class="select-trainer">
              <mat-label>Nuevo entrenador</mat-label>
              <mat-select [(ngModel)]="asignaciones[e.id]">
                @for (t of data.entrenadores; track t.id) {
                  <mat-option [value]="t.id">{{ t.nombre }} {{ t.apellidos }}</mat-option>
                }
              </mat-select>
            </mat-form-field>
          </div>
        }
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="cancelar()">Cancelar</button>
      @if (data.entrenadores.length > 0) {
        <button mat-flat-button color="warn" (click)="confirmar()" [disabled]="!todasAsignadas">
          <mat-icon>person_off</mat-icon>
          Dar de baja y reasignar
        </button>
      }
    </mat-dialog-actions>
  `,
  styles: [`
    .aviso { margin-bottom: 16px; }
    .sin-entrenadores { color: #f44336; font-style: italic; }
    .fila-entrenamiento { display: flex; align-items: center; gap: 16px; margin-bottom: 4px; }
    .nombre-entrenamiento { flex: 1; font-weight: 500; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .select-trainer { width: 240px; flex-shrink: 0; }
  `]
})
export class ReasignarDialogComponent {
  asignaciones: { [id: number]: number } = {};

  constructor(
    private dialogRef: MatDialogRef<ReasignarDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { entrenamientos: Entrenamiento[], entrenadores: Usuario[] }
  ) {
    data.entrenamientos.forEach(e => this.asignaciones[e.id] = 0);
  }

  get todasAsignadas(): boolean {
    return this.data.entrenamientos.every(e => (this.asignaciones[e.id] ?? 0) > 0);
  }

  confirmar(): void { this.dialogRef.close(this.asignaciones); }
  cancelar(): void { this.dialogRef.close(null); }
}

@Component({
  selector: 'app-entrenadores',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatCardModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    LayoutComponent
  ],
  templateUrl: './entrenadores.component.html',
  styleUrl: './entrenadores.component.scss'
})
export class EntrenadoresComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',       ruta: '/propietario/dashboard' },
    { label: 'Entrenadores',   icon: 'sports',          ruta: '/propietario/entrenadores' },
    { label: 'Clientes',       icon: 'group',           ruta: '/propietario/clientes' },
    { label: 'Suscripciones',  icon: 'card_membership', ruta: '/propietario/suscripciones' },
    { label: 'Configuración',  icon: 'settings',        ruta: '/propietario/configuracion' }
  ];

  columnas = ['nombre', 'email', 'telefono', 'estado', 'acciones'];
  entrenadores: Usuario[] = [];
  cargando = true;
  procesando: { [id: number]: boolean } = {};

  constructor(
    private gimnasioService: GimnasioService,
    public contexto: GimnasioContextService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    const id = this.contexto.obtenerIdOFail();
    this.cargando = true;
    this.gimnasioService.listarEntrenadores(id).subscribe({
      next: data => { this.entrenadores = data; this.cargando = false; },
      error: () => { this.mostrarError('Error al cargar entrenadores'); this.cargando = false; }
    });
  }

  abrirDialogoAlta(): void {
    const ref = this.dialog.open(AltaUsuarioDialogComponent, {
      width: '480px',
      data: { titulo: 'Nuevo entrenador' }
    });
    ref.afterClosed().subscribe(datos => {
      if (!datos) return;
      const id = this.contexto.obtenerIdOFail();
      this.gimnasioService.altaEntrenador(id, datos).subscribe({
        next: () => {
          this.snackBar.open('Entrenador dado de alta correctamente', 'Cerrar', { duration: 3000 });
          this.cargar();
        },
        error: (err) => this.mostrarError(err.error?.mensaje || 'Error al dar de alta el entrenador')
      });
    });
  }

  darDeBaja(entrenador: Usuario): void {
    const gimnasioId = this.contexto.obtenerIdOFail();
    this.procesando[entrenador.id] = true;

    this.gimnasioService.listarEntrenamientosDeEntrenador(gimnasioId, entrenador.id).subscribe({
      next: (entrenamientos) => {
        if (entrenamientos.length === 0) {
          this.ejecutarBaja(gimnasioId, entrenador);
          return;
        }

        const otrosActivos = this.entrenadores.filter(e => e.id !== entrenador.id && e.activoEnGimnasio);
        this.procesando[entrenador.id] = false;

        const ref = this.dialog.open(ReasignarDialogComponent, {
          width: '620px',
          disableClose: true,
          data: { entrenamientos, entrenadores: otrosActivos }
        });

        ref.afterClosed().subscribe(asignaciones => {
          if (!asignaciones) return;
          this.procesando[entrenador.id] = true;
          const ops = Object.entries(asignaciones)
            .filter(([, tId]) => (tId as number) > 0)
            .map(([eId, tId]) => this.gimnasioService.reasignarEntrenador(+eId, tId as number));
          const reasignacion$ = ops.length > 0 ? forkJoin(ops) : of([] as void[]);
          reasignacion$.subscribe({
            next: () => this.ejecutarBaja(gimnasioId, entrenador),
            error: () => {
              this.procesando[entrenador.id] = false;
              this.mostrarError('Error al reasignar los entrenamientos');
            }
          });
        });
      },
      error: () => {
        this.procesando[entrenador.id] = false;
        this.mostrarError('Error al verificar entrenamientos del entrenador');
      }
    });
  }

  private ejecutarBaja(gimnasioId: number, entrenador: Usuario): void {
    this.gimnasioService.bajaEntrenador(gimnasioId, entrenador.id).subscribe({
      next: () => {
        this.snackBar.open('Entrenador dado de baja', 'Cerrar', { duration: 3000 });
        this.procesando[entrenador.id] = false;
        this.cargar();
      },
      error: () => {
        this.procesando[entrenador.id] = false;
        this.mostrarError('Error al dar de baja el entrenador');
      }
    });
  }

  reactivar(entrenador: Usuario): void {
    const gimnasioId = this.contexto.obtenerIdOFail();
    this.procesando[entrenador.id] = true;
    this.gimnasioService.reactivarEntrenador(gimnasioId, entrenador.id).subscribe({
      next: () => {
        this.snackBar.open('Entrenador reactivado', 'Cerrar', { duration: 3000 });
        this.procesando[entrenador.id] = false;
        this.cargar();
      },
      error: () => {
        this.procesando[entrenador.id] = false;
        this.mostrarError('Error al reactivar el entrenador');
      }
    });
  }

  editarPerfil(entrenador: Usuario): void {
    const ref = this.dialog.open(EditarPerfilDialogComponent, {
      width: '560px',
      data: { usuario: entrenador, titulo: 'Editar datos del entrenador' }
    });
    ref.afterClosed().subscribe(datos => {
      if (!datos) return;
      const gimnasioId = this.contexto.obtenerIdOFail();
      this.gimnasioService.actualizarPerfilEntrenador(gimnasioId, entrenador.id, datos).subscribe({
        next: () => {
          this.snackBar.open('Datos actualizados', 'Cerrar', { duration: 3000 });
          this.cargar();
        },
        error: (err) => this.mostrarError(err.error?.mensaje || 'Error al actualizar los datos')
      });
    });
  }

  private mostrarError(msg: string): void {
    this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
  }
}
