# FuerzApp — Gym SaaS Platform

## Project Overview

**FuerzApp** is a multi-tenant SaaS platform for gym management. Built as a monorepo with a Spring Boot REST API backend and an Angular 19 SPA frontend. This is a DAW final-year project with a real commercial goal (selling the platform to gyms).

**4 roles:** `ADMIN_PLATAFORMA`, `PROPIETARIO`, `ENTRENADOR`, `CLIENTE`

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.3.0, Spring Security, JWT (jjwt 0.12.5) |
| ORM | Spring Data JPA / Hibernate, MySQL 8 |
| Payments | Stripe API v25.3.0 (test mode, Checkout Sessions + webhooks) |
| Build | Maven |
| Frontend | Angular 19.2 (Standalone Components), Angular Material 19.2 (Azure Blue theme) |
| Language | TypeScript 5.7, SCSS |
| Reactive | RxJS 7.8 |
| Tests | Karma + Jasmine |

---

## Project Structure

```
proyecto/
├── backend/                     Spring Boot REST API (port 8080)
│   ├── src/main/java/com/fuerzapp/
│   │   ├── config/              JWT, Security, Stripe, DB configs, DataSeeder
│   │   ├── controller/          7 REST controllers
│   │   ├── service/             6 interfaces + 6 XxxServiceImpl implementations
│   │   ├── repository/          14 JPA repositories
│   │   ├── entity/              16 domain entities
│   │   ├── dto/                 26 request/response DTOs
│   │   ├── enums/               6 enumerations
│   │   └── exception/           Global exception handler
│   └── src/test/java/com/fuerzapp/
│       └── service/             4 unit test classes (JUnit 5 + Mockito)
│
└── frontend/                    Angular SPA (port 4200)
    └── src/app/
        ├── core/                Guards, interceptors, models, services
        ├── features/
        │   ├── admin/           Platform admin panel
        │   ├── propietario/     Gym owner panel
        │   ├── entrenador/      Trainer panel
        │   ├── cliente/         Client panel
        │   └── auth/            Login page
        └── shared/              Reusable components, pipes, layout
```

---

## Dev Commands

### Backend
```bash
cd backend
mvn spring-boot:run          # Run dev server (port 8080)
mvn clean package            # Build JAR
mvn test                     # Run tests
```

### Frontend
```bash
cd frontend
npm install                  # Install dependencies
ng serve                     # Dev server (port 4200)
ng build                     # Production build → dist/
ng test                      # Run unit tests
```

---

## Domain Entities (16)

`Usuario`, `Gimnasio`, `EntrenadorGimnasio`, `ClienteGimnasio`, `TipoSuscripcion`, `Extra`, `ClienteSuscripcion`, `ClienteSuscripcionExtra`, `Pago`, `Factura`, `Ejercicio`, `Entrenamiento`, `EntrenamientoEjercicio`, `Sesion`, `SesionCliente`, `EntrenamientoEspecifico`

---

## Controllers & Endpoints

- **AuthController** — login/register
- **AdminController** — platform-level gym management
- **GimnasioController** — `/mis-gimnasios` handles 3 roles: PROPIETARIO, ENTRENADOR, CLIENTE
- **SuscripcionController** — subscription types and extras
- **PagoController** — Stripe Checkout + webhook (public endpoint)
- **EjercicioController** — exercises (global predefined + per-gym)
- **EntrenamientoController** — training plans and sessions

---

## Frontend Services & Core

- `auth.service.ts` — login/logout, localStorage, getter `currentUser`
- `gimnasio-context.service.ts` — BehaviorSubject + localStorage for active gym, `obtenerIdOFail()`
- `gimnasio.service.ts` — CRUD gyms, `misGimnasios()` → GET /api/gimnasios/mis-gimnasios
- `suscripcion.service.ts`, `ejercicio.service.ts`, `entrenamiento.service.ts`, `pago.service.ts`
- `auth.interceptor.ts` — attaches Bearer token, auto-logout on 401
- `authGuard`, `rolGuard(['ROL'])` — route protection

---

## Implemented Panels

### Admin (`/admin`)
- `gimnasios.component` — table with stats, create/disable gyms
- `alta-gimnasio-dialog.component` — MatStepper 2 steps (gym data + owner)

### Propietario (`/propietario`)
- `dashboard.component` — gym selector (if multiple), stats, quick actions
- `entrenadores.component` — table with initials avatar, add/remove
- `clientes.component` — same as entrenadores
- `suscripciones.component` — 3 tabs: types, extras, subscribers

### Entrenador (`/entrenador`)
- `dashboard.component` — gym selector, stats, upcoming sessions
- `entrenamientos.component` — 2-column layout: list+create / detail with tabs (exercises, sessions, assign)
- `ejercicios.component` — card grid with muscle group filter, differentiates predefined/own

### Cliente (`/cliente`)
- `dashboard.component` — cards: active subscription, upcoming sessions, pending plans; auto-loads gym
- `entrenamientos.component` — 3 tabs: individual sessions, group sessions, specific plans with mark-as-complete
- `suscripcion.component` — active subscription, extras, payment history, Stripe Checkout flow, invoice download

---

## Key Architecture Notes

- **Authentication:** JWT tokens attached via `authInterceptor`. Guards: `authGuard` (authenticated) and `rolGuard` (role-based).
- **Multi-tenancy:** Each gym is isolated via `GimnasioContextService` on the frontend; backend scopes data by gym.
- **Payments:** Stripe Checkout Sessions + webhook with automatic invoice generation (`FAC-YYYY-NNNNN` format).
- **Data seeding:** `DataSeeder` seeds 28 predefined exercises on startup.
- **Angular components:** All components are standalone (no NgModules).
- **Material theme:** Azure Blue (`@angular/material`), configured in `angular.json`.
- **Multi-gym users:** Trainers and clients can belong to multiple gyms via `EntrenadorGimnasio` / `ClienteGimnasio` join tables.
- **Exercise tiers:** Global predefined (`esPredefinido=true`) + per-gym custom exercises.
- **Training types:** `GRUPAL` (group sessions), `INDIVIDUAL` (1:1 sessions), `ESPECIFICO` (plan assigned to a specific client).
- **LoginResponse** includes the user `id` field so the frontend can make calls by `clienteId`.

---

## SOLID & Testing

### Service Layer Pattern (DIP — Dependency Inversion)

Each service follows the interface + implementation pattern:

| Interface | Implementation | Controllers that use it |
|-----------|---------------|------------------------|
| `AuthService` | `AuthServiceImpl` | `AuthController` |
| `GimnasioService` | `GimnasioServiceImpl` | `AdminController`, `GimnasioController` |
| `SuscripcionService` | `SuscripcionServiceImpl` | `SuscripcionController` |
| `EjercicioService` | `EjercicioServiceImpl` | `EjercicioController` |
| `EntrenamientoService` | `EntrenamientoServiceImpl` | `EntrenamientoController` |
| `PagoService` | `PagoServiceImpl` | `PagoController` |

Controllers depend on the interface, not the implementation. Spring autowires the single `@Service`-annotated impl automatically.

### Backend Unit Tests (JUnit 5 + Mockito)

All tests use `@ExtendWith(MockitoExtension.class)` — pure unit tests, no Spring context loaded. Located in `backend/src/test/java/com/fuerzapp/service/`.

| Test class | Tests | Key scenarios covered |
|------------|-------|-----------------------|
| `GimnasioServiceTest` | 8 | Email duplicate on create, gym creation flow, state change, trainer add/remove |
| `SuscripcionServiceTest` | 8 | Auto-cancel of previous active subscription, end-date calculation, duplicate extra check |
| `EjercicioServiceTest` | 8 | Block deletion of predefined exercises, muscle-group filter routing, SecurityContext mock |
| `EntrenamientoServiceTest` | 10 | ESPECIFICO blocks session creation, session capacity limit, INDIVIDUAL type validation, mark-as-complete |

`SecurityContextHolder` is mocked via Mockito's `mockStatic` (available in Mockito 5, bundled with Spring Boot 3.3).
