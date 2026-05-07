# 5. Tarea 1 — Análisis y diseño del sistema

La primera tarea del proyecto abarca todas las actividades de análisis previas al desarrollo: la captura y especificación de requisitos, el diseño del modelo de datos relacional, la definición de la arquitectura del sistema, el diseño de los endpoints de la API REST y la elaboración de los wireframes de la interfaz de usuario. Esta fase es fundamental porque las decisiones tomadas aquí condicionan el resto del desarrollo y son muy costosas de revertir una vez iniciada la implementación.

---

## 5.1 Análisis de requisitos

### 5.1.1 Técnica de captura de requisitos

Para la captura de requisitos se utilizó la técnica de análisis de dominio, complementada con la revisión de plataformas SaaS similares del mercado (Mindbody, Glofox, Virtuagym). Se identificaron los actores del sistema y sus necesidades, se elaboraron historias de usuario y se priorizaron los requisitos funcionales mediante la técnica MoSCoW (Must have, Should have, Could have, Won't have).

### 5.1.2 Actores del sistema

El sistema contempla cuatro actores principales, cada uno con un conjunto de responsabilidades y permisos diferenciados:

**ADMIN_PLATAFORMA:** Es el administrador global del SaaS. Tiene visibilidad completa sobre todos los gimnasios registrados en la plataforma. Sus responsabilidades son: dar de alta nuevos gimnasios (asignando un propietario en el mismo proceso), visualizar estadísticas globales de uso y activar o desactivar gimnasios.

**PROPIETARIO:** Es el dueño o gestor de uno o varios gimnasios. Gestiona sus propios centros: configura los tipos de suscripción y extras disponibles, añade y elimina entrenadores y clientes, y visualiza el estado de las suscripciones activas de sus clientes. Un propietario puede tener varios gimnasios bajo su gestión.

**ENTRENADOR:** Es el profesional que imparte clases y sesiones en el gimnasio. Puede crear ejercicios propios (además de los predefinidos por la plataforma), crear planes de entrenamiento de tipo grupal, individual o específico, programar sesiones y asignar planes a clientes concretos. Un entrenador puede estar vinculado a varios gimnasios.

**CLIENTE:** Es el usuario final del gimnasio. Consulta sus sesiones programadas, visualiza y completa sus planes de entrenamiento, gestiona su suscripción (contratación, ampliación con extras) y realiza pagos a través de la pasarela Stripe integrada. Un cliente puede estar dado de alta en varios gimnasios.

### 5.1.3 Requisitos funcionales

A continuación se recogen los requisitos funcionales principales del sistema, organizados por módulo:

**RF-AUTH — Autenticación:**
- RF-AUTH-01: El sistema debe permitir el registro de nuevos usuarios con nombre, apellidos, email y contraseña.
- RF-AUTH-02: El sistema debe autenticar a los usuarios mediante email y contraseña, devolviendo un token JWT válido.
- RF-AUTH-03: El token JWT debe incluir el identificador de usuario, el email y el rol.
- RF-AUTH-04: Las rutas protegidas deben requerir un token JWT válido en la cabecera Authorization.
- RF-AUTH-05: El sistema debe invalidar sesiones ante tokens expirados o inválidos (respuesta HTTP 401).

**RF-GIM — Gestión de gimnasios:**
- RF-GIM-01: El administrador puede crear gimnasios, incluyendo la creación simultánea del usuario propietario.
- RF-GIM-02: El administrador puede activar y desactivar gimnasios.
- RF-GIM-03: El propietario puede visualizar todos sus gimnasios y sus estadísticas.
- RF-GIM-04: El propietario puede añadir y eliminar entrenadores de sus gimnasios.
- RF-GIM-05: El propietario puede añadir y eliminar clientes de sus gimnasios.
- RF-GIM-06: Los usuarios pueden estar vinculados a múltiples gimnasios.

**RF-SUS — Suscripciones:**
- RF-SUS-01: El propietario puede crear tipos de suscripción con nombre, descripción, precio mensual y duración.
- RF-SUS-02: El propietario puede crear extras opcionales vinculados a los tipos de suscripción.
- RF-SUS-03: El cliente puede contratar una suscripción, lo que cancela automáticamente la suscripción activa anterior.
- RF-SUS-04: El cliente puede añadir extras a su suscripción activa.
- RF-SUS-05: El sistema debe calcular automáticamente la fecha de fin de la suscripción en función de su duración.

**RF-PAG — Pagos y facturación:**
- RF-PAG-01: El sistema debe integrar Stripe Checkout Sessions para el proceso de pago.
- RF-PAG-02: El sistema debe escuchar eventos de Stripe mediante webhooks.
- RF-PAG-03: Cada pago completado debe generar automáticamente una factura con formato FAC-YYYY-NNNNN.
- RF-PAG-04: El cliente debe poder descargar sus facturas en formato PDF.
- RF-PAG-05: El historial de pagos debe ser visible para el cliente.

**RF-ENT — Entrenamientos:**
- RF-ENT-01: La plataforma debe incluir un catálogo de 28 ejercicios predefinidos, clasificados por grupo muscular.
- RF-ENT-02: Los entrenadores pueden crear ejercicios propios para su gimnasio.
- RF-ENT-03: Los ejercicios predefinidos no pueden ser eliminados.
- RF-ENT-04: El entrenador puede crear planes de entrenamiento de tipo GRUPAL, INDIVIDUAL o ESPECIFICO.
- RF-ENT-05: Los planes de tipo ESPECIFICO se asignan a un cliente concreto y no permiten la creación de sesiones de grupo.
- RF-ENT-06: Las sesiones tienen un límite de capacidad configurable.
- RF-ENT-07: El cliente puede marcar como completados los planes de entrenamiento específicos asignados.

### 5.1.4 Requisitos no funcionales

**RNF-01 — Seguridad:** Toda la comunicación entre frontend y backend debe realizarse sobre HTTPS en producción. Las contraseñas deben almacenarse cifradas mediante BCrypt. El control de acceso debe verificarse en el backend para cada petición, independientemente de las restricciones del frontend.

**RNF-02 — Escalabilidad:** La arquitectura debe permitir escalar el backend horizontalmente sin estado compartido en memoria (JWT stateless, sin sesiones HTTP).

**RNF-03 — Usabilidad:** La interfaz debe ser intuitiva, con tiempos de respuesta percibidos inferiores a 2 segundos para operaciones habituales. Debe seguir los principios de Material Design.

**RNF-04 — Mantenibilidad:** El código debe seguir los principios SOLID, especialmente la inversión de dependencias (DIP) mediante interfaces de servicio. Debe haber cobertura de tests unitarios para la lógica de negocio crítica.

**RNF-05 — Disponibilidad:** El sistema debe estar disponible en un entorno de producción accesible públicamente, con un uptime objetivo del 99% en horario lectivo.

---

## 5.2 Diseño de la base de datos

### 5.2.1 Decisiones de diseño

El motor de base de datos elegido es **MySQL 8**, por su amplia adopción en el sector, su compatibilidad con Spring Data JPA/Hibernate y su disponibilidad en los principales proveedores de hosting. Se optó por un modelo relacional clásico en lugar de una base de datos documental (como MongoDB) porque los datos del dominio son altamente estructurados y relacionales, y porque las consultas de negocio se benefician del lenguaje SQL y de las restricciones de integridad referencial.

El esquema se genera automáticamente mediante la función `ddl-auto: update` de Hibernate en desarrollo, lo que permite iterar rápidamente sobre el modelo de entidades sin necesidad de escribir migraciones manuales. En producción se recomienda el uso de Flyway o Liquibase para gestionar las migraciones de forma controlada.

### 5.2.2 Entidades del dominio

El modelo de datos consta de **16 entidades** que cubren todos los aspectos del dominio:

**Usuario:** Entidad central que representa a cualquier usuario del sistema, independientemente de su rol. Almacena nombre, apellidos, email (único), contraseña cifrada y rol. La distinción de roles se hace mediante el campo enumerado `rol` (ADMIN_PLATAFORMA, PROPIETARIO, ENTRENADOR, CLIENTE), evitando la necesidad de tablas de herencia.

**Gimnasio:** Representa un centro deportivo dado de alta en la plataforma. Contiene nombre, dirección, teléfono, email de contacto, estado (activo/inactivo) y la referencia al usuario propietario.

**EntrenadorGimnasio:** Tabla de unión entre entrenadores y gimnasios. Implementa la relación muchos-a-muchos entre `Usuario` (con rol ENTRENADOR) y `Gimnasio`. Permite que un entrenador esté vinculado a múltiples gimnasios.

**ClienteGimnasio:** Análoga a `EntrenadorGimnasio`, pero para clientes. Permite que un cliente esté dado de alta en múltiples gimnasios.

**TipoSuscripcion:** Define los planes de suscripción disponibles en un gimnasio. Contiene nombre, descripción, precio mensual y duración en días. Cada gimnasio puede tener múltiples tipos de suscripción.

**Extra:** Representa un servicio adicional que puede añadirse a una suscripción (por ejemplo, acceso a clases de spinning, servicio de nutricionista). Contiene nombre, descripción y precio adicional.

**ClienteSuscripcion:** Registra la suscripción activa de un cliente en un gimnasio. Incluye fechas de inicio y fin, estado (ACTIVA, CANCELADA, EXPIRADA) y referencias al cliente, al tipo de suscripción y al gimnasio.

**ClienteSuscripcionExtra:** Tabla de unión que registra qué extras ha contratado un cliente en su suscripción activa.

**Pago:** Registra cada transacción económica realizada. Almacena el importe, la fecha, el estado del pago (PENDIENTE, COMPLETADO, FALLIDO), la referencia de sesión de Stripe y la referencia al cliente y al gimnasio.

**Factura:** Generada automáticamente por cada pago completado. Incluye número de factura con formato normalizado (FAC-YYYY-NNNNN), fecha de emisión, importe, datos del emisor y receptor, y una URL de descarga en PDF.

**Ejercicio:** Representa un ejercicio físico. Puede ser predefinido por la plataforma (`esPredefinido = true`) o creado por un entrenador para un gimnasio concreto. Incluye nombre, descripción, grupo muscular (enum) y referencia opcional al gimnasio.

**Entrenamiento:** Plan de entrenamiento creado por un entrenador. Incluye nombre, descripción, tipo (GRUPAL, INDIVIDUAL, ESPECIFICO) y referencias al entrenador creador y al gimnasio.

**EntrenamientoEjercicio:** Tabla de unión entre `Entrenamiento` y `Ejercicio`, con campos adicionales como series, repeticiones, peso y orden del ejercicio en el plan.

**Sesion:** Representa una sesión concreta de un plan de entrenamiento. Contiene fecha, hora, duración, capacidad máxima y estado. Solo aplicable a entrenamientos de tipo GRUPAL o INDIVIDUAL.

**SesionCliente:** Registra la inscripción de un cliente en una sesión. Tabla de unión entre `Sesion` y `Usuario` (cliente).

**EntrenamientoEspecifico:** Representa la asignación de un plan de tipo ESPECIFICO a un cliente concreto. Incluye fecha de asignación, fecha de completado y estado (PENDIENTE, COMPLETADO).

### 5.2.3 Relaciones principales

Las relaciones más relevantes del modelo son:

- `Gimnasio` 1:N `TipoSuscripcion` — Un gimnasio tiene múltiples tipos de suscripción.
- `Gimnasio` N:M `Usuario(ENTRENADOR)` a través de `EntrenadorGimnasio`.
- `Gimnasio` N:M `Usuario(CLIENTE)` a través de `ClienteGimnasio`.
- `TipoSuscripcion` 1:N `Extra` — Un tipo de suscripción puede tener múltiples extras.
- `Usuario(CLIENTE)` 1:N `ClienteSuscripcion` — Un cliente puede tener historial de suscripciones, pero solo una activa por gimnasio.
- `Pago` 1:1 `Factura` — Cada pago completado genera exactamente una factura.
- `Entrenamiento` N:M `Ejercicio` a través de `EntrenamientoEjercicio`.
- `Entrenamiento` 1:N `Sesion` — Un plan puede tener múltiples sesiones programadas.
- `Sesion` N:M `Usuario(CLIENTE)` a través de `SesionCliente`.
- `Entrenamiento(ESPECIFICO)` 1:N `EntrenamientoEspecifico` — Un plan específico puede ser asignado a múltiples clientes.

---

## 5.3 Diseño de la arquitectura del sistema

### 5.3.1 Arquitectura general

FuerzApp sigue una **arquitectura de tres capas desacopladas**:

```
[Cliente (navegador)]
        ↕ HTTPS / JSON
[Frontend — Angular 19 SPA]  ← Puerto 4200 (dev) / Hosting estático (prod)
        ↕ HTTP REST / JSON + JWT Bearer
[Backend — Spring Boot REST API]  ← Puerto 8080 (dev) / Servidor cloud (prod)
        ↕ JDBC / JPA
[Base de datos — MySQL 8]  ← Puerto 3306
        ↕ HTTPS
[Stripe API]  ← Pagos externos
```

El frontend es una SPA (*Single Page Application*) que se sirve como archivos estáticos (HTML, CSS, JavaScript compilado). Una vez descargada en el navegador, todas las interacciones con el servidor se realizan mediante llamadas AJAX a la API REST del backend. No hay renderizado en el servidor (*SSR*); el enrutamiento es completamente del lado del cliente (*client-side routing*) gestionado por Angular Router.

El backend expone una API REST pura: no gestiona sesiones HTTP, no sirve vistas y no mantiene estado entre peticiones. Toda la autenticación y autorización se basa en el token JWT que el cliente incluye en cada petición.

### 5.3.2 Arquitectura del backend

El backend sigue la arquitectura en capas clásica de Spring Boot:

**Capa de presentación (Controllers):** 7 controladores REST que reciben las peticiones HTTP, validan los datos de entrada mediante DTOs y anotaciones de validación de Bean Validation, y delegan el procesamiento en la capa de servicio. Los controladores están anotados con `@RestController` y `@RequestMapping`.

**Capa de servicio (Services):** 6 interfaces de servicio con sus correspondientes implementaciones (`XxxServiceImpl`). Esta separación sigue el principio de inversión de dependencias (DIP). Los controladores dependen de las interfaces, nunca de las implementaciones concretas. Spring autowiring se encarga de inyectar la implementación correcta en tiempo de ejecución mediante `@Service` y `@Autowired`.

**Capa de persistencia (Repositories):** 14 repositorios JPA que extienden `JpaRepository`. Proporcionan operaciones CRUD estándar y permiten definir consultas derivadas (*derived queries*) o consultas JPQL personalizadas mediante `@Query`.

**Capa de seguridad (Security):** Implementada con Spring Security 6. Incluye el filtro `JwtAuthenticationFilter` que intercepta todas las peticiones, extrae y valida el token JWT y establece el contexto de seguridad. La configuración de seguridad define qué endpoints son públicos (login, registro, webhook de Stripe) y cuáles requieren autenticación y qué roles.

### 5.3.3 Arquitectura del frontend

El frontend sigue la arquitectura recomendada por Angular con **componentes standalone** (sin NgModules), organizada en cuatro capas:

**Core:** Servicios singleton (AuthService, GimnasioContextService), guards de ruta (authGuard, rolGuard), interceptores HTTP (AuthInterceptor) y modelos de dominio (interfaces TypeScript).

**Features:** Módulos funcionales agrupados por rol: `admin/`, `propietario/`, `entrenador/`, `cliente/` y `auth/`. Cada uno contiene sus componentes standalone con sus rutas lazy-loaded.

**Shared:** Componentes reutilizables (layout con sidebar, header), pipes y directivas compartidas entre múltiples features.

**Assets y estilos:** Tema Azure Blue de Angular Material configurado en `angular.json`, estilos globales en SCSS.

La comunicación con el backend se realiza exclusivamente a través de servicios Angular que encapsulan las llamadas HTTP mediante `HttpClient`. El `AuthInterceptor` añade automáticamente el token JWT a todas las peticiones salientes y gestiona el logout automático ante respuestas 401.

---

## 5.4 Diseño de la API REST

### 5.4.1 Convenciones generales

La API sigue las convenciones REST estándar:

- Recursos en plural y en minúscula en los paths: `/api/gimnasios`, `/api/ejercicios`.
- Verbos HTTP semánticos: GET (lectura), POST (creación), PUT/PATCH (actualización), DELETE (eliminación).
- Respuestas con códigos HTTP estándar: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict.
- Cuerpos de petición y respuesta en formato JSON.
- Autenticación mediante cabecera `Authorization: Bearer <token>`.

### 5.4.2 Endpoints principales por controlador

**AuthController (`/api/auth`):**
- `POST /api/auth/login` — Autenticación. Devuelve token JWT + datos del usuario (id, nombre, email, rol). Público.
- `POST /api/auth/register` — Registro de nuevo usuario. Público.

**AdminController (`/api/admin`):**
- `GET /api/admin/gimnasios` — Lista todos los gimnasios con estadísticas. Requiere ADMIN_PLATAFORMA.
- `POST /api/admin/gimnasios` — Crea un gimnasio con su propietario. Requiere ADMIN_PLATAFORMA.
- `PUT /api/admin/gimnasios/{id}/estado` — Activa o desactiva un gimnasio. Requiere ADMIN_PLATAFORMA.

**GimnasioController (`/api/gimnasios`):**
- `GET /api/gimnasios/mis-gimnasios` — Devuelve los gimnasios del usuario autenticado (funciona para los tres roles: PROPIETARIO, ENTRENADOR, CLIENTE).
- `GET /api/gimnasios/{id}` — Detalle de un gimnasio.
- `GET /api/gimnasios/{id}/entrenadores` — Lista entrenadores del gimnasio. Requiere PROPIETARIO.
- `POST /api/gimnasios/{id}/entrenadores` — Añade entrenador al gimnasio. Requiere PROPIETARIO.
- `DELETE /api/gimnasios/{id}/entrenadores/{entrenadorId}` — Elimina entrenador. Requiere PROPIETARIO.
- `GET /api/gimnasios/{id}/clientes` — Lista clientes del gimnasio. Requiere PROPIETARIO o ENTRENADOR.
- `POST /api/gimnasios/{id}/clientes` — Añade cliente al gimnasio. Requiere PROPIETARIO.
- `DELETE /api/gimnasios/{id}/clientes/{clienteId}` — Elimina cliente. Requiere PROPIETARIO.

**SuscripcionController (`/api/suscripciones`):**
- `GET /api/suscripciones/gimnasio/{gimnasioId}` — Lista tipos de suscripción del gimnasio.
- `POST /api/suscripciones/gimnasio/{gimnasioId}` — Crea tipo de suscripción. Requiere PROPIETARIO.
- `POST /api/suscripciones/gimnasio/{gimnasioId}/extras` — Crea extra. Requiere PROPIETARIO.
- `GET /api/suscripciones/cliente/{clienteId}` — Suscripción activa del cliente.
- `POST /api/suscripciones/cliente/{clienteId}/contratar` — Contrata suscripción (cancela la anterior activa automáticamente).
- `POST /api/suscripciones/cliente/{clienteId}/extras` — Añade extra a la suscripción activa.

**PagoController (`/api/pagos`):**
- `POST /api/pagos/checkout` — Crea sesión de Stripe Checkout. Requiere CLIENTE.
- `POST /api/pagos/webhook` — Endpoint de webhook de Stripe. Público (verificado por firma Stripe).
- `GET /api/pagos/cliente/{clienteId}/historial` — Historial de pagos del cliente.
- `GET /api/pagos/facturas/{facturaId}/pdf` — Descarga factura en PDF.

**EjercicioController (`/api/ejercicios`):**
- `GET /api/ejercicios` — Lista todos los ejercicios (predefinidos + del gimnasio del usuario).
- `GET /api/ejercicios/grupo-muscular/{grupo}` — Filtra por grupo muscular.
- `POST /api/ejercicios` — Crea ejercicio personalizado. Requiere ENTRENADOR.
- `DELETE /api/ejercicios/{id}` — Elimina ejercicio (no permitido para predefinidos).

**EntrenamientoController (`/api/entrenamientos`):**
- `GET /api/entrenamientos/gimnasio/{gimnasioId}` — Lista planes del gimnasio.
- `POST /api/entrenamientos` — Crea plan de entrenamiento. Requiere ENTRENADOR.
- `POST /api/entrenamientos/{id}/ejercicios` — Añade ejercicio al plan.
- `POST /api/entrenamientos/{id}/sesiones` — Crea sesión (bloqueado para tipo ESPECIFICO).
- `POST /api/entrenamientos/{id}/sesiones/{sesionId}/inscribir` — Inscribe cliente en sesión.
- `POST /api/entrenamientos/especificos` — Asigna plan ESPECIFICO a cliente.
- `PUT /api/entrenamientos/especificos/{id}/completar` — Marca plan específico como completado. Requiere CLIENTE.

---

## 5.5 Diseño de la interfaz de usuario (wireframes)

### 5.5.1 Metodología de diseño

Para el diseño de la interfaz se adoptó un enfoque *mobile-first* con un punto de quiebre principal en 960px. Sin embargo, dado que la plataforma está orientada principalmente a uso en escritorio (gestión de gimnasio), la experiencia desktop es la prioritaria. Se utilizó **Angular Material 19.2** con el tema **Azure Blue** como sistema de diseño, aprovechando sus componentes preconstruidos (MatTable, MatDialog, MatTabs, MatStepper, MatCard, etc.) para garantizar consistencia visual y accesibilidad.

### 5.5.2 Estructura de navegación

La aplicación sigue un modelo de navegación basado en un *sidebar* lateral fijo con las secciones del panel activo, una barra superior (*toolbar*) con el nombre del usuario y el gimnasio activo (cuando aplica), y el área de contenido principal que ocupa el resto de la pantalla.

Todas las rutas de la aplicación están protegidas por `authGuard` (que verifica que el usuario está autenticado) y por `rolGuard` (que verifica que el rol del usuario tiene acceso a la sección). Las rutas no protegidas son únicamente la página de login (`/login`).

### 5.5.3 Pantallas principales

**Login (`/login`):** Formulario centrado con logo de la plataforma, campos de email y contraseña, y botón de acceso. Sin registro público (los usuarios son dados de alta por administradores o propietarios).

**Panel Admin — Gimnasios (`/admin/gimnasios`):** Tabla Material con columnas: nombre, propietario, ciudad, estado, nº entrenadores, nº clientes, acciones. Botón flotante para crear gimnasio. Al crear, se abre un `MatDialog` con un `MatStepper` de dos pasos: datos del gimnasio y datos del propietario.

**Panel Propietario — Dashboard (`/propietario/dashboard`):** Selector de gimnasio (si el propietario tiene varios), tarjetas de estadísticas (nº entrenadores, nº clientes, nº suscripciones activas) y accesos rápidos a las secciones principales.

**Panel Propietario — Suscripciones (`/propietario/suscripciones`):** Tres pestañas (MatTabs): tipos de suscripción, extras y listado de suscriptores activos.

**Panel Entrenador — Entrenamientos (`/entrenador/entrenamientos`):** Layout de dos columnas: columna izquierda con lista de planes y formulario de creación; columna derecha con el detalle del plan seleccionado, con pestañas para ejercicios, sesiones y asignación a clientes.

**Panel Entrenador — Ejercicios (`/entrenador/ejercicios`):** Rejilla de tarjetas con filtro por grupo muscular. Cada tarjeta muestra nombre, descripción y grupo muscular. Los ejercicios predefinidos tienen una etiqueta visual diferenciadora.

**Panel Cliente — Suscripción (`/cliente/suscripcion`):** Tarjeta con la suscripción activa, listado de extras contratados, botón de contratar/renovar (redirige a Stripe Checkout) y historial de pagos con botón de descarga de factura por cada pago.

**Panel Cliente — Entrenamientos (`/cliente/entrenamientos`):** Tres pestañas: sesiones individuales, sesiones grupales y planes específicos asignados al cliente, con botón de marcar como completado.

---

## Tabla de duración — Tarea 1

| Subtarea | Descripción | Duración estimada |
|----------|-------------|-------------------|
| 5.1 | Análisis de requisitos | 12 horas |
| 5.2 | Diseño de la base de datos | 10 horas |
| 5.3 | Diseño de la arquitectura | 8 horas |
| 5.4 | Diseño de la API REST | 10 horas |
| 5.5 | Diseño de la interfaz (wireframes) | 8 horas |
| **Total Tarea 1** | | **48 horas** |

---
