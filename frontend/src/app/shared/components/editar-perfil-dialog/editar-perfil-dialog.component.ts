import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Usuario, PerfilUsuarioRequest } from '../../../core/models/usuario.model';

export interface EditarPerfilDialogData {
  usuario: Usuario;
  titulo: string;
}

@Component({
  selector: 'app-editar-perfil-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.titulo }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form-grid">
        <mat-form-field appearance="outline">
          <mat-label>Nombre</mat-label>
          <input matInput formControlName="nombre" />
          @if (form.get('nombre')?.hasError('required')) {
            <mat-error>El nombre es obligatorio</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Apellidos</mat-label>
          <input matInput formControlName="apellidos" />
          @if (form.get('apellidos')?.hasError('required')) {
            <mat-error>Los apellidos son obligatorios</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" type="email" />
          @if (form.get('email')?.hasError('required')) {
            <mat-error>El email es obligatorio</mat-error>
          } @else if (form.get('email')?.hasError('email')) {
            <mat-error>Email no válido</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Teléfono</mat-label>
          <input matInput formControlName="telefono" />
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="cancelar()">Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()" [disabled]="form.invalid">
        <mat-icon>save</mat-icon>
        Guardar
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 8px 16px;
      min-width: 480px;
      padding-top: 8px;
    }
    .full-width { grid-column: 1 / -1; }
  `]
})
export class EditarPerfilDialogComponent {

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditarPerfilDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditarPerfilDialogData
  ) {
    this.form = this.fb.group({
      nombre:    [data.usuario.nombre,    Validators.required],
      apellidos: [data.usuario.apellidos, Validators.required],
      email:     [data.usuario.email,     [Validators.required, Validators.email]],
      telefono:  [data.usuario.telefono ?? '']
    });
  }

  guardar(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.value as PerfilUsuarioRequest);
  }

  cancelar(): void {
    this.dialogRef.close(null);
  }
}
