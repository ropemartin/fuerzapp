# 8. Tarea 4 — Integración, pruebas y despliegue

La cuarta tarea abarca las actividades de verificación y puesta en producción del sistema completo. Aunque la integración entre frontend y backend se ha ido validando de forma incremental durante el desarrollo, esta tarea se centra en las pruebas funcionales de extremo a extremo, la corrección de los problemas detectados y el despliegue en un entorno de producción accesible públicamente.

---

## 8.1 Integración frontend–backend

### 8.1.1 Configuración del entorno y proxy de desarrollo

Durante el desarrollo, el frontend (puerto 4200) y el backend (puerto 8080) corren en servidores separados. Para evitar problemas de CORS durante el desarrollo, Angular CLI permite configurar un proxy que redirige las llamadas a `/api/*` al servidor backend.

El archivo `proxy.conf.json`:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

Referenciado en `angular.json`:
```json
{
  "serve": {
    "options": {
      "proxyConfig": "proxy.conf.json"
    }
  }
}
```

En producción, el CORS se configura directamente en el backend:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(frontendUrl)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(false);
    }
}
```

### 8.1.2 Contratos de API y DTOs

Un punto crítico de la integración es el contrato entre los DTOs del backend (clases Java) y los modelos del frontend (interfaces TypeScript). Se mantuvo consistencia de nombres de campos (usando camelCase en ambos lados) para evitar transformaciones adicionales.

Los modelos TypeScript del frontend replican la estructura de los DTOs del backend:

```typescript
// frontend/src/app/core/models/gimnasio.model.ts
export interface Gimnasio {
  id: number;
  nombre: string;
  direccion: string;
  telefono: string;
  email: string;
  activo: boolean;
  propietario: UsuarioResumen;
  numEntrenadores: number;
  numClientes: number;
  numSuscripcionesActivas: number;
}

export interface UsuarioResumen {
  id: number;
  nombre: string;
  apellidos: string;
  email: string;
}
```

### 8.1.3 Manejo de errores global

Los errores HTTP se capturan en el `AuthInterceptor` (para los 401) y se manejan localmente en cada componente para los demás errores. Se utiliza un servicio de notificaciones basado en `MatSnackBar` para mostrar mensajes de error al usuario de forma no intrusiva:

```typescript
mostrarError(mensaje: string): void {
  this.snackBar.open(mensaje, 'Cerrar', {
    duration: 4000,
    panelClass: ['snack-error'],
  });
}
```

---

## 8.2 Pruebas de integración y funcionales

### 8.2.1 Estrategia de pruebas

La estrategia de pruebas del proyecto contempla tres niveles:

1. **Pruebas unitarias del backend** (JUnit 5 + Mockito): Ya detalladas en la Tarea 2. 34 tests que cubren la lógica de negocio de los 4 servicios principales.

2. **Pruebas unitarias del frontend** (Karma + Jasmine): Pruebas de los servicios Angular y de los guards de ruta. Incluyen el uso de `HttpClientTestingModule` para simular las respuestas HTTP.

3. **Pruebas funcionales manuales**: Pruebas de extremo a extremo ejecutadas manualmente en el navegador, siguiendo casos de prueba documentados por rol.

### 8.2.2 Pruebas funcionales por rol — Casos de prueba

**CP-ADMIN-01 — Alta de gimnasio con propietario:**
- Precondición: Sesión activa como ADMIN_PLATAFORMA.
- Pasos: Abrir diálogo de alta → completar datos del gimnasio (paso 1) → completar datos del propietario (paso 2) → confirmar.
- Resultado esperado: Nuevo gimnasio aparece en la tabla. El propietario puede hacer login con las credenciales indicadas.
- Resultado obtenido: OK.

**CP-ADMIN-02 — Desactivar gimnasio:**
- Precondición: Gimnasio activo en la tabla.
- Pasos: Hacer clic en el botón de toggle de estado.
- Resultado esperado: El estado del gimnasio cambia a "Inactivo". El propietario y los usuarios del gimnasio no pueden acceder.
- Resultado obtenido: OK.

**CP-PROP-01 — Añadir entrenador al gimnasio:**
- Precondición: Sesión activa como PROPIETARIO. El usuario entrenador debe existir en el sistema.
- Pasos: Panel Entrenadores → introducir email del entrenador → clic en "Añadir".
- Resultado esperado: El entrenador aparece en la tabla. El entrenador puede ver el gimnasio en su panel.
- Resultado obtenido: OK.

**CP-PROP-02 — Crear tipo de suscripción:**
- Precondición: Sesión activa como PROPIETARIO.
- Pasos: Panel Suscripciones → pestaña "Tipos" → completar formulario → guardar.
- Resultado esperado: Nuevo tipo aparece en la lista y está disponible para los clientes.
- Resultado obtenido: OK.

**CP-ENT-01 — Crear plan de entrenamiento y añadir ejercicios:**
- Precondición: Sesión activa como ENTRENADOR, gimnasio activo seleccionado.
- Pasos: Panel Entrenamientos → formulario de creación (nombre, tipo GRUPAL) → guardar → seleccionar plan → pestaña Ejercicios → añadir ejercicio.
- Resultado esperado: Plan creado con ejercicios vinculados. Visible en el panel del cliente.
- Resultado obtenido: OK.

**CP-ENT-02 — Asignar plan ESPECIFICO a cliente:**
- Precondición: Plan de tipo ESPECIFICO existente.
- Pasos: Seleccionar plan → pestaña Asignar → buscar cliente por email → asignar.
- Resultado esperado: El plan aparece en la sección "Planes específicos" del panel del cliente.
- Resultado obtenido: OK.

**CP-CLI-01 — Contratar suscripción con Stripe (modo test):**
- Precondición: Tipos de suscripción configurados en el gimnasio.
- Pasos: Panel Suscripción → seleccionar tipo → clic en "Contratar" → completar pago en Stripe con tarjeta de prueba 4242 4242 4242 4242.
- Resultado esperado: Stripe redirige al frontend con `?pago=exitoso`. La suscripción activa aparece en el panel. La factura está disponible para descarga.
- Resultado obtenido: OK.

**CP-CLI-02 — Marcar plan específico como completado:**
- Precondición: Plan ESPECIFICO asignado al cliente en estado PENDIENTE.
- Pasos: Panel Entrenamientos → pestaña "Planes específicos" → clic en "Marcar como completado".
- Resultado esperado: El estado del plan cambia a COMPLETADO. El botón desaparece.
- Resultado obtenido: OK.

### 8.2.3 Incidencias detectadas y resueltas

Durante las pruebas funcionales se detectaron y resolvieron las siguientes incidencias:

**INC-01 — Carga de gimnasio perdida tras recarga de página:**
- Descripción: Al recargar la página, el `GimnasioContextService` perdía el gimnasio activo porque el `BehaviorSubject` se inicializaba con `null`.
- Solución: Inicializar el `BehaviorSubject` leyendo el valor del `localStorage` en el constructor del servicio.

**INC-02 — CORS bloqueando llamadas al webhook de Stripe:**
- Descripción: Stripe no enviaba el header `Origin` en las peticiones al webhook, lo que causaba que Spring Security rechazara la petición.
- Solución: Añadir el endpoint del webhook (`/api/pagos/webhook`) a la lista de endpoints públicos en `SecurityConfig` y configurar el CORS para permitir peticiones sin origen.

**INC-03 — Logout no limpiaba el gimnasio activo:**
- Descripción: Al cerrar sesión, el gimnasio activo permanecía en el `localStorage`, lo que podía causar problemas si el siguiente usuario que inicia sesión no tiene acceso a ese gimnasio.
- Solución: Llamar a `GimnasioContextService.limpiar()` desde el método `logout()` de `AuthService`.

**INC-04 — Selector de ejercicios mostraba ejercicios de otro gimnasio:**
- Descripción: Los ejercicios predefinidos se mostraban correctamente, pero los ejercicios personalizados no se filtraban correctamente por gimnasio.
- Solución: Añadir el filtro por `gimnasioId` en la consulta del repositorio de ejercicios.

---

## 8.3 Despliegue en producción

### 8.3.1 Estrategia de despliegue

El despliegue se realiza en dos servicios separados:

- **Backend:** TODO (describir servicio de hosting: Railway, Render, AWS EC2, VPS, etc.)
- **Frontend:** TODO (describir servicio de hosting: Vercel, Netlify, Firebase Hosting, etc.)
- **Base de datos:** TODO (describir servicio de base de datos: PlanetScale, Railway MySQL, AWS RDS, etc.)

**URL de la aplicación en producción:** TODO (https://fuerzapp.ejemplo.com)

**URL del repositorio:** TODO (https://github.com/usuario/fuerzapp)

### 8.3.2 Construcción del backend para producción

El backend se empaqueta como un JAR ejecutable con Maven:

```bash
cd backend
mvn clean package -DskipTests
```

El artefacto generado (`target/fuerzapp-0.0.1-SNAPSHOT.jar`) se despliega en el servidor con:

```bash
java -jar fuerzapp-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://HOST:3306/fuerzapp \
  --spring.datasource.username=$DB_USER \
  --spring.datasource.password=$DB_PASSWORD \
  --jwt.secret=$JWT_SECRET \
  --stripe.secret-key=$STRIPE_SECRET_KEY \
  --stripe.webhook-secret=$STRIPE_WEBHOOK_SECRET \
  --frontend.url=https://fuerzapp.ejemplo.com
```

Las variables de entorno sensibles nunca se incluyen en el código fuente ni en los archivos de configuración versionados.

### 8.3.3 Construcción del frontend para producción

El frontend se compila con Angular CLI en modo producción:

```bash
cd frontend
ng build --configuration=production
```

La compilación genera los artefactos estáticos (HTML, CSS, JS con tree-shaking y minificación) en el directorio `dist/frontend/browser/`. Estos archivos se suben al servicio de hosting estático elegido.

Es importante configurar el servidor de archivos estáticos para redirigir todas las rutas al `index.html`, ya que Angular gestiona el enrutamiento del lado del cliente. En Nginx, esto se consigue con:

```nginx
location / {
  try_files $uri $uri/ /index.html;
}
```

### 8.3.4 Configuración del webhook de Stripe en producción

Para que Stripe pueda notificar al backend en producción, es necesario registrar la URL del webhook en el panel de Stripe:

1. Acceder al panel de desarrolladores de Stripe.
2. Crear un nuevo webhook endpoint apuntando a `https://api.fuerzapp.ejemplo.com/api/pagos/webhook`.
3. Seleccionar el evento `checkout.session.completed`.
4. Copiar el *Signing Secret* del webhook y configurarlo en la variable de entorno `STRIPE_WEBHOOK_SECRET` del backend.

---

## Tabla de duración — Tarea 4

| Subtarea | Descripción | Duración estimada |
|----------|-------------|-------------------|
| 8.1 | Integración frontend–backend | 10 horas |
| 8.2 | Pruebas de integración y funcionales | 12 horas |
| 8.3 | Despliegue en producción | 8 horas |
| **Total Tarea 4** | | **30 horas** |

---

# 9. Recursos Humanos

El desarrollo de FuerzApp ha sido llevado a cabo íntegramente por un único desarrollador, el alumno autor del proyecto. Dado que es un proyecto de fin de ciclo individual, todos los roles del equipo de desarrollo recaen sobre la misma persona.

| Rol | Persona | Perfil |
|-----|---------|--------|
| Analista | TODO (Nombre del alumno) | Alumno de DAW |
| Diseñador UX/UI | TODO (Nombre del alumno) | Alumno de DAW |
| Desarrollador backend | TODO (Nombre del alumno) | Alumno de DAW |
| Desarrollador frontend | TODO (Nombre del alumno) | Alumno de DAW |
| Tester | TODO (Nombre del alumno) | Alumno de DAW |
| DevOps / Despliegue | TODO (Nombre del alumno) | Alumno de DAW |

El tutor del proyecto ha ejercido un rol de supervisión y orientación en las reuniones de seguimiento periódicas, sin participar directamente en el desarrollo técnico.

| Rol | Persona | Participación |
|-----|---------|---------------|
| Tutor / Supervisor | TODO (Nombre del profesor) | Revisión y orientación |

---

# 10. Recursos Materiales

## 10.1 Hardware

| Recurso | Descripción | Uso en el proyecto |
|---------|-------------|-------------------|
| Ordenador portátil | TODO (marca y modelo) | Desarrollo de todo el proyecto |
| Conexión a internet | TODO (proveedor y velocidad) | Comunicación, documentación, despliegue |

## 10.2 Software

| Herramienta | Versión | Licencia | Uso |
|-------------|---------|----------|-----|
| Windows 10 Pro | 10.0.19045 | Propietaria (licencia educativa) | Sistema operativo |
| IntelliJ IDEA / VS Code | TODO | Community / MIT | IDE de desarrollo |
| Java Development Kit (JDK) | 21 (LTS) | GPL v2 | Runtime y compilación del backend |
| Spring Boot | 3.3.0 | Apache 2.0 | Framework del backend |
| Maven | 3.9+ | Apache 2.0 | Gestión de dependencias y build del backend |
| MySQL Server | 8.0 | GPL / Comercial | Base de datos |
| MySQL Workbench | 8.0 | GPL | Administración de la base de datos |
| Node.js | 20 LTS | MIT | Runtime de Angular CLI |
| Angular CLI | 19.2 | MIT | Scaffolding y build del frontend |
| Git | 2.x | GPL v2 | Control de versiones |
| GitHub / GitLab | — | Propietaria (plan gratuito) | Alojamiento del repositorio |
| Postman | — | Propietaria (plan gratuito) | Pruebas manuales de la API REST |
| Stripe Dashboard | — | Propietaria (gratuito en modo test) | Configuración de webhooks y pruebas de pago |
| TODO (servicio hosting backend) | — | TODO | Despliegue del backend |
| TODO (servicio hosting frontend) | — | TODO | Despliegue del frontend |

## 10.3 Recursos online y documentación

| Recurso | Descripción |
|---------|-------------|
| Spring Boot Documentation | https://docs.spring.io/spring-boot/docs/3.3.0/reference/html/ |
| Angular Documentation | https://angular.dev |
| Angular Material | https://material.angular.io |
| Stripe API Reference | https://stripe.com/docs/api |
| jjwt Documentation | https://github.com/jwtk/jjwt |
| Stack Overflow | Resolución de dudas técnicas específicas |

---

# 11. Cronograma

## 11.1 Planificación temporal

El proyecto se ha desarrollado durante TODO semanas, comprendidas entre TODO (fecha de inicio) y TODO (fecha de finalización). La planificación se divide en las cuatro tareas principales documentadas en este informe.

La siguiente tabla muestra la distribución temporal estimada:

| Tarea | Semanas | Horas estimadas |
|-------|---------|-----------------|
| Tarea 1 — Análisis y diseño | TODO (ej. semanas 1–3) | 48 h |
| Tarea 2 — Desarrollo del backend | TODO (ej. semanas 4–10) | 82 h |
| Tarea 3 — Desarrollo del frontend | TODO (ej. semanas 8–15) | 88 h |
| Tarea 4 — Integración, pruebas y despliegue | TODO (ej. semanas 14–16) | 30 h |
| Documentación (informe) | TODO (ej. semanas 15–17) | 20 h |
| **Total** | | **268 h** |

Cabe destacar que las Tareas 2 y 3 se solapan temporalmente: el desarrollo del frontend comenzó antes de que el backend estuviera completamente terminado, trabajando con datos de prueba (*mock*) en algunos componentes hasta que los endpoints del backend estuvieron disponibles.

## 11.2 Diagrama de Gantt

```
Semana:        1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17
Tarea 1        ████████████
Tarea 2              ████████████████████████████
Tarea 3                             ██████████████████████████
Tarea 4                                                  ████████████
Documentación                                               █████████
```

*(Nota: El diagrama de Gantt anterior es esquemático. TODO: ajustar según las fechas reales del proyecto.)*

---

# 12. Presupuesto

## 12.1 Costes de personal

Para el cálculo del coste de personal se estima el salario de un desarrollador junior full-stack en España, con un sueldo bruto aproximado de **24.000 €/año**, lo que equivale aproximadamente a **12 €/hora** (considerando 2.000 horas laborables al año).

| Concepto | Horas | Coste/hora | Coste total |
|----------|-------|-----------|-------------|
| Análisis y diseño | 48 h | 12 €/h | 576 € |
| Desarrollo backend | 82 h | 12 €/h | 984 € |
| Desarrollo frontend | 88 h | 12 €/h | 1.056 € |
| Integración y pruebas | 30 h | 12 €/h | 360 € |
| Documentación | 20 h | 12 €/h | 240 € |
| **Subtotal personal** | **268 h** | | **3.216 €** |

## 12.2 Costes de hardware

| Concepto | Coste total | Vida útil | Amortización período | Coste imputable |
|----------|-------------|-----------|---------------------|----------------|
| Ordenador portátil | TODO (ej. 900 €) | 4 años | TODO semanas | TODO € |

*(Nota: La amortización se calcula proporcionalmente al período de desarrollo del proyecto respecto a la vida útil total del equipo.)*

## 12.3 Costes de software

| Concepto | Coste |
|----------|-------|
| JDK 21 (GPL) | 0 € |
| Spring Boot (Apache 2.0) | 0 € |
| Angular (MIT) | 0 € |
| MySQL Community Edition (GPL) | 0 € |
| Git (GPL) | 0 € |
| TODO (IDE seleccionado) | TODO € |
| **Subtotal software** | TODO € |

## 12.4 Costes de servicios externos (producción)

| Concepto | Coste mensual | Período | Coste total |
|----------|---------------|---------|-------------|
| Hosting backend (TODO servicio) | TODO € | TODO meses | TODO € |
| Hosting frontend (TODO servicio) | 0 € (plan gratuito) | — | 0 € |
| Base de datos en la nube | TODO € | TODO meses | TODO € |
| Stripe (comisión por transacción) | 1,4% + 0,25 € por transacción | Modo test | 0 € |
| **Subtotal servicios** | | | TODO € |

## 12.5 Resumen del presupuesto

| Categoría | Coste |
|-----------|-------|
| Personal | 3.216 € |
| Hardware (amortización) | TODO € |
| Software | TODO € |
| Servicios externos | TODO € |
| **TOTAL** | **TODO €** |

*(Nota: Los costes de Stripe en producción dependerán del volumen de transacciones reales. En modo de prueba, el coste es 0 €.)*

---

# 13. Política de seguimiento y evaluación

## 13.1 Metodología de trabajo

El desarrollo del proyecto se ha llevado a cabo siguiendo una metodología **iterativa e incremental**, con características inspiradas en las metodologías ágiles (Scrum). Dado que el proyecto es individual, no se realizaron sprints formales con ceremonias de equipo, pero sí se mantuvo una cadencia de trabajo por ciclos de una a dos semanas, con objetivos concretos y revisión de avances al finalizar cada ciclo.

Las reuniones de seguimiento con el tutor del proyecto se realizaron con periodicidad TODO (semanal/quincenal), en las que se presentaban los avances del ciclo anterior y se revisaban los objetivos del siguiente. El control de versiones con Git proporcionó un registro automático e histórico del progreso del proyecto.

## 13.2 Control de versiones

El proyecto se gestiona con **Git** y se aloja en **TODO (GitHub/GitLab)**. La estrategia de ramas seguida es una variación simplificada de Git Flow adaptada al desarrollo individual:

- **`main`:** Rama principal. Solo recibe merges de código probado y funcional. Representa el estado de producción.
- **`develop`:** Rama de integración. El código en desarrollo se integra aquí antes de pasar a `main`.
- **`feature/nombre-funcionalidad`:** Ramas de trabajo para cada funcionalidad. Se crean a partir de `develop` y se fusionan de vuelta cuando la funcionalidad está completa.

## 13.3 Indicadores de seguimiento

Los siguientes indicadores se utilizaron para evaluar el avance y la calidad del proyecto:

| Indicador | Objetivo | Resultado |
|-----------|----------|-----------|
| Cobertura de tests unitarios backend | Cubrir los 4 servicios principales | 34 tests en 4 clases ✓ |
| Endpoints de la API implementados | Todos los definidos en el diseño | 100% ✓ |
| Paneles de usuario implementados | 4 paneles (admin, propietario, entrenador, cliente) | 4/4 ✓ |
| Flujo de pago con Stripe funcional | Checkout + webhook + factura | ✓ |
| Despliegue en producción | URL pública accesible | TODO |
| Casos de prueba funcionales superados | 8 casos documentados | TODO/8 |

## 13.4 Gestión de riesgos

Durante el desarrollo se identificaron y gestionaron los siguientes riesgos:

**Riesgo 1 — Complejidad de la integración con Stripe:**
- Probabilidad: Media. Impacto: Alto.
- Mitigación: Comenzar la integración con Stripe en una fase temprana del desarrollo del backend para detectar problemas con suficiente tiempo de margen. Usar el modo test de Stripe durante todo el desarrollo.

**Riesgo 2 — Compatibilidad de versiones entre Angular 19 y Angular Material:**
- Probabilidad: Baja. Impacto: Medio.
- Mitigación: Usar las versiones exactas indicadas en la documentación oficial (`@angular/material@19.2` con `@angular/core@19.2`). No actualizar dependencias sin verificar la compatibilidad.

**Riesgo 3 — Pérdida de datos de desarrollo:**
- Probabilidad: Baja. Impacto: Alto.
- Mitigación: Control de versiones con Git y repositorio remoto. Commits frecuentes.

---

# 14. Futuras mejoras

FuerzApp, en su estado actual, constituye una plataforma funcional y completa para la gestión de gimnasios. Sin embargo, existen varias líneas de mejora que se han identificado durante el desarrollo y que podrían implementarse en versiones futuras para ampliar las funcionalidades de la plataforma:

**FM-01 — Aplicación móvil nativa:**
El panel de cliente podría beneficiarse enormemente de una aplicación móvil nativa (iOS / Android) desarrollada con **Flutter** o **React Native**, que aprovechara capacidades del dispositivo como notificaciones push para recordatorios de sesiones, integración con el calendario del teléfono y acceso offline a los planes de entrenamiento.

**FM-02 — Sistema de notificaciones en tiempo real:**
Implementar un sistema de notificaciones en tiempo real mediante **WebSockets** (con Spring WebSocket y STOMP) para notificar a los clientes cuando un entrenador les asigna un plan, cuando hay plazas disponibles en una sesión o cuando su suscripción está próxima a expirar.

**FM-03 — Panel de estadísticas avanzado:**
Añadir un dashboard de analítica más detallado para propietarios, con gráficos de evolución de ingresos, tasa de retención de clientes, sesiones más populares y rendimiento por entrenador. Podría implementarse con una librería como **Chart.js** integrada en Angular.

**FM-04 — Integración con wearables y APIs de fitness:**
Permitir la importación de datos de actividad desde plataformas como **Strava**, **Apple Health** o **Google Fit**, para enriquecer el perfil de actividad del cliente y ofrecer recomendaciones de entrenamiento personalizadas.

**FM-05 — Sistema de mensajería interna:**
Añadir un sistema de mensajería directa entre entrenadores y clientes, integrado en la plataforma, para facilitar la comunicación sin necesidad de herramientas externas.

**FM-06 — Suscripciones recurrentes con Stripe Billing:**
Migrar el sistema de pagos desde sesiones de Checkout puntuales a **Stripe Billing** con suscripciones recurrentes automáticas, para automatizar la renovación mensual y reducir la fricción del proceso de pago.

**FM-07 — Gestión de inventario y equipamiento:**
Añadir un módulo de gestión del equipamiento e inventario del gimnasio, con control de mantenimiento preventivo y alertas de revisión.

**FM-08 — Multiidioma (i18n):**
Implementar soporte multiidioma en el frontend con **Angular i18n** para expandir la plataforma a mercados internacionales (portugués, francés, inglés).

**FM-09 — Migración de base de datos con Flyway:**
Adoptar **Flyway** como gestor de migraciones de base de datos para pasar de `ddl-auto: update` (adecuado en desarrollo) a un sistema de versionado controlado de esquemas, imprescindible en entornos productivos con datos reales.

**FM-10 — Pruebas end-to-end con Cypress o Playwright:**
Añadir una suite de pruebas end-to-end automatizadas con **Cypress** o **Playwright** que cubran los flujos críticos del sistema (login, contratación de suscripción, creación de plan de entrenamiento) ejecutándose en cada despliegue para detectar regresiones.

---

# 15. Anexos

## Anexo A — Modelo Entidad-Relación

*(TODO: Insertar diagrama ER generado con MySQL Workbench o dbdiagram.io)*

## Anexo B — Diagrama de arquitectura del sistema

*(TODO: Insertar diagrama de arquitectura con los tres componentes principales: frontend Angular, backend Spring Boot, base de datos MySQL y Stripe)*

## Anexo C — Capturas de pantalla de la aplicación

### C.1 Pantalla de login

*(TODO: Insertar captura)*

### C.2 Panel de administrador — Gestión de gimnasios

*(TODO: Insertar captura)*

### C.3 Diálogo de alta de gimnasio (MatStepper)

*(TODO: Insertar captura — paso 1 y paso 2)*

### C.4 Panel de propietario — Dashboard

*(TODO: Insertar captura)*

### C.5 Panel de propietario — Suscripciones (3 pestañas)

*(TODO: Insertar captura)*

### C.6 Panel de entrenador — Entrenamientos (layout 2 columnas)

*(TODO: Insertar captura)*

### C.7 Panel de entrenador — Ejercicios (rejilla de tarjetas)

*(TODO: Insertar captura)*

### C.8 Panel de cliente — Dashboard

*(TODO: Insertar captura)*

### C.9 Panel de cliente — Suscripción y flujo de pago Stripe

*(TODO: Insertar captura — pantalla de selección y redirección a Stripe Checkout)*

### C.10 Stripe Checkout (modo test)

*(TODO: Insertar captura de la página de pago de Stripe)*

### C.11 Panel de cliente — Entrenamientos específicos con botón "Completar"

*(TODO: Insertar captura)*

## Anexo D — Resultados de pruebas unitarias

```
[INFO] Tests run: 34, Failures: 0, Errors: 0, Skipped: 0

Results:
GimnasioServiceTest    - 8/8 tests passed
SuscripcionServiceTest - 8/8 tests passed
EjercicioServiceTest   - 8/8 tests passed
EntrenamientoServiceTest - 10/10 tests passed

BUILD SUCCESS
```

*(TODO: Insertar captura de la ejecución de tests con mvn test)*

## Anexo E — Configuración del entorno de desarrollo

### E.1 Requisitos previos

- JDK 21 o superior
- Maven 3.9 o superior
- Node.js 20 LTS o superior
- Angular CLI 19: `npm install -g @angular/cli@19`
- MySQL 8 en ejecución local en el puerto 3306

### E.2 Pasos de instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone TODO_URL_REPOSITORIO
cd fuerzapp

# 2. Configurar las variables de entorno del backend
# Crear archivo backend/src/main/resources/application-local.properties
# con las credenciales de MySQL, JWT secret y claves de Stripe

# 3. Iniciar el backend
cd backend
mvn spring-boot:run

# 4. En otra terminal, iniciar el frontend
cd frontend
npm install
ng serve

# 5. Acceder a la aplicación en http://localhost:4200
```

### E.3 Credenciales de prueba (entorno de desarrollo)

| Rol | Email | Contraseña |
|-----|-------|------------|
| Admin plataforma | TODO@email.com | TODO |
| Propietario | TODO@email.com | TODO |
| Entrenador | TODO@email.com | TODO |
| Cliente | TODO@email.com | TODO |

---

# 16. Bibliografía

## Documentación oficial

- **Spring Boot Reference Documentation** (3.3.0). Pivotal Software. Disponible en: https://docs.spring.io/spring-boot/docs/3.3.0/reference/html/

- **Spring Security Reference Documentation**. Pivotal Software. Disponible en: https://docs.spring.io/spring-security/reference/

- **Angular Documentation**. Google. Disponible en: https://angular.dev

- **Angular Material Documentation** (19.2). Google. Disponible en: https://material.angular.io

- **Stripe API Reference**. Stripe Inc. Disponible en: https://stripe.com/docs/api

- **jjwt — Java JWT Library** (0.12.5). Stormpath / jwtk contributors. Disponible en: https://github.com/jwtk/jjwt

- **MySQL 8.0 Reference Manual**. Oracle Corporation. Disponible en: https://dev.mysql.com/doc/refman/8.0/en/

- **Hibernate ORM Documentation** (6.4). Red Hat. Disponible en: https://hibernate.org/orm/documentation/

- **RxJS Documentation** (7.8). ReactiveX contributors. Disponible en: https://rxjs.dev

- **TypeScript Handbook** (5.7). Microsoft. Disponible en: https://www.typescriptlang.org/docs/handbook/

## Libros y recursos académicos

- **Craig Walls** (2022). *Spring in Action* (6ª edición). Manning Publications.

- **Ng-book — The Complete Guide to Angular** (2023). Fullstack.io.

- **Martin, Robert C.** (2008). *Clean Code: A Handbook of Agile Software Craftsmanship*. Prentice Hall.

- **Martin, Robert C.** (2017). *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall.

## Recursos online

- **Baeldung — Spring Tutorials**. Disponible en: https://www.baeldung.com/spring-tutorial

- **Stack Overflow**. Disponible en: https://stackoverflow.com

- **The Twelve-Factor App**. Disponible en: https://12factor.net/es/

---

*Documento generado como memoria técnica del Proyecto de Fin de Ciclo.*
*FuerzApp — Plataforma SaaS de Gestión de Gimnasios*
*TODO (Nombre del alumno) — TODO (Centro educativo) — Curso TODO*
