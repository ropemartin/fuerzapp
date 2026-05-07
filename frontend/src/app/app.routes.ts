import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { rolGuard } from './core/guards/rol.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },

  // ─── Admin de plataforma ──────────────────────────────────────────────────
  {
    path: 'admin',
    canActivate: [authGuard, rolGuard(['ADMIN_PLATAFORMA'])],
    children: [
      {
        path: 'gimnasios',
        loadComponent: () =>
          import('./features/admin/gimnasios/gimnasios.component').then(m => m.GimnasiosComponent)
      },
      {
        path: 'usuarios',
        loadComponent: () =>
          import('./features/admin/usuarios/usuarios.component').then(m => m.UsuariosComponent)
      },
      { path: '', redirectTo: 'gimnasios', pathMatch: 'full' }
    ]
  },

  // ─── Propietario ──────────────────────────────────────────────────────────
  {
    path: 'propietario',
    canActivate: [authGuard, rolGuard(['PROPIETARIO'])],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/propietario/dashboard/dashboard.component').then(m => m.DashboardPropietarioComponent)
      },
      {
        path: 'entrenadores',
        loadComponent: () =>
          import('./features/propietario/entrenadores/entrenadores.component').then(m => m.EntrenadoresComponent)
      },
      {
        path: 'clientes',
        loadComponent: () =>
          import('./features/propietario/clientes/clientes.component').then(m => m.ClientesComponent)
      },
      {
        path: 'suscripciones',
        loadComponent: () =>
          import('./features/propietario/suscripciones/suscripciones.component').then(m => m.SuscripcionesComponent)
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  // ─── Entrenador ───────────────────────────────────────────────────────────
  {
    path: 'entrenador',
    canActivate: [authGuard, rolGuard(['ENTRENADOR'])],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/entrenador/dashboard/dashboard.component').then(m => m.DashboardEntrenadorComponent)
      },
      {
        path: 'entrenamientos',
        loadComponent: () =>
          import('./features/entrenador/entrenamientos/entrenamientos.component').then(m => m.EntrenamientosComponent)
      },
      {
        path: 'ejercicios',
        loadComponent: () =>
          import('./features/entrenador/ejercicios/ejercicios.component').then(m => m.EjerciciosComponent)
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  // ─── Cliente ──────────────────────────────────────────────────────────────
  {
    path: 'cliente',
    canActivate: [authGuard, rolGuard(['CLIENTE'])],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/cliente/dashboard/dashboard.component').then(m => m.DashboardClienteComponent)
      },
      {
        path: 'entrenamientos',
        loadComponent: () =>
          import('./features/cliente/entrenamientos/entrenamientos.component').then(m => m.EntrenamientosClienteComponent)
      },
      {
        path: 'suscripcion',
        loadComponent: () =>
          import('./features/cliente/suscripcion/suscripcion.component').then(m => m.SuscripcionClienteComponent)
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
