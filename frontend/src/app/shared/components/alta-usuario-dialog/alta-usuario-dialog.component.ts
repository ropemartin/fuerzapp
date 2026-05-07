import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-alta-usuario-dialog',
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
  templateUrl: './alta-usuario-dialog.component.html',
  styleUrl: './alta-usuario-dialog.component.scss'
})
export class AltaUsuarioDialogComponent {

  form: FormGroup;
  mostrarPassword = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AltaUsuarioDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { titulo: string }
  ) {
    this.form = this.fb.group({
      nombre:    ['', Validators.required],
      apellidos: ['', Validators.required],
      email:     ['', [Validators.required, Validators.email]],
      password:  ['', [Validators.required, Validators.minLength(6)]],
      telefono:  ['']
    });
  }

  guardar(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.value);
  }

  cancelar(): void {
    this.dialogRef.close(null);
  }
}
