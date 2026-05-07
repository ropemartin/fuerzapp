import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Rol } from '../models/usuario.model';

export const rolGuard = (rolesPermitidos: Rol[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const rol = authService.getRol();

    if (rol && rolesPermitidos.includes(rol)) return true;

    router.navigate(['/login']);
    return false;
  };
};
