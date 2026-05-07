# 7. Tarea 3 — Desarrollo del frontend

El frontend de FuerzApp es una *Single Page Application* (SPA) desarrollada con **Angular 19.2** en su modalidad de componentes independientes (*Standalone Components*). Esta modalidad, estabilizada en Angular 17 como enfoque recomendado, elimina la necesidad de `NgModules` y permite que cada componente declare sus propias dependencias de forma explícita, facilitando el *lazy loading* y la comprensión del código.

La interfaz de usuario se construye con **Angular Material 19.2** y el tema **Azure Blue**, que proporciona una paleta de colores coherente y accesible basada en las directrices de Material Design 3. El lenguaje de estilos es **SCSS**, y la programación reactiva se maneja con **RxJS 7.8**.

---

## 7.1 Configuración del proyecto Angular

### 7.1.1 Inicialización y estructura

El proyecto Angular se inicializa con Angular CLI 19:

```bash
ng new frontend --standalone --routing --style=scss
```

La estructura de directorios del proyecto frontend sigue la arquitectura feature-based recomendada por Angular:

```
src/app/
├── core/
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   └── rol.guard.ts
│   ├── interceptors/
│   │   └── auth.interceptor.ts
│   ├── models/
│   │   ├── usuario.model.ts
│   │   ├── gimnasio.model.ts
│   │   ├── suscripcion.model.ts
│   │   ├── ejercicio.model.ts
│   │   ├── entrenamiento.model.ts
│   │   └── pago.model.ts
│   └── services/
│       ├── auth.service.ts
│       ├── gimnasio-context.service.ts
│       ├── gimnasio.service.ts
│       ├── suscripcion.service.ts
│       ├── ejercicio.service.ts
│       ├── entrenamiento.service.ts
│       └── pago.service.ts
├── features/
│   ├── auth/
│   │   └── login/
│   │       ├── login.component.ts
│   │       ├── login.component.html
│   │       └── login.component.scss
│   ├── admin/
│   │   ├── gimnasios/
│   │   └── alta-gimnasio-dialog/
│   ├── propietario/
│   │   ├── dashboard/
│   │   ├── entrenadores/
│   │   ├── clientes/
│   │   └── suscripciones/
│   ├── entrenador/
│   │   ├── dashboard/
│   │   ├── entrenamientos/
│   │   └── ejercicios/
│   └── cliente/
│       ├── dashboard/
│       ├── entrenamientos/
│       └── suscripcion/
└── shared/
    ├── layout/
    │   ├── sidebar/
    │   └── toolbar/
    └── pipes/
```

### 7.1.2 Configuración del tema Angular Material

Angular Material se instala con el esquema de configuración automatizada:

```bash
ng add @angular/material
```

Se selecciona el tema **Azure Blue** precompilado, que se aplica globalmente en `angular.json`:

```json
{
  "styles": [
    "@angular/material/prebuilt-themes/azure-blue.css",
    "src/styles.scss"
  ]
}
```

### 7.1.3 Configuración del HttpClient y providers globales

En Angular 19 con componentes standalone, los providers globales se configuran en `app.config.ts`:

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimationsAsync(),
  ],
};
```

El `authInterceptor` se registra aquí como interceptor funcional (la nueva API de interceptores de Angular 15+).

### 7.1.4 Enrutamiento

Las rutas de la aplicación se definen en `app.routes.ts` con *lazy loading* por feature:

```typescript
export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: 'admin',
    canActivate: [authGuard, rolGuard(['ADMIN_PLATAFORMA'])],
    loadChildren: () =>
      import('./features/admin/admin.routes').then(m => m.adminRoutes),
  },
  {
    path: 'propietario',
    canActivate: [authGuard, rolGuard(['PROPIETARIO'])],
    loadChildren: () =>
      import('./features/propietario/propietario.routes')
        .then(m => m.propietarioRoutes),
  },
  {
    path: 'entrenador',
    canActivate: [authGuard, rolGuard(['ENTRENADOR'])],
    loadChildren: () =>
      import('./features/entrenador/entrenador.routes')
        .then(m => m.entrenadorRoutes),
  },
  {
    path: 'cliente',
    canActivate: [authGuard, rolGuard(['CLIENTE'])],
    loadChildren: () =>
      import('./features/cliente/cliente.routes')
        .then(m => m.clienteRoutes),
  },
];
```

---

## 7.2 Módulo de autenticación

### 7.2.1 AuthService

El servicio de autenticación (`AuthService`) es el servicio singleton más importante de la aplicación. Gestiona el ciclo de vida de la sesión del usuario:

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'fuerzapp_token';
  private readonly USER_KEY = 'fuerzapp_user';

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`,
        { email, password })
      .pipe(
        tap(response => {
          localStorage.setItem(this.TOKEN_KEY, response.token);
          localStorage.setItem(this.USER_KEY, JSON.stringify(response));
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }

  get currentUser(): LoginResponse | null {
    const stored = localStorage.getItem(this.USER_KEY);
    return stored ? JSON.parse(stored) : null;
  }

  get token(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return this.token !== null;
  }
}
```

La respuesta de login (`LoginResponse`) incluye los campos `token`, `id`, `nombre`, `apellidos`, `email` y `rol`. El campo `id` es especialmente importante porque muchos endpoints del backend requieren el identificador del usuario (por ejemplo, `/api/pagos/cliente/{clienteId}/historial`).

### 7.2.2 AuthInterceptor

El interceptor de autenticación añade el token JWT a todas las peticiones HTTP salientes y gestiona el logout automático ante respuestas 401:

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.token;

  const authReq = token
    ? req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      })
    : req;

  return next(authReq).pipe(
    catchError(error => {
      if (error.status === 401) {
        authService.logout();
      }
      return throwError(() => error);
    })
  );
};
```

### 7.2.3 Guards de ruta

**authGuard:** Verifica que el usuario tiene una sesión activa. Si no, redirige al login:

```typescript
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }
  return router.parseUrl('/login');
};
```

**rolGuard:** Verifica que el rol del usuario coincide con los roles permitidos para la ruta:

```typescript
export const rolGuard = (rolesPermitidos: string[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const user = authService.currentUser;

    if (user && rolesPermitidos.includes(user.rol)) {
      return true;
    }
    return router.parseUrl('/login');
  };
};
```

### 7.2.4 Componente de login

El componente de login presenta un formulario reactivo con validación en tiempo real:

```typescript
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './login.component.html',
})
export class LoginComponent {

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
  });

  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    const { email, password } = this.loginForm.value;
    this.authService.login(email!, password!).subscribe({
      next: (user) => {
        const routes: Record<string, string> = {
          ADMIN_PLATAFORMA: '/admin/gimnasios',
          PROPIETARIO: '/propietario/dashboard',
          ENTRENADOR: '/entrenador/dashboard',
          CLIENTE: '/cliente/dashboard',
        };
        this.router.navigate([routes[user.rol] || '/login']);
      },
      error: () => {
        this.errorMessage = 'Credenciales incorrectas';
      },
    });
  }
}
```

---

## 7.3 Panel de administrador de plataforma

### 7.3.1 GimnasioContextService

El `GimnasioContextService` es un servicio singleton que gestiona el gimnasio activo seleccionado por el usuario. Usa un `BehaviorSubject` para emitir cambios reactivos que los componentes pueden suscribirse, y persiste el gimnasio activo en `localStorage` para sobrevivir a recargas de página:

```typescript
@Injectable({ providedIn: 'root' })
export class GimnasioContextService {

  private readonly STORAGE_KEY = 'gimnasio_activo';
  private gimnasioActivo$ = new BehaviorSubject<Gimnasio | null>(
    this.loadFromStorage()
  );

  get gimnasio$(): Observable<Gimnasio | null> {
    return this.gimnasioActivo$.asObservable();
  }

  setGimnasio(gimnasio: Gimnasio): void {
    this.gimnasioActivo$.next(gimnasio);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(gimnasio));
  }

  obtenerIdOFail(): number {
    const gimnasio = this.gimnasioActivo$.value;
    if (!gimnasio) {
      throw new Error('No hay gimnasio activo seleccionado');
    }
    return gimnasio.id;
  }

  private loadFromStorage(): Gimnasio | null {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : null;
  }
}
```

### 7.3.2 Componente Gimnasios (Admin)

El componente de gestión de gimnasios del panel de administrador muestra una tabla Material con todos los gimnasios de la plataforma y permite crear nuevos:

El componente utiliza `MatTable` con columnas configuradas, `MatPaginator` para la paginación y `MatDialog` para el diálogo de alta. La carga de datos se realiza al inicializar el componente mediante `ngOnInit` y se refresca tras cada operación de creación o cambio de estado.

```typescript
@Component({
  selector: 'app-gimnasios',
  standalone: true,
  imports: [
    MatTableModule, MatButtonModule, MatDialogModule,
    MatChipsModule, MatIconModule, CommonModule,
  ],
  templateUrl: './gimnasios.component.html',
})
export class GimnasiosComponent implements OnInit {

  displayedColumns = ['nombre', 'propietario', 'ciudad', 'estado',
    'entrenadores', 'clientes', 'acciones'];
  dataSource: Gimnasio[] = [];

  constructor(
    private gimnasioService: GimnasioService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.cargarGimnasios();
  }

  cargarGimnasios(): void {
    this.gimnasioService.listarTodosAdmin().subscribe(
      gimnasios => this.dataSource = gimnasios
    );
  }

  abrirDialogoAlta(): void {
    const dialogRef = this.dialog.open(AltaGimnasioDialogComponent, {
      width: '600px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe(resultado => {
      if (resultado) this.cargarGimnasios();
    });
  }

  toggleEstado(gimnasio: Gimnasio): void {
    this.gimnasioService
      .cambiarEstado(gimnasio.id, !gimnasio.activo)
      .subscribe(() => this.cargarGimnasios());
  }
}
```

### 7.3.3 Diálogo de alta de gimnasio (MatStepper)

El diálogo de alta de gimnasio usa un `MatStepper` de dos pasos para guiar al administrador en el proceso:

**Paso 1 — Datos del gimnasio:** Nombre, dirección, teléfono, email de contacto.
**Paso 2 — Datos del propietario:** Nombre, apellidos, email (debe ser único en el sistema), contraseña provisional.

```html
<mat-stepper #stepper>
  <mat-step [stepControl]="datosGimnasioForm" label="Datos del gimnasio">
    <form [formGroup]="datosGimnasioForm">
      <mat-form-field appearance="outline">
        <mat-label>Nombre del gimnasio</mat-label>
        <input matInput formControlName="nombre" />
        <mat-error *ngIf="datosGimnasioForm.get('nombre')?.hasError('required')">
          Campo obligatorio
        </mat-error>
      </mat-form-field>
      <!-- ... más campos -->
      <button mat-button matStepperNext
              [disabled]="datosGimnasioForm.invalid">
        Siguiente
      </button>
    </form>
  </mat-step>

  <mat-step [stepControl]="datosPropietarioForm" label="Datos del propietario">
    <form [formGroup]="datosPropietarioForm">
      <!-- ... campos del propietario -->
      <button mat-button matStepperPrevious>Atrás</button>
      <button mat-raised-button color="primary"
              (click)="crearGimnasio()"
              [disabled]="datosPropietarioForm.invalid">
        Crear gimnasio
      </button>
    </form>
  </mat-step>
</mat-stepper>
```

---

## 7.4 Panel de propietario de gimnasio

### 7.4.1 Dashboard del propietario

El dashboard del propietario muestra un selector de gimnasio (si el propietario tiene varios) y tarjetas de estadísticas. La selección del gimnasio activo actualiza el `GimnasioContextService` y propaga el cambio a todos los componentes que dependen del gimnasio activo.

La lógica de auto-selección es importante: si el propietario solo tiene un gimnasio, se selecciona automáticamente sin mostrar el selector. Si tiene varios, se muestra un `MatSelect` con todos sus gimnasios.

### 7.4.2 Gestión de entrenadores y clientes

Los componentes `EntrenadoresComponent` y `ClientesComponent` tienen una estructura similar: una tabla Material con el listado de personas vinculadas al gimnasio activo, un campo de búsqueda por email para añadir nuevas personas y un botón de eliminación por cada fila.

El avatar de iniciales se genera dinámicamente a partir del nombre del usuario:

```typescript
getIniciales(nombre: string, apellidos: string): string {
  return `${nombre.charAt(0)}${apellidos.charAt(0)}`.toUpperCase();
}
```

### 7.4.3 Gestión de suscripciones

El componente de suscripciones del panel de propietario organiza la información en tres pestañas (`MatTabs`):

**Pestaña 1 — Tipos de suscripción:** Lista los tipos creados con su precio y duración, y un formulario inline para crear nuevos.

**Pestaña 2 — Extras:** Lista los extras disponibles vinculados a los tipos de suscripción, con formulario de creación.

**Pestaña 3 — Suscriptores activos:** Tabla con los clientes que tienen suscripción activa, mostrando el tipo de suscripción, la fecha de inicio, la fecha de fin y los extras contratados.

---

## 7.5 Panel de entrenador

### 7.5.1 Dashboard del entrenador

El dashboard del entrenador sigue el mismo patrón de selector de gimnasio que el del propietario. Además, muestra estadísticas relevantes para el entrenador: número de clientes asignados, número de sesiones programadas próximas y número de planes activos.

### 7.5.2 Gestión de entrenamientos

El componente de entrenamientos del panel de entrenador implementa un layout de dos columnas que maximiza el uso del espacio de pantalla:

**Columna izquierda:** Lista de planes de entrenamiento del gimnasio activo (con posibilidad de filtrar por tipo) y formulario de creación de nuevo plan en la parte inferior.

**Columna derecha:** Detalle del plan seleccionado, con tres pestañas:
- *Ejercicios:* Lista de ejercicios del plan con sus series, repeticiones y peso. Permite añadir nuevos ejercicios.
- *Sesiones:* Lista de sesiones programadas con fecha, hora y nº de inscritos. Permite crear nuevas sesiones (deshabilitado para planes ESPECIFICO).
- *Asignar:* Para planes ESPECIFICO, permite buscar un cliente por email y asignarle el plan.

```typescript
@Component({
  selector: 'app-entrenamientos',
  standalone: true,
  imports: [
    MatSidenavModule, MatListModule, MatTabsModule,
    MatCardModule, MatButtonModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, ReactiveFormsModule, CommonModule,
  ],
  templateUrl: './entrenamientos.component.html',
})
export class EntrenamientosComponent implements OnInit {

  entrenamientos: Entrenamiento[] = [];
  entrenamientoSeleccionado: Entrenamiento | null = null;
  gimnasioId!: number;

  crearForm = new FormGroup({
    nombre: new FormControl('', Validators.required),
    descripcion: new FormControl(''),
    tipo: new FormControl<TipoEntrenamiento | null>(null, Validators.required),
  });

  constructor(
    private entrenamientoService: EntrenamientoService,
    private gimnasioContext: GimnasioContextService,
  ) {}

  ngOnInit(): void {
    this.gimnasioId = this.gimnasioContext.obtenerIdOFail();
    this.cargarEntrenamientos();
  }

  seleccionar(entrenamiento: Entrenamiento): void {
    this.entrenamientoSeleccionado = entrenamiento;
  }

  esTipoEspecifico(): boolean {
    return this.entrenamientoSeleccionado?.tipo === 'ESPECIFICO';
  }
}
```

### 7.5.3 Gestión de ejercicios

El componente de ejercicios presenta una rejilla de tarjetas (`MatCard`) en lugar de una tabla, para una experiencia visual más rica. Cada tarjeta muestra el nombre del ejercicio, la descripción, el grupo muscular y, en el caso de ejercicios propios del entrenador, un botón de eliminación.

Un selector de grupo muscular permite filtrar los ejercicios mostrados. Los ejercicios predefinidos de la plataforma se distinguen visualmente mediante una etiqueta `MatChip` de color diferente.

---

## 7.6 Panel de cliente

### 7.6.1 Dashboard del cliente

El dashboard del cliente es el panel más visual. Muestra tres tarjetas principales:

- **Suscripción activa:** Nombre del plan, fecha de expiración y estado con indicador de color.
- **Próximas sesiones:** Las 3 sesiones más cercanas programadas para el cliente.
- **Planes pendientes:** Los planes específicos asignados al cliente que aún no ha completado.

El `GimnasioContextService` gestiona el gimnasio activo también para el cliente. En el caso del cliente, si solo pertenece a un gimnasio, la carga es automática al inicializar el dashboard, sin necesidad de selector.

### 7.6.2 Entrenamientos del cliente

El componente de entrenamientos del cliente organiza la información en tres pestañas:

**Sesiones individuales:** Lista las sesiones de planes INDIVIDUAL a las que el cliente está inscrito, con fecha, hora y entrenador.

**Sesiones grupales:** Lista las sesiones de planes GRUPAL disponibles o en las que el cliente está inscrito, con indicador de plazas disponibles.

**Planes específicos:** Lista los planes ESPECIFICO asignados al cliente. Para cada plan, muestra los ejercicios incluidos y un botón "Marcar como completado" si el plan está en estado PENDIENTE.

```typescript
marcarCompletado(asignacionId: number): void {
  this.entrenamientoService
    .marcarEspecificoCompletado(asignacionId)
    .subscribe(() => {
      this.cargarPlanesEspecificos();
    });
}
```

### 7.6.3 Suscripción del cliente y flujo de pago Stripe

El componente de suscripción del cliente es el más complejo del panel de cliente, ya que integra el flujo de pago con Stripe:

```typescript
@Component({
  selector: 'app-suscripcion',
  standalone: true,
  imports: [
    MatCardModule, MatButtonModule, MatTableModule,
    MatChipsModule, MatDividerModule, CommonModule,
  ],
  templateUrl: './suscripcion.component.html',
})
export class SuscripcionComponent implements OnInit {

  suscripcionActiva: ClienteSuscripcion | null = null;
  historialPagos: Pago[] = [];
  tiposSuscripcion: TipoSuscripcion[] = [];

  constructor(
    private suscripcionService: SuscripcionService,
    private pagoService: PagoService,
    private authService: AuthService,
    private gimnasioContext: GimnasioContextService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    // Detectar retorno desde Stripe Checkout
    this.route.queryParams.subscribe(params => {
      if (params['pago'] === 'exitoso') {
        // Mostrar notificación de éxito y recargar datos
        this.mostrarExitoYRecargar();
      }
    });
    this.cargarDatos();
  }

  iniciarCheckout(tipoSuscripcionId: number): void {
    const clienteId = this.authService.currentUser!.id;
    const gimnasioId = this.gimnasioContext.obtenerIdOFail();

    this.pagoService
      .crearCheckoutSession(clienteId, gimnasioId, tipoSuscripcionId)
      .subscribe(url => {
        window.location.href = url;  // Redirige a Stripe Checkout
      });
  }

  descargarFactura(facturaId: number): void {
    this.pagoService.descargarFacturaPdf(facturaId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `factura-${facturaId}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
```

El flujo completo de pago es:
1. El cliente selecciona un tipo de suscripción y hace clic en "Contratar".
2. El frontend solicita al backend la creación de una sesión de Stripe Checkout.
3. El backend crea la sesión con los metadatos necesarios (clienteId, gimnasioId, tipoSuscripcionId) y devuelve la URL de Stripe.
4. El frontend redirige el navegador a la URL de Stripe Checkout.
5. El cliente completa el pago en la página de Stripe (en modo test, con tarjeta de prueba).
6. Stripe notifica al backend mediante webhook el evento `checkout.session.completed`.
7. El backend procesa el pago, activa la suscripción y genera la factura.
8. Stripe redirige al usuario de vuelta al frontend con el parámetro `?pago=exitoso`.
9. El componente detecta el parámetro y muestra una notificación de éxito, recargando los datos.

---

## Tabla de duración — Tarea 3

| Subtarea | Descripción | Duración estimada |
|----------|-------------|-------------------|
| 7.1 | Configuración del proyecto Angular | 8 horas |
| 7.2 | Módulo de autenticación | 10 horas |
| 7.3 | Panel de administrador | 14 horas |
| 7.4 | Panel de propietario | 18 horas |
| 7.5 | Panel de entrenador | 20 horas |
| 7.6 | Panel de cliente | 18 horas |
| **Total Tarea 3** | | **88 horas** |

---
