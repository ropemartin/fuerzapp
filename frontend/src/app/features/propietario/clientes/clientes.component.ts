import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { EditarPerfilDialogComponent } from '../../../shared/components/editar-perfil-dialog/editar-perfil-dialog.component';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { AltaUsuarioDialogComponent } from '../../../shared/components/alta-usuario-dialog/alta-usuario-dialog.component';
import { Usuario } from '../../../core/models/usuario.model';
import { SuscripcionActivaInfo } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-baja-cliente-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>
      <mat-icon color="warn">person_off</mat-icon>
      Dar de baja a {{ data.clienteNombre }}
    </h2>
    <mat-dialog-content>
      @if (data.suscripcion) {
        <div class="aviso-suscripcion">
          <mat-icon color="warn">warning</mat-icon>
          <p>
            Este cliente tiene la suscripción <strong>{{ data.suscripcion.tipoNombre }}</strong>
            activa hasta el <strong>{{ data.suscripcion.fechaFin | date:'dd/MM/yyyy' }}</strong>.
            La suscripción continuará hasta su vencimiento.
          </p>
        </div>
      }
      <p>¿Confirmas que deseas dar de baja a este cliente del gimnasio?</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="cancelar()">Cancelar</button>
      <button mat-flat-button color="warn" (click)="confirmar()">
        <mat-icon>person_off</mat-icon>
        Dar de baja
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2 mat-icon { vertical-align: middle; margin-right: 8px; }
    .aviso-suscripcion {
      display: flex;
      gap: 12px;
      align-items: flex-start;
      background: #fff3e0;
      border-radius: 6px;
      padding: 12px;
      margin-bottom: 12px;
      mat-icon { flex-shrink: 0; margin-top: 2px; }
      p { margin: 0; font-size: 0.9rem; }
    }
  `]
})
export class BajaClienteDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<BajaClienteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { clienteNombre: string; suscripcion: SuscripcionActivaInfo | null }
  ) {}
  confirmar(): void { this.dialogRef.close(true); }
  cancelar(): void { this.dialogRef.close(false); }
}

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatCardModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    LayoutComponent
  ],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.scss'
})
export class ClientesComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',     icon: 'dashboard',       ruta: '/propietario/dashboard' },
    { label: 'Entrenadores',  icon: 'sports',          ruta: '/propietario/entrenadores' },
    { label: 'Clientes',      icon: 'group',           ruta: '/propietario/clientes' },
    { label: 'Suscripciones', icon: 'card_membership', ruta: '/propietario/suscripciones' }
  ];

  columnas = ['nombre', 'email', 'telefono', 'estado', 'acciones'];
  clientes: Usuario[] = [];
  cargando = true;
  procesando: { [id: number]: boolean } = {};

  constructor(
    private gimnasioService: GimnasioService,
    public contexto: GimnasioContextService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    const id = this.contexto.obtenerIdOFail();
    this.cargando = true;
    this.gimnasioService.listarClientes(id).subscribe({
      next: data => { this.clientes = data; this.cargando = false; },
      error: () => { this.mostrarError('Error al cargar clientes'); this.cargando = false; }
    });
  }

  abrirDialogoAlta(): void {
    const ref = this.dialog.open(AltaUsuarioDialogComponent, {
      width: '480px',
      data: { titulo: 'Nuevo cliente' }
    });

    ref.afterClosed().subscribe(datos => {
      if (!datos) return;
      const id = this.contexto.obtenerIdOFail();
      this.gimnasioService.altaCliente(id, datos).subscribe({
        next: () => {
          this.snackBar.open('Cliente dado de alta correctamente', 'Cerrar', { duration: 3000 });
          this.cargar();
        },
        error: (err) => this.mostrarError(err.error?.mensaje || 'Error al dar de alta el cliente')
      });
    });
  }

  darDeBaja(cliente: Usuario): void {
    const gimnasioId = this.contexto.obtenerIdOFail();
    this.procesando[cliente.id] = true;

    this.gimnasioService.suscripcionActivaCliente(gimnasioId, cliente.id).subscribe({
      next: (suscripcion) => {
        this.procesando[cliente.id] = false;
        const ref = this.dialog.open(BajaClienteDialogComponent, {
          width: '480px',
          data: { clienteNombre: `${cliente.nombre} ${cliente.apellidos}`, suscripcion }
        });
        ref.afterClosed().subscribe(confirmado => {
          if (!confirmado) return;
          this.ejecutarBaja(gimnasioId, cliente);
        });
      },
      error: () => {
        this.procesando[cliente.id] = false;
        this.mostrarError('Error al verificar la suscripción del cliente');
      }
    });
  }

  private ejecutarBaja(gimnasioId: number, cliente: Usuario): void {
    this.procesando[cliente.id] = true;
    this.gimnasioService.bajaCliente(gimnasioId, cliente.id).subscribe({
      next: () => {
        this.snackBar.open('Cliente dado de baja', 'Cerrar', { duration: 3000 });
        this.procesando[cliente.id] = false;
        this.cargar();
      },
      error: () => {
        this.procesando[cliente.id] = false;
        this.mostrarError('Error al dar de baja el cliente');
      }
    });
  }

  reactivar(cliente: Usuario): void {
    const gimnasioId = this.contexto.obtenerIdOFail();
    this.procesando[cliente.id] = true;
    this.gimnasioService.reactivarCliente(gimnasioId, cliente.id).subscribe({
      next: () => {
        this.snackBar.open('Cliente reactivado', 'Cerrar', { duration: 3000 });
        this.procesando[cliente.id] = false;
        this.cargar();
      },
      error: () => {
        this.procesando[cliente.id] = false;
        this.mostrarError('Error al reactivar el cliente');
      }
    });
  }

  editarPerfil(cliente: Usuario): void {
    const ref = this.dialog.open(EditarPerfilDialogComponent, {
      width: '560px',
      data: { usuario: cliente, titulo: 'Editar datos del cliente' }
    });
    ref.afterClosed().subscribe(datos => {
      if (!datos) return;
      const gimnasioId = this.contexto.obtenerIdOFail();
      this.gimnasioService.actualizarPerfilCliente(gimnasioId, cliente.id, datos).subscribe({
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
