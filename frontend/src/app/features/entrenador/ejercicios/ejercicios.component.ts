import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { LayoutComponent, NavItem } from '../../../shared/components/layout/layout.component';
import { EjercicioService } from '../../../core/services/ejercicio.service';
import { GimnasioContextService } from '../../../core/services/gimnasio-context.service';
import { Ejercicio } from '../../../core/models/ejercicio.model';

@Component({
  selector: 'app-ejercicios',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatDividerModule,
    LayoutComponent
  ],
  templateUrl: './ejercicios.component.html',
  styleUrl: './ejercicios.component.scss'
})
export class EjerciciosComponent implements OnInit {

  navItems: NavItem[] = [
    { label: 'Dashboard',       icon: 'dashboard',      ruta: '/entrenador/dashboard' },
    { label: 'Entrenamientos',  icon: 'fitness_center',  ruta: '/entrenador/entrenamientos' },
    { label: 'Ejercicios',      icon: 'sports_gymnastics', ruta: '/entrenador/ejercicios' }
  ];

  gruposMusculares = [
    'PECHO', 'ESPALDA', 'HOMBROS', 'BICEPS', 'TRICEPS',
    'ABDOMEN', 'GLUTEOS', 'PIERNAS', 'CARDIO', 'OTRO'
  ];

  dificultades = ['PRINCIPIANTE', 'INTERMEDIO', 'AVANZADO'];

  ejercicios: Ejercicio[] = [];
  cargando = true;
  creando = false;
  filtroGrupo = '';

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private ejercicioService: EjercicioService,
    public contexto: GimnasioContextService,
    private snack: MatSnackBar
  ) {
    this.form = this.fb.group({
      nombre:        ['', Validators.required],
      descripcion:   [''],
      grupoMuscular: ['', Validators.required],
      dificultad:    ['', Validators.required],
      videoUrl:      ['']
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    const id = this.contexto.obtenerIdOFail();
    this.cargando = true;
    this.ejercicioService.listarPorGimnasio(id).subscribe({
      next: data => { this.ejercicios = data; this.cargando = false; },
      error: () => { this.snack.open('Error al cargar ejercicios', 'Cerrar', { duration: 3000 }); this.cargando = false; }
    });
  }

  get ejerciciosFiltrados(): Ejercicio[] {
    if (!this.filtroGrupo) return this.ejercicios;
    return this.ejercicios.filter(e => e.grupoMuscular === this.filtroGrupo);
  }

  crear(): void {
    if (this.form.invalid) return;
    const id = this.contexto.obtenerIdOFail();
    this.ejercicioService.crear(id, this.form.value).subscribe({
      next: () => {
        this.snack.open('Ejercicio creado', 'Cerrar', { duration: 3000 });
        this.form.reset();
        this.creando = false;
        this.cargar();
      },
      error: () => this.snack.open('Error al crear ejercicio', 'Cerrar', { duration: 3000 })
    });
  }

  eliminar(ejercicio: Ejercicio): void {
    if (ejercicio.esPredefinido) {
      this.snack.open('No se pueden eliminar ejercicios predefinidos', 'Cerrar', { duration: 3000 });
      return;
    }
    const id = this.contexto.obtenerIdOFail();
    this.ejercicioService.eliminar(ejercicio.id).subscribe({
      next: () => {
        this.snack.open('Ejercicio eliminado', 'Cerrar', { duration: 3000 });
        this.cargar();
      },
      error: () => this.snack.open('Error al eliminar', 'Cerrar', { duration: 3000 })
    });
  }
}
