import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/services/auth.service';
import { Rol } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  form: FormGroup;
  cargando = false;
  error = '';
  mostrarPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    // Si ya está autenticado, redirigir directamente
    if (this.authService.estaAutenticado()) {
      this.redirigirPorRol(this.authService.getRol()!);
    }
  }

  login(): void {
    if (this.form.invalid) return;

    this.cargando = true;
    this.error = '';

    this.authService.login(this.form.value).subscribe({
      next: response => {
        this.cargando = false;
        this.redirigirPorRol(response.rol);
      },
      error: () => {
        this.cargando = false;
        this.error = 'Email o contraseña incorrectos';
      }
    });
  }

  private redirigirPorRol(rol: Rol): void {
    const rutas: Record<Rol, string> = {
      ADMIN_PLATAFORMA: '/admin/gimnasios',
      PROPIETARIO: '/propietario/dashboard',
      ENTRENADOR: '/entrenador/dashboard',
      CLIENTE: '/cliente/dashboard'
    };
    this.router.navigate([rutas[rol]]);
  }
}
