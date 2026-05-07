import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatStepperModule } from '@angular/material/stepper';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { GimnasioService } from '../../../core/services/gimnasio.service';

@Component({
  selector: 'app-alta-gimnasio-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatStepperModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './alta-gimnasio-dialog.component.html',
  styleUrl: './alta-gimnasio-dialog.component.scss'
})
export class AltaGimnasioDialogComponent {

  formGimnasio: FormGroup;
  formPropietario: FormGroup;
  cargando = false;
  mostrarPassword = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AltaGimnasioDialogComponent>,
    private gimnasioService: GimnasioService,
    private snackBar: MatSnackBar
  ) {
    this.formGimnasio = this.fb.group({
      nombre: ['', Validators.required],
      direccion: [''],
      ciudad: [''],
      telefono: [''],
      email: ['', Validators.email]
    });

    this.formPropietario = this.fb.group({
      nombre: ['', Validators.required],
      apellidos: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      telefono: ['']
    });
  }

  guardar(): void {
    if (this.formGimnasio.invalid || this.formPropietario.invalid) return;

    this.cargando = true;

    this.gimnasioService.crear({
      gimnasio: this.formGimnasio.value,
      propietario: this.formPropietario.value
    }).subscribe({
      next: () => {
        this.cargando = false;
        this.snackBar.open('Gimnasio creado correctamente', 'Cerrar', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.cargando = false;
        const msg = err.error?.mensaje || 'Error al crear el gimnasio';
        this.snackBar.open(msg, 'Cerrar', { duration: 4000 });
      }
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
