import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

function passwordsIguales(control: AbstractControl): ValidationErrors | null {
  const nueva = control.get('passwordNueva')?.value;
  const confirmar = control.get('confirmar')?.value;
  return nueva && confirmar && nueva !== confirmar ? { noCoinciden: true } : null;
}

@Component({
  selector: 'app-cambiar-password-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>lock</mat-icon>
      Cambiar contraseña
    </h2>

    <mat-dialog-content>
      <form [formGroup]="form" class="form-password">

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Contraseña actual</mat-label>
          <input matInput [type]="verActual ? 'text' : 'password'" formControlName="passwordActual" />
          <button mat-icon-button matSuffix type="button" (click)="verActual = !verActual">
            <mat-icon>{{ verActual ? 'visibility_off' : 'visibility' }}</mat-icon>
          </button>
          @if (form.get('passwordActual')?.hasError('required') && form.get('passwordActual')?.touched) {
            <mat-error>La contraseña actual es obligatoria</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nueva contraseña</mat-label>
          <input matInput [type]="verNueva ? 'text' : 'password'" formControlName="passwordNueva" />
          <button mat-icon-button matSuffix type="button" (click)="verNueva = !verNueva">
            <mat-icon>{{ verNueva ? 'visibility_off' : 'visibility' }}</mat-icon>
          </button>
          @if (form.get('passwordNueva')?.hasError('required') && form.get('passwordNueva')?.touched) {
            <mat-error>La nueva contraseña es obligatoria</mat-error>
          }
          @if (form.get('passwordNueva')?.hasError('minlength') && form.get('passwordNueva')?.touched) {
            <mat-error>Mínimo 6 caracteres</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Confirmar nueva contraseña</mat-label>
          <input matInput [type]="verConfirmar ? 'text' : 'password'" formControlName="confirmar" />
          <button mat-icon-button matSuffix type="button" (click)="verConfirmar = !verConfirmar">
            <mat-icon>{{ verConfirmar ? 'visibility_off' : 'visibility' }}</mat-icon>
          </button>
          @if (form.hasError('noCoinciden') && form.get('confirmar')?.touched) {
            <mat-error>Las contraseñas no coinciden</mat-error>
          }
        </mat-form-field>

      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="cancelar()" [disabled]="guardando">Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()"
              [disabled]="form.invalid || guardando">
        <mat-icon>save</mat-icon>
        {{ guardando ? 'Guardando...' : 'Cambiar contraseña' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2 mat-icon { vertical-align: middle; margin-right: 8px; }
    .form-password { display: flex; flex-direction: column; gap: 4px; padding-top: 8px; min-width: 360px; }
    .full-width { width: 100%; }
  `]
})
export class CambiarPasswordDialogComponent {

  form: FormGroup;
  verActual = false;
  verNueva = false;
  verConfirmar = false;
  guardando = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<CambiarPasswordDialogComponent>,
    private authService: AuthService,
    private snack: MatSnackBar
  ) {
    this.form = this.fb.group({
      passwordActual: ['', Validators.required],
      passwordNueva:  ['', [Validators.required, Validators.minLength(6)]],
      confirmar:      ['', Validators.required]
    }, { validators: passwordsIguales });
  }

  guardar(): void {
    if (this.form.invalid) return;
    this.guardando = true;

    const { passwordActual, passwordNueva } = this.form.value;
    this.authService.cambiarPassword(passwordActual, passwordNueva).subscribe({
      next: () => {
        this.snack.open('Contraseña cambiada correctamente', 'Cerrar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.guardando = false;
        const msg = err.status === 400 ? 'La contraseña actual no es correcta' : 'Error al cambiar la contraseña';
        this.snack.open(msg, 'Cerrar', { duration: 4000 });
      }
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
