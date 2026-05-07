import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { GimnasioService } from '../../../core/services/gimnasio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { Gimnasio } from '../../../core/models/gimnasio.model';

@Component({
  selector: 'app-configuracion-propietario',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSnackBarModule,
    MatTabsModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    LayoutComponent
  ],
  templateUrl: './configuracion.component.html',
  styleUrl: './configuracion.component.scss'
})
export class ConfiguracionPropietarioComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',      icon: 'dashboard',       ruta: '/propietario/dashboard' },
    { label: 'Entrenadores',   icon: 'sports',          ruta: '/propietario/entrenadores' },
    { label: 'Clientes',       icon: 'group',           ruta: '/propietario/clientes' },
    { label: 'Suscripciones',  icon: 'card_membership', ruta: '/propietario/suscripciones' },
    { label: 'Configuración',  icon: 'settings',        ruta: '/propietario/configuracion' }
  ];

  gimnasio: Gimnasio | null = null;
  cargando = true;
  guardando = false;

  form = {
    nombre: '',
    email: '',
    direccion: '',
    ciudad: '',
    telefono: '',
    porcentajeIva: 0,
    logoUrl: ''
  };

  logoTabIndex = 0; // 0 = subir archivo, 1 = URL externa
  logoPreview: string | null = null;
  subiendoArchivo = false;

  constructor(
    private gimnasioService: GimnasioService,
    public contexto: GimnasioContextService,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    const gym = this.contexto.obtener();
    if (!gym) { this.cargando = false; return; }

    this.gimnasioService.obtener(gym.id).subscribe({
      next: g => {
        this.gimnasio = g;
        this.cargarForm(g);
        this.cargando = false;
      },
      error: () => {
        this.snack.open('Error al cargar datos del gimnasio', 'Cerrar', { duration: 3000 });
        this.cargando = false;
      }
    });
  }

  private cargarForm(g: Gimnasio): void {
    this.form.nombre       = g.nombre;
    this.form.email        = g.email ?? '';
    this.form.direccion    = g.direccion ?? '';
    this.form.ciudad       = g.ciudad ?? '';
    this.form.telefono     = g.telefono ?? '';
    this.form.porcentajeIva = g.porcentajeIva ?? 0;
    this.form.logoUrl      = g.logoUrl ?? '';
    this.logoPreview       = g.logoUrl ?? null;
  }

  onArchivoSeleccionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.snack.open('Solo se permiten archivos de imagen', 'Cerrar', { duration: 3000 });
      return;
    }

    if (file.size > 2 * 1024 * 1024) {
      this.snack.open('El archivo no puede superar 2 MB', 'Cerrar', { duration: 3000 });
      return;
    }

    this.subiendoArchivo = true;
    const reader = new FileReader();
    reader.onload = () => {
      const base64 = reader.result as string;
      this.form.logoUrl = base64;
      this.logoPreview = base64;
      this.subiendoArchivo = false;
    };
    reader.onerror = () => {
      this.snack.open('Error al leer el archivo', 'Cerrar', { duration: 3000 });
      this.subiendoArchivo = false;
    };
    reader.readAsDataURL(file);
  }

  onUrlCambiada(): void {
    this.logoPreview = this.form.logoUrl?.trim() || null;
  }

  eliminarLogo(): void {
    this.form.logoUrl = '';
    this.logoPreview = null;
  }

  guardar(): void {
    if (!this.gimnasio || !this.form.nombre.trim()) return;
    this.guardando = true;

    this.gimnasioService.actualizarDatos(this.gimnasio.id, {
      nombre:        this.form.nombre,
      email:         this.form.email || undefined,
      direccion:     this.form.direccion || undefined,
      ciudad:        this.form.ciudad || undefined,
      telefono:      this.form.telefono || undefined,
      porcentajeIva: this.form.porcentajeIva,
      logoUrl:       this.form.logoUrl || undefined
    }).subscribe({
      next: actualizado => {
        this.gimnasio = actualizado;
        this.contexto.seleccionar(actualizado);
        this.guardando = false;
        this.snack.open('Datos guardados correctamente', 'Cerrar', { duration: 3000 });
      },
      error: () => {
        this.snack.open('Error al guardar los cambios', 'Cerrar', { duration: 3000 });
        this.guardando = false;
      }
    });
  }
}
