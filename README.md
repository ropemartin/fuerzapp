# FuerzApp

Plataforma SaaS de gestión de gimnasios. Permite a propietarios gestionar sus centros, a entrenadores crear planes de entrenamiento y sesiones, y a clientes consultar su actividad y pagar su suscripción online.

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21, Spring Boot 3.3, Spring Security, JWT |
| Base de datos | MySQL 8, Spring Data JPA / Hibernate |
| Pagos | Stripe Checkout Sessions + Webhooks |
| Frontend | Angular 19 (Standalone), Angular Material 19 |
| Lenguaje frontend | TypeScript 5.7, SCSS, RxJS 7.8 |
| Build | Maven (backend), Angular CLI (frontend) |

## Roles

| Rol | Descripción |
|-----|-------------|
| `ADMIN_PLATAFORMA` | Gestión global de gimnasios |
| `PROPIETARIO` | Gestión de su gimnasio: clientes, entrenadores, suscripciones |
| `ENTRENADOR` | Entrenamientos, ejercicios y sesiones |
| `CLIENTE` | Consulta su actividad, suscripción y pagos |

## Estructura del repositorio

```
proyecto/
├── backend/        Spring Boot REST API (puerto 8080/8081)
└── frontend/       Angular SPA (puerto 4200)
```

## Requisitos previos

- Java 21+
- Maven 3.9+
- Node.js 20+ y npm
- MySQL 8 corriendo localmente

## Instalación y arranque local

### Base de datos

```sql
CREATE DATABASE fuerzapp;
```

### Backend

```bash
cd backend
mvn spring-boot:run
```

La API arranca en `http://localhost:8081/api`. En el primer arranque Hibernate crea el esquema y el `DataSeeder` carga datos de ejemplo automáticamente.

### Frontend

```bash
cd frontend
npm install
ng serve
```

La aplicación queda disponible en `http://localhost:4200`.

## Variables de entorno (backend)

| Variable | Descripción | Por defecto |
|----------|-------------|-------------|
| `SPRING_DATASOURCE_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/fuerzapp` |
| `SPRING_DATASOURCE_USERNAME` | Usuario MySQL | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña MySQL | `root` |
| `JWT_SECRET` | Clave de firma JWT (mín. 64 chars) | valor de desarrollo incluido |
| `STRIPE_API_KEY` | Clave secreta de Stripe | vacío |
| `STRIPE_WEBHOOK_SECRET` | Signing secret del webhook de Stripe | vacío |
| `FRONTEND_URL` | URL del frontend (para CORS) | `http://localhost:4200` |
| `PORT` | Puerto del servidor | `8081` |

## Credenciales de prueba

Todos los usuarios tienen contraseña **`123456`**.

| Email | Rol | Gimnasio |
|-------|-----|----------|
| `admin@fuerzapp.com` | Admin plataforma | — |
| `carlos@fitgym.com` | Propietario | FitGym Madrid |
| `laura@powerzone.com` | Propietario | PowerZone Barcelona |
| `miguel@ironbody.com` | Propietario | IronBody Valencia |
| `ana@fitgym.com` | Entrenador | FitGym Madrid |
| `sofia@powerzone.com` | Entrenador | PowerZone Barcelona |
| `lucia@ironbody.com` | Entrenador | IronBody Valencia |
| `juan@email.com` | Cliente | FitGym Madrid |
| `sara@email.com` | Cliente | PowerZone Barcelona |
| `javier@email.com` | Cliente | IronBody Valencia |

## Entornos de producción

| Servicio | URL |
|---------|-----|
| Frontend | https://fuerzapp.vercel.app |
| Backend API | https://fuerzapp-backend-production.up.railway.app/api |

## Flujo de pago (Stripe test)

Usa la tarjeta de prueba `4242 4242 4242 4242` con cualquier fecha futura y CVC.

## Licencia

Proyecto propietario — todos los derechos reservados. Véase la sección de Licenciamiento en el informe del proyecto.
