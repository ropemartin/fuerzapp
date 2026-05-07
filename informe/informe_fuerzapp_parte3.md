# 6. Tarea 2 — Desarrollo del backend

El desarrollo del backend constituye el núcleo técnico de FuerzApp. En esta tarea se implementó la API REST completa que da servicio a todos los paneles del frontend. El backend está construido con **Java 21** y **Spring Boot 3.3.0**, siguiendo una arquitectura en capas limpia y aplicando los principios SOLID, especialmente el principio de inversión de dependencias (DIP) mediante la separación entre interfaces de servicio e implementaciones concretas.

---

## 6.1 Configuración del entorno y estructura del proyecto

### 6.1.1 Herramientas y entorno de desarrollo

El entorno de desarrollo del backend se configura con las siguientes herramientas:

- **JDK 21 (LTS):** Se eligió Java 21 por ser la versión LTS más reciente, con soporte a largo plazo garantizado y mejoras de rendimiento significativas respecto a versiones anteriores (Virtual Threads, Record Patterns, etc.).
- **Maven 3.9+:** Sistema de construcción y gestión de dependencias. El proyecto se define en el archivo `pom.xml` con Spring Boot Parent como BOM (*Bill of Materials*) para la gestión de versiones.
- **Spring Boot 3.3.0:** Framework principal. Se inicializa con Spring Initializr con las dependencias: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`.
- **MySQL 8:** Motor de base de datos. En desarrollo se utiliza una instancia local en el puerto 3306.
- **IntelliJ IDEA / VS Code:** Entorno de desarrollo integrado.

### 6.1.2 Estructura de paquetes

La estructura de paquetes del backend sigue las convenciones de Spring Boot, organizando el código por tipo de componente:

```
com.fuerzapp/
├── config/
│   ├── JwtConfig.java              Configuración de clave secreta y expiración
│   ├── SecurityConfig.java         Cadena de filtros de Spring Security
│   ├── StripeConfig.java           Inicialización del cliente Stripe
│   ├── DataSeeder.java             Siembra de 28 ejercicios predefinidos
│   └── WebConfig.java              Configuración de CORS
├── controller/
│   ├── AuthController.java
│   ├── AdminController.java
│   ├── GimnasioController.java
│   ├── SuscripcionController.java
│   ├── PagoController.java
│   ├── EjercicioController.java
│   └── EntrenamientoController.java
├── service/
│   ├── AuthService.java            Interface
│   ├── AuthServiceImpl.java        Implementación
│   ├── GimnasioService.java
│   ├── GimnasioServiceImpl.java
│   ├── SuscripcionService.java
│   ├── SuscripcionServiceImpl.java
│   ├── EjercicioService.java
│   ├── EjercicioServiceImpl.java
│   ├── EntrenamientoService.java
│   ├── EntrenamientoServiceImpl.java
│   ├── PagoService.java
│   └── PagoServiceImpl.java
├── repository/
│   ├── UsuarioRepository.java
│   ├── GimnasioRepository.java
│   ├── EntrenadorGimnasioRepository.java
│   ├── ClienteGimnasioRepository.java
│   ├── TipoSuscripcionRepository.java
│   ├── ExtraRepository.java
│   ├── ClienteSuscripcionRepository.java
│   ├── ClienteSuscripcionExtraRepository.java
│   ├── PagoRepository.java
│   ├── FacturaRepository.java
│   ├── EjercicioRepository.java
│   ├── EntrenamientoRepository.java
│   ├── SesionRepository.java
│   └── SesionClienteRepository.java
├── entity/
│   ├── Usuario.java
│   ├── Gimnasio.java
│   ├── EntrenadorGimnasio.java
│   ├── ClienteGimnasio.java
│   ├── TipoSuscripcion.java
│   ├── Extra.java
│   ├── ClienteSuscripcion.java
│   ├── ClienteSuscripcionExtra.java
│   ├── Pago.java
│   ├── Factura.java
│   ├── Ejercicio.java
│   ├── Entrenamiento.java
│   ├── EntrenamientoEjercicio.java
│   ├── Sesion.java
│   ├── SesionCliente.java
│   └── EntrenamientoEspecifico.java
├── dto/
│   ├── (26 clases DTO de request y response)
├── enums/
│   ├── Rol.java
│   ├── EstadoGimnasio.java
│   ├── TipoEntrenamiento.java
│   ├── GrupoMuscular.java
│   ├── EstadoSuscripcion.java
│   └── EstadoPago.java
└── exception/
    └── GlobalExceptionHandler.java
```

### 6.1.3 Configuración de la base de datos

La conexión a la base de datos se configura en el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fuerzapp?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

El uso de variables de entorno (`${DB_USERNAME:root}`) permite configurar credenciales distintas en producción sin modificar el código fuente, siguiendo el principio de configuración externalizada de *The Twelve-Factor App*.

---

## 6.2 Modelado de entidades y persistencia

### 6.2.1 Entidades JPA

Las entidades del dominio se mapean como clases Java anotadas con `@Entity` y `@Table`. Cada entidad tiene una clave primaria autogenerada (`@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`) de tipo `Long`. Las relaciones se mapean con las anotaciones JPA estándar (`@ManyToOne`, `@OneToMany`, `@ManyToMany`).

A continuación se muestra el modelado de algunas entidades representativas:

**Entidad Usuario:**

```java
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // getters y setters
}
```

La anotación `@Enumerated(EnumType.STRING)` almacena el valor del enum como texto en la base de datos (en lugar del índice ordinal), lo que hace el esquema más legible y resistente a cambios en el orden de los valores del enum.

**Entidad ClienteSuscripcion:**

```java
@Entity
@Table(name = "clientes_suscripciones")
public class ClienteSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_suscripcion_id", nullable = false)
    private TipoSuscripcion tipoSuscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gimnasio_id", nullable = false)
    private Gimnasio gimnasio;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estado;

    // getters y setters
}
```

El uso de `FetchType.LAZY` en las relaciones `@ManyToOne` evita la carga innecesaria de entidades relacionadas cuando no se necesitan, previniendo el problema de N+1 consultas y mejorando el rendimiento.

### 6.2.2 Repositorios JPA

Los repositorios extienden `JpaRepository<T, ID>`, lo que proporciona automáticamente las operaciones CRUD estándar (save, findById, findAll, delete, etc.). Para consultas específicas del dominio se utilizan métodos derivados o anotaciones `@Query`:

```java
@Repository
public interface ClienteSuscripcionRepository
        extends JpaRepository<ClienteSuscripcion, Long> {

    Optional<ClienteSuscripcion> findByClienteIdAndGimnasioIdAndEstado(
        Long clienteId, Long gimnasioId, EstadoSuscripcion estado);

    List<ClienteSuscripcion> findByClienteIdAndGimnasioId(
        Long clienteId, Long gimnasioId);
}
```

### 6.2.3 DataSeeder — Siembra de datos iniciales

El componente `DataSeeder` implementa `ApplicationRunner` y se ejecuta automáticamente al arrancar la aplicación. Su función es sembrar en la base de datos los 28 ejercicios predefinidos de la plataforma, clasificados por grupo muscular, siempre y cuando no existan ya (para evitar duplicados en reinicios):

```java
@Component
public class DataSeeder implements ApplicationRunner {

    @Autowired
    private EjercicioRepository ejercicioRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (ejercicioRepository.countByEsPredefinidoTrue() == 0) {
            List<Ejercicio> ejercicios = Arrays.asList(
                crearEjercicio("Press de banca", "Ejercicio de pecho con barra",
                    GrupoMuscular.PECHO, true),
                crearEjercicio("Sentadilla", "Ejercicio fundamental de piernas",
                    GrupoMuscular.PIERNAS, true),
                // ... 26 ejercicios más
            );
            ejercicioRepository.saveAll(ejercicios);
        }
    }
}
```

Los 28 ejercicios predefinidos se distribuyen entre los grupos musculares: PECHO, ESPALDA, HOMBROS, BICEPS, TRICEPS, PIERNAS, ABDOMINALES y CARDIO.

---

## 6.3 Implementación de servicios y lógica de negocio

### 6.3.1 Patrón Interface + Implementación (DIP)

Cada servicio sigue el patrón de interfaz + implementación, cumpliendo con el principio de inversión de dependencias:

```java
// Interfaz — define el contrato
public interface GimnasioService {
    List<GimnasioAdminDto> listarTodosParaAdmin();
    GimnasioDto crearGimnasioConPropietario(CrearGimnasioRequest request);
    void cambiarEstadoGimnasio(Long gimnasioId, boolean activo);
    List<GimnasioDto> misGimnasios(Long usuarioId, Rol rol);
    void añadirEntrenador(Long gimnasioId, String emailEntrenador);
    void eliminarEntrenador(Long gimnasioId, Long entrenadorId);
    // ...
}

// Implementación — contiene la lógica real
@Service
@Transactional
public class GimnasioServiceImpl implements GimnasioService {

    @Autowired
    private GimnasioRepository gimnasioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public GimnasioDto crearGimnasioConPropietario(CrearGimnasioRequest request) {
        // Verificar que el email del propietario no está en uso
        if (usuarioRepository.existsByEmail(request.getEmailPropietario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe un usuario con ese email");
        }

        // Crear el usuario propietario
        Usuario propietario = new Usuario();
        propietario.setNombre(request.getNombrePropietario());
        propietario.setApellidos(request.getApellidosPropietario());
        propietario.setEmail(request.getEmailPropietario());
        propietario.setPassword(passwordEncoder.encode(request.getPasswordPropietario()));
        propietario.setRol(Rol.PROPIETARIO);
        propietario = usuarioRepository.save(propietario);

        // Crear el gimnasio
        Gimnasio gimnasio = new Gimnasio();
        gimnasio.setNombre(request.getNombreGimnasio());
        gimnasio.setDireccion(request.getDireccion());
        gimnasio.setPropietario(propietario);
        gimnasio.setActivo(true);
        gimnasio = gimnasioRepository.save(gimnasio);

        return mapToDto(gimnasio);
    }
}
```

Los controladores inyectan la interfaz, no la implementación:

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private GimnasioService gimnasioService;  // Interfaz, no GimnasioServiceImpl
    // ...
}
```

### 6.3.2 Lógica de negocio relevante — Suscripciones

El servicio de suscripciones implementa reglas de negocio importantes. Al contratar una nueva suscripción, el sistema cancela automáticamente cualquier suscripción activa anterior del mismo cliente en el mismo gimnasio, evitando duplicidades:

```java
@Override
public ClienteSuscripcionDto contratarSuscripcion(Long clienteId,
        Long gimnasioId, ContratarSuscripcionRequest request) {

    // Cancelar suscripción activa anterior (si existe)
    Optional<ClienteSuscripcion> suscripcionActiva =
        clienteSuscripcionRepository.findByClienteIdAndGimnasioIdAndEstado(
            clienteId, gimnasioId, EstadoSuscripcion.ACTIVA);

    suscripcionActiva.ifPresent(s -> {
        s.setEstado(EstadoSuscripcion.CANCELADA);
        clienteSuscripcionRepository.save(s);
    });

    // Calcular fecha de fin según duración del tipo de suscripción
    TipoSuscripcion tipo = tipoSuscripcionRepository
        .findById(request.getTipoSuscripcionId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    LocalDate fechaInicio = LocalDate.now();
    LocalDate fechaFin = fechaInicio.plusDays(tipo.getDuracionDias());

    // Crear nueva suscripción
    ClienteSuscripcion nueva = new ClienteSuscripcion();
    nueva.setCliente(/* ... */);
    nueva.setTipoSuscripcion(tipo);
    nueva.setGimnasio(/* ... */);
    nueva.setFechaInicio(fechaInicio);
    nueva.setFechaFin(fechaFin);
    nueva.setEstado(EstadoSuscripcion.ACTIVA);

    return mapToDto(clienteSuscripcionRepository.save(nueva));
}
```

### 6.3.3 Lógica de negocio relevante — Entrenamientos

El servicio de entrenamientos implementa la restricción de que los planes de tipo `ESPECIFICO` no pueden tener sesiones de grupo:

```java
@Override
public SesionDto crearSesion(Long entrenamientoId, CrearSesionRequest request) {
    Entrenamiento entrenamiento = entrenamientoRepository
        .findById(entrenamientoId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (entrenamiento.getTipo() == TipoEntrenamiento.ESPECIFICO) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Los entrenamientos de tipo ESPECIFICO no pueden tener sesiones");
    }

    // Verificar capacidad al inscribir cliente
    if (sesion.getInscritos().size() >= sesion.getCapacidadMaxima()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
            "La sesión ha alcanzado su capacidad máxima");
    }

    // ...
}
```

---

## 6.4 Autenticación y seguridad con JWT

### 6.4.1 Configuración de Spring Security

Spring Security se configura mediante una clase `SecurityConfig` anotada con `@Configuration` y `@EnableWebSecurity`. Se define una cadena de filtros (*Security Filter Chain*) que especifica las reglas de autorización para cada endpoint y añade el filtro JWT antes del filtro de autenticación estándar de Spring:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/pagos/webhook").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN_PLATAFORMA")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

La política `STATELESS` desactiva la creación de sesiones HTTP del lado del servidor: cada petición es autónoma y se autentica exclusivamente a través del token JWT.

### 6.4.2 Filtro JWT

El filtro `JwtAuthenticationFilter` extiende `OncePerRequestFilter` y se ejecuta una vez por cada petición HTTP:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String email = jwtUtil.extractEmail(token);

                if (email != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                    if (jwtUtil.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                    }
                }
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### 6.4.3 Generación y validación de tokens JWT

La clase `JwtUtil` encapsula la lógica de generación y validación de tokens JWT usando la librería `jjwt 0.12.5`:

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")  // 24 horas por defecto
    private long expiration;

    public String generateToken(Usuario usuario) {
        return Jwts.builder()
            .subject(usuario.getEmail())
            .claim("id", usuario.getId())
            .claim("rol", usuario.getRol().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
```

El token JWT generado incluye como *claims* el email del usuario, su identificador (`id`) y su rol. Esto permite que el backend pueda identificar al usuario autenticado sin necesidad de consultar la base de datos en cada petición (salvo para cargar los detalles de autorización).

La respuesta del endpoint de login incluye el campo `id` del usuario junto con el token JWT, para que el frontend pueda realizar llamadas a endpoints que requieren el identificador del usuario (como `/api/pagos/cliente/{clienteId}/historial`).

---

## 6.5 Integración con Stripe (pagos y facturación)

### 6.5.1 Configuración de Stripe

La integración con Stripe se inicializa en la clase `StripeConfig`:

```java
@Configuration
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Bean
    public void initStripe() {
        Stripe.apiKey = secretKey;
    }
}
```

Las claves de Stripe se externalizan en variables de entorno para no exponerlas en el código fuente:

```properties
stripe.secret-key=${STRIPE_SECRET_KEY}
stripe.webhook-secret=${STRIPE_WEBHOOK_SECRET}
stripe.success-url=${FRONTEND_URL}/cliente/suscripcion?pago=exitoso
stripe.cancel-url=${FRONTEND_URL}/cliente/suscripcion?pago=cancelado
```

### 6.5.2 Creación de sesiones de Checkout

El flujo de pago se inicia desde el frontend, que solicita al backend la creación de una sesión de Stripe Checkout:

```java
@Override
public String crearCheckoutSession(Long clienteId, Long gimnasioId,
        Long tipoSuscripcionId) throws StripeException {

    TipoSuscripcion tipo = tipoSuscripcionRepository
        .findById(tipoSuscripcionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(successUrl)
        .setCancelUrl(cancelUrl)
        .addLineItem(
            SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount((long)(tipo.getPrecio() * 100))
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData
                                .ProductData.builder()
                                .setName(tipo.getNombre())
                                .setDescription(tipo.getDescripcion())
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .putMetadata("clienteId", clienteId.toString())
        .putMetadata("gimnasioId", gimnasioId.toString())
        .putMetadata("tipoSuscripcionId", tipoSuscripcionId.toString())
        .build();

    Session session = Session.create(params);
    return session.getUrl();
}
```

El backend devuelve la URL de la sesión de Stripe Checkout al frontend, que redirige al usuario a esa URL. Los metadatos (`clienteId`, `gimnasioId`, `tipoSuscripcionId`) permiten que el webhook procese el pago correctamente.

### 6.5.3 Webhook de Stripe y generación de facturas

El webhook es un endpoint público (`/api/pagos/webhook`) que Stripe llama cuando se produce un evento (pago completado, pago fallido, etc.). La firma del webhook se verifica para garantizar que la petición proviene realmente de Stripe:

```java
@PostMapping("/webhook")
public ResponseEntity<String> handleWebhook(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String sigHeader) {

    Event event;
    try {
        event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
    } catch (SignatureVerificationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Firma inválida");
    }

    if ("checkout.session.completed".equals(event.getType())) {
        Session session = (Session) event.getDataObjectDeserializer()
            .getObject().orElseThrow();
        pagoService.procesarPagoCompletado(session);
    }

    return ResponseEntity.ok("OK");
}
```

Al procesar el evento `checkout.session.completed`, el servicio de pagos:
1. Crea un registro de `Pago` con estado COMPLETADO.
2. Contrata la suscripción para el cliente (mediante `SuscripcionService`).
3. Genera automáticamente una `Factura` con número secuencial en formato `FAC-YYYY-NNNNN`.

La generación del número de factura garantiza unicidad mediante una consulta que obtiene el último número del año actual y lo incrementa:

```java
private String generarNumeroFactura() {
    int year = LocalDate.now().getYear();
    long ultimoNumero = facturaRepository
        .countByAnio(year);
    return String.format("FAC-%d-%05d", year, ultimoNumero + 1);
}
```

---

## 6.6 Pruebas unitarias del backend

### 6.6.1 Estrategia de testing

Las pruebas unitarias del backend se han implementado con **JUnit 5** y **Mockito**, siguiendo un enfoque de pruebas puras sin contexto Spring (`@ExtendWith(MockitoExtension.class)`). Esto garantiza que las pruebas sean rápidas, aisladas y sin dependencias de infraestructura (base de datos, red).

El patrón general de cada test es: **Arrange** (preparar los mocks y datos de entrada), **Act** (llamar al método bajo prueba) y **Assert** (verificar el resultado o las interacciones).

Se han implementado **34 pruebas unitarias** distribuidas en 4 clases de test:

### 6.6.2 GimnasioServiceTest (8 tests)

```java
@ExtendWith(MockitoExtension.class)
class GimnasioServiceTest {

    @Mock
    private GimnasioRepository gimnasioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private GimnasioServiceImpl gimnasioService;

    @Test
    void crearGimnasio_deberiaLanzarExcepcion_cuandoEmailDuplicado() {
        // Arrange
        CrearGimnasioRequest request = new CrearGimnasioRequest();
        request.setEmailPropietario("existente@email.com");
        when(usuarioRepository.existsByEmail("existente@email.com"))
            .thenReturn(true);

        // Act & Assert
        assertThrows(ResponseStatusException.class,
            () -> gimnasioService.crearGimnasioConPropietario(request));
        verify(gimnasioRepository, never()).save(any());
    }
    // ...
}
```

Los 8 tests de `GimnasioServiceTest` cubren: verificación de email duplicado al crear gimnasio, flujo completo de creación de gimnasio, cambio de estado activo/inactivo, añadir entrenador existente, añadir entrenador no encontrado, eliminar entrenador, listar gimnasios del propietario y listar gimnasios del entrenador.

### 6.6.3 SuscripcionServiceTest (8 tests)

Los 8 tests de `SuscripcionServiceTest` cubren: cancelación automática de suscripción activa anterior al contratar una nueva, cálculo correcto de fecha de fin en función de la duración, intento de añadir extra duplicado (debe lanzar excepción), listado de suscriptores activos, creación de tipo de suscripción, y creación de extra vinculado a un tipo de suscripción.

### 6.6.4 EjercicioServiceTest (8 tests)

```java
@Test
void eliminarEjercicio_deberiaLanzarExcepcion_cuandoEsPredefinido() {
    // Arrange
    Ejercicio ejercicioPredefinido = new Ejercicio();
    ejercicioPredefinido.setEsPredefinido(true);
    when(ejercicioRepository.findById(1L))
        .thenReturn(Optional.of(ejercicioPredefinido));

    // Act & Assert
    assertThrows(ResponseStatusException.class,
        () -> ejercicioService.eliminarEjercicio(1L));
}
```

Los 8 tests de `EjercicioServiceTest` cubren: bloqueo de eliminación de ejercicios predefinidos, eliminación exitosa de ejercicio personalizado, filtrado por grupo muscular de ejercicios predefinidos, filtrado por grupo muscular con ejercicios del gimnasio, creación de ejercicio personalizado, mock del `SecurityContextHolder` para obtener el gimnasio del entrenador autenticado, y listado completo de ejercicios.

### 6.6.5 EntrenamientoServiceTest (10 tests)

Los 10 tests de `EntrenamientoServiceTest` cubren: bloqueo de creación de sesión en entrenamientos de tipo ESPECIFICO, control de capacidad máxima de sesión, validación de tipo INDIVIDUAL, creación correcta de plan grupal, asignación de plan ESPECIFICO a cliente, marcado de plan como completado por el cliente, añadir ejercicio a un plan, listar planes del gimnasio, obtener detalle de un plan, e inscripción de cliente en sesión.

Para los tests que requieren acceder al usuario autenticado mediante `SecurityContextHolder`, se utiliza la función `mockStatic` de Mockito (disponible en Mockito 5, incluido en Spring Boot 3.3):

```java
@Test
void crearEjercicio_deberiaUsarGimnasioDelEntrenadorAutenticado() {
    try (MockedStatic<SecurityContextHolder> mockedStatic =
            Mockito.mockStatic(SecurityContextHolder.class)) {
        // Mock del contexto de seguridad
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        mockedStatic.when(SecurityContextHolder::getContext)
            .thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("entrenador@gym.com");
        // ...
    }
}
```

---

## Tabla de duración — Tarea 2

| Subtarea | Descripción | Duración estimada |
|----------|-------------|-------------------|
| 6.1 | Configuración del entorno y estructura | 6 horas |
| 6.2 | Modelado de entidades y persistencia | 16 horas |
| 6.3 | Implementación de servicios y lógica de negocio | 24 horas |
| 6.4 | Autenticación y seguridad con JWT | 10 horas |
| 6.5 | Integración con Stripe (pagos y facturación) | 12 horas |
| 6.6 | Pruebas unitarias del backend | 14 horas |
| **Total Tarea 2** | | **82 horas** |

---
