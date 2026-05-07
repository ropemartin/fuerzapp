package com.fuerzapp.config;

import com.fuerzapp.entity.*;
import com.fuerzapp.enums.*;
import com.fuerzapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EjercicioRepository ejercicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final GimnasioRepository gimnasioRepository;
    private final EntrenadorGimnasioRepository entrenadorGimnasioRepository;
    private final ClienteGimnasioRepository clienteGimnasioRepository;
    private final TipoSuscripcionRepository tipoSuscripcionRepository;
    private final ExtraRepository extraRepository;
    private final ClienteSuscripcionRepository clienteSuscripcionRepository;
    private final ClienteSuscripcionExtraRepository clienteSuscripcionExtraRepository;
    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoEjercicioRepository entrenamientoEjercicioRepository;
    private final SesionRepository sesionRepository;
    private final SesionClienteRepository sesionClienteRepository;
    private final EntrenamientoEspecificoRepository entrenamientoEspecificoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (ejercicioRepository.findByEsPredefinidoTrue().isEmpty()) {
            ejercicioRepository.saveAll(ejerciciosPredefinidos());
            System.out.println("✓ Biblioteca de ejercicios predefinidos cargada.");
        }
        if (usuarioRepository.count() == 0) {
            seedDemoData();
            System.out.println("✓ Datos de ejemplo cargados.");
        }
    }

    private void seedDemoData() {
        String pass = passwordEncoder.encode("123456");

        // ─── USUARIOS ─────────────────────────────────────────────────────────
        Usuario admin   = usuario("admin@fuerzapp.com",      pass, "Admin",    "FuerzApp",           "600000000", Rol.ADMIN_PLATAFORMA);
        Usuario carlos  = usuario("carlos@fitgym.com",       pass, "Carlos",   "Martínez López",     "611111111", Rol.PROPIETARIO);
        Usuario laura   = usuario("laura@powerzone.com",     pass, "Laura",    "González Ruiz",      "622222222", Rol.PROPIETARIO);
        Usuario miguel  = usuario("miguel@ironbody.com",     pass, "Miguel",   "Fernández Torres",   "633333333", Rol.PROPIETARIO);
        Usuario ana     = usuario("ana@fitgym.com",          pass, "Ana",      "García Sánchez",     "644444444", Rol.ENTRENADOR);
        Usuario pedro   = usuario("pedro@fitgym.com",        pass, "Pedro",    "Romero Vega",        "655555555", Rol.ENTRENADOR);
        Usuario sofia   = usuario("sofia@powerzone.com",     pass, "Sofía",    "Navarro Díaz",       "666666666", Rol.ENTRENADOR);
        Usuario diego   = usuario("diego@powerzone.com",     pass, "Diego",    "Moreno Herrera",     "677777777", Rol.ENTRENADOR);
        Usuario lucia   = usuario("lucia@ironbody.com",      pass, "Lucía",    "Jiménez Blanco",     "688888888", Rol.ENTRENADOR);
        Usuario marcos  = usuario("marcos@ironbody.com",     pass, "Marcos",   "Alonso Prieto",      "699999999", Rol.ENTRENADOR);
        Usuario c1      = usuario("juan@email.com",          pass, "Juan",     "López García",       "611000001", Rol.CLIENTE);
        Usuario c2      = usuario("maria@email.com",         pass, "María",    "Sánchez Pérez",      "611000002", Rol.CLIENTE);
        Usuario c3      = usuario("roberto@email.com",       pass, "Roberto",  "Torres Ruiz",        "611000003", Rol.CLIENTE);
        Usuario c4      = usuario("elena@email.com",         pass, "Elena",    "Díaz Moreno",        "611000004", Rol.CLIENTE);
        Usuario c5      = usuario("pablo@email.com",         pass, "Pablo",    "Martín Castro",      "611000005", Rol.CLIENTE);
        Usuario c6      = usuario("sara@email.com",          pass, "Sara",     "Fernández León",     "611000006", Rol.CLIENTE);
        Usuario c7      = usuario("david@email.com",         pass, "David",    "Gómez Herrera",      "611000007", Rol.CLIENTE);
        Usuario c8      = usuario("claudia@email.com",       pass, "Claudia",  "Vega Ortega",        "611000008", Rol.CLIENTE);
        Usuario c9      = usuario("andres@email.com",        pass, "Andrés",   "Rubio Iglesias",     "611000009", Rol.CLIENTE);
        Usuario c10     = usuario("patricia@email.com",      pass, "Patricia", "Santos Ramos",       "611000010", Rol.CLIENTE);
        Usuario c11     = usuario("javier@email.com",        pass, "Javier",   "Muñoz Serrano",      "611000011", Rol.CLIENTE);
        Usuario c12     = usuario("carmen@email.com",        pass, "Carmen",   "Navarro Gil",        "611000012", Rol.CLIENTE);
        Usuario c13     = usuario("luis@email.com",          pass, "Luis",     "Molina Reyes",       "611000013", Rol.CLIENTE);
        Usuario c14     = usuario("beatriz@email.com",       pass, "Beatriz",  "Aguilar Fuentes",    "611000014", Rol.CLIENTE);
        Usuario c15     = usuario("alex@email.com",          pass, "Alex",     "Vargas Montoya",     "611000015", Rol.CLIENTE);

        usuarioRepository.saveAll(List.of(admin, carlos, laura, miguel, ana, pedro, sofia, diego, lucia, marcos,
                c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15));

        // ─── GIMNASIOS ────────────────────────────────────────────────────────
        Gimnasio fitgym    = gimnasio("FitGym Madrid",        "Calle Gran Vía 45",       "Madrid",    "910000001", "info@fitgym.com",    carlos);
        Gimnasio powerzone = gimnasio("PowerZone Barcelona",  "Passeig de Gràcia 88",    "Barcelona", "930000002", "info@powerzone.com", laura);
        Gimnasio ironbody  = gimnasio("IronBody Valencia",    "Avenida del Puerto 12",   "Valencia",  "960000003", "info@ironbody.com",  miguel);
        gimnasioRepository.saveAll(List.of(fitgym, powerzone, ironbody));

        // ─── ENTRENADORES EN GIMNASIOS ────────────────────────────────────────
        entrenadorGimnasioRepository.saveAll(List.of(
                entrenadorGym(ana, fitgym), entrenadorGym(pedro, fitgym),
                entrenadorGym(sofia, powerzone), entrenadorGym(diego, powerzone),
                entrenadorGym(lucia, ironbody), entrenadorGym(marcos, ironbody)
        ));

        // ─── CLIENTES EN GIMNASIOS ────────────────────────────────────────────
        clienteGimnasioRepository.saveAll(List.of(
                clienteGym(c1, fitgym), clienteGym(c2, fitgym), clienteGym(c3, fitgym),
                clienteGym(c4, fitgym), clienteGym(c5, fitgym),
                clienteGym(c6, powerzone), clienteGym(c7, powerzone), clienteGym(c8, powerzone),
                clienteGym(c9, powerzone), clienteGym(c10, powerzone),
                clienteGym(c11, ironbody), clienteGym(c12, ironbody), clienteGym(c13, ironbody),
                clienteGym(c14, ironbody), clienteGym(c15, ironbody)
        ));

        // ─── TIPOS DE SUSCRIPCIÓN ─────────────────────────────────────────────
        TipoSuscripcion fgBasica     = tipoSus(fitgym,    "Básica",       "Acceso completo en horario estándar",           new BigDecimal("29.99"), 30);
        TipoSuscripcion fgEstandar   = tipoSus(fitgym,    "Estándar",     "Acceso completo + clases grupales",             new BigDecimal("49.99"), 30);
        TipoSuscripcion fgPremium    = tipoSus(fitgym,    "Premium",      "Acceso ilimitado + sesión personal mensual",    new BigDecimal("79.99"), 30);
        TipoSuscripcion pzBasica     = tipoSus(powerzone, "Básica",       "Acceso a sala de fitness",                      new BigDecimal("35.00"), 30);
        TipoSuscripcion pzEstandar   = tipoSus(powerzone, "Estándar",     "Acceso completo + zona CrossFit",               new BigDecimal("55.00"), 30);
        TipoSuscripcion pzPremium    = tipoSus(powerzone, "Premium",      "Todo incluido + asesoría nutricional",          new BigDecimal("89.00"), 30);
        TipoSuscripcion ibMensual    = tipoSus(ironbody,  "Mensual",      "Acceso completo al gimnasio",                   new BigDecimal("39.99"), 30);
        TipoSuscripcion ibTrimestral = tipoSus(ironbody,  "Trimestral",   "3 meses con descuento especial",                new BigDecimal("99.99"), 90);
        TipoSuscripcion ibAnual      = tipoSus(ironbody,  "Anual",        "El mejor precio por 12 meses completos",        new BigDecimal("349.99"), 365);
        tipoSuscripcionRepository.saveAll(List.of(fgBasica, fgEstandar, fgPremium, pzBasica, pzEstandar, pzPremium, ibMensual, ibTrimestral, ibAnual));

        // ─── EXTRAS ───────────────────────────────────────────────────────────
        Extra fgParking   = extra(fitgym,    "Parking mensual",         "Plaza de parking reservada",              new BigDecimal("15.00"));
        Extra fgSauna     = extra(fitgym,    "Acceso sauna y spa",      "Acceso ilimitado a zona húmeda",          new BigDecimal("20.00"));
        Extra fgNutricion = extra(fitgym,    "Plan nutricional",        "Dieta personalizada por nutricionista",   new BigDecimal("30.00"));
        Extra fgToallas   = extra(fitgym,    "Servicio de toallas",     "Toalla limpia en cada visita",            new BigDecimal("8.00"));
        Extra pzParking   = extra(powerzone, "Parking",                 "Aparcamiento cubierto",                   new BigDecimal("18.00"));
        Extra pzSauna     = extra(powerzone, "Sauna finlandesa",        "Acceso a sauna exclusiva",                new BigDecimal("25.00"));
        Extra pzMasajes   = extra(powerzone, "Sesión de masajes",       "1 masaje deportivo mensual",              new BigDecimal("35.00"));
        Extra ibParking   = extra(ironbody,  "Parking",                 "Parking privado",                        new BigDecimal("12.00"));
        Extra ibBatidos   = extra(ironbody,  "Batidos proteicos",       "1 batido post-entrenamiento diario",      new BigDecimal("22.00"));
        Extra ibTaquilla  = extra(ironbody,  "Taquilla permanente",     "Taquilla asignada permanentemente",       new BigDecimal("10.00"));
        extraRepository.saveAll(List.of(fgParking, fgSauna, fgNutricion, fgToallas, pzParking, pzSauna, pzMasajes, ibParking, ibBatidos, ibTaquilla));

        // ─── SUSCRIPCIONES ────────────────────────────────────────────────────
        LocalDate hoy = LocalDate.now();
        ClienteSuscripcion s1  = suscripcion(c1,  fgPremium,    hoy.minusDays(15), hoy.plusDays(15),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s2  = suscripcion(c2,  fgEstandar,   hoy.minusDays(5),  hoy.plusDays(25),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s3  = suscripcion(c3,  fgBasica,     hoy.minusDays(20), hoy.plusDays(10),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s4  = suscripcion(c4,  fgEstandar,   hoy.minusDays(2),  hoy.plusDays(28),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s5  = suscripcion(c5,  fgPremium,    hoy.minusDays(10), hoy.plusDays(20),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s6  = suscripcion(c6,  pzPremium,    hoy.minusDays(8),  hoy.plusDays(22),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s7  = suscripcion(c7,  pzEstandar,   hoy.minusDays(12), hoy.plusDays(18),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s8  = suscripcion(c8,  pzBasica,     hoy.minusDays(25), hoy.plusDays(5),   EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s9  = suscripcion(c9,  pzEstandar,   hoy.minusDays(3),  hoy.plusDays(27),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s10 = suscripcion(c10, pzPremium,    hoy.minusDays(18), hoy.plusDays(12),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s11 = suscripcion(c11, ibAnual,      hoy.minusDays(60), hoy.plusDays(305), EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s12 = suscripcion(c12, ibTrimestral, hoy.minusDays(30), hoy.plusDays(60),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s13 = suscripcion(c13, ibMensual,    hoy.minusDays(7),  hoy.plusDays(23),  EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s14 = suscripcion(c14, ibAnual,      hoy.minusDays(45), hoy.plusDays(320), EstadoSuscripcion.ACTIVA);
        ClienteSuscripcion s15 = suscripcion(c15, ibMensual,    hoy.minusDays(1),  hoy.plusDays(29),  EstadoSuscripcion.ACTIVA);
        clienteSuscripcionRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15));

        // ─── EXTRAS CONTRATADOS ───────────────────────────────────────────────
        clienteSuscripcionExtraRepository.saveAll(List.of(
                susExtra(s1,  fgSauna), susExtra(s1, fgParking),
                susExtra(s2,  fgNutricion),
                susExtra(s5,  fgSauna), susExtra(s5, fgToallas),
                susExtra(s6,  pzSauna), susExtra(s6, pzMasajes),
                susExtra(s7,  pzParking),
                susExtra(s10, pzSauna),
                susExtra(s11, ibBatidos), susExtra(s11, ibTaquilla),
                susExtra(s12, ibBatidos),
                susExtra(s14, ibParking), susExtra(s14, ibBatidos)
        ));

        // ─── PAGOS Y FACTURAS ─────────────────────────────────────────────────
        BigDecimal[] precios = {
                fgPremium.getPrecio(), fgEstandar.getPrecio(), fgBasica.getPrecio(),
                fgEstandar.getPrecio(), fgPremium.getPrecio(),
                pzPremium.getPrecio(), pzEstandar.getPrecio(), pzBasica.getPrecio(),
                pzEstandar.getPrecio(), pzPremium.getPrecio(),
                ibAnual.getPrecio(), ibTrimestral.getPrecio(), ibMensual.getPrecio(),
                ibAnual.getPrecio(), ibMensual.getPrecio()
        };
        ClienteSuscripcion[] suscripciones = {s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15};
        for (int i = 0; i < suscripciones.length; i++) {
            crearPagoYFactura(suscripciones[i], precios[i], i + 1);
        }

        // ─── ENTRENAMIENTOS ───────────────────────────────────────────────────
        Entrenamiento fgYoga      = entrenamiento(fitgym,    ana,    "Yoga Flow Matutino",          "Sesión de yoga para comenzar el día con energía y flexibilidad.",               TipoEntrenamiento.GRUPAL);
        Entrenamiento fgCrossfit  = entrenamiento(fitgym,    pedro,  "CrossFit WOD",                "Workout of the Day de alta intensidad con ejercicios variados.",                TipoEntrenamiento.GRUPAL);
        Entrenamiento fgPersonal  = entrenamiento(fitgym,    ana,    "Entrenamiento Personal",      "Sesión personalizada de fuerza e hipertrofia.",                                TipoEntrenamiento.INDIVIDUAL);
        Entrenamiento fgPerdida   = entrenamiento(fitgym,    pedro,  "Plan Pérdida de Peso",        "Programa específico de 8 semanas para pérdida de grasa corporal.",             TipoEntrenamiento.ESPECIFICO);

        Entrenamiento pzSpinning  = entrenamiento(powerzone, sofia,  "Spinning Intensivo",          "Clase de ciclismo indoor con música y alta energía.",                          TipoEntrenamiento.GRUPAL);
        Entrenamiento pzFuncional = entrenamiento(powerzone, diego,  "Funcional Total Body",        "Entrenamiento funcional que trabaja todo el cuerpo en circuito.",              TipoEntrenamiento.GRUPAL);
        Entrenamiento pzPower     = entrenamiento(powerzone, sofia,  "Personal Training Power",     "Entrenamiento de potencia y rendimiento atlético.",                            TipoEntrenamiento.INDIVIDUAL);
        Entrenamiento pzDefinic   = entrenamiento(powerzone, diego,  "Plan Definición Muscular",    "Programa de definición muscular orientado a competición.",                     TipoEntrenamiento.ESPECIFICO);

        Entrenamiento ibPower     = entrenamiento(ironbody,  lucia,  "Powerlifting Grupal",         "Entrenamiento de fuerza máxima con los tres grandes levantamientos.",           TipoEntrenamiento.GRUPAL);
        Entrenamiento ibHiit      = entrenamiento(ironbody,  marcos, "HIIT Metabólico",             "Intervalos de alta intensidad para quemar grasa y mejorar el cardio.",         TipoEntrenamiento.GRUPAL);
        Entrenamiento ibCompet    = entrenamiento(ironbody,  lucia,  "Preparación Powerlifting",    "Entrenamiento individual para competición de powerlifting.",                    TipoEntrenamiento.INDIVIDUAL);
        Entrenamiento ibVolumen   = entrenamiento(ironbody,  marcos, "Programa Volumen",            "Plan de volumen muscular limpio de 12 semanas.",                               TipoEntrenamiento.ESPECIFICO);

        entrenamientoRepository.saveAll(List.of(
                fgYoga, fgCrossfit, fgPersonal, fgPerdida,
                pzSpinning, pzFuncional, pzPower, pzDefinic,
                ibPower, ibHiit, ibCompet, ibVolumen
        ));

        // ─── EJERCICIOS EN ENTRENAMIENTOS ─────────────────────────────────────
        List<Ejercicio> predefinidos = ejercicioRepository.findByEsPredefinidoTrue();
        Ejercicio pressBanca   = ej(predefinidos, "Press de banca");
        Ejercicio flexiones    = ej(predefinidos, "Flexiones");
        Ejercicio dominadas    = ej(predefinidos, "Dominadas");
        Ejercicio remoBarra    = ej(predefinidos, "Remo con barra");
        Ejercicio sentadilla   = ej(predefinidos, "Sentadilla con barra");
        Ejercicio pressMilitar = ej(predefinidos, "Press militar");
        Ejercicio elevLateral  = ej(predefinidos, "Elevaciones laterales");
        Ejercicio curl         = ej(predefinidos, "Curl con barra");
        Ejercicio plancha      = ej(predefinidos, "Plancha");
        Ejercicio hipThrust    = ej(predefinidos, "Hip thrust");
        Ejercicio burpees      = ej(predefinidos, "Burpees");
        Ejercicio prensa       = ej(predefinidos, "Prensa de piernas");
        Ejercicio zancadas     = ej(predefinidos, "Zancadas");
        Ejercicio pesoMuerto   = ej(predefinidos, "Peso muerto");
        Ejercicio cinta        = ej(predefinidos, "Carrera en cinta");
        Ejercicio crunch       = ej(predefinidos, "Crunch abdominal");
        Ejercicio curlFemoral  = ej(predefinidos, "Curl femoral");
        Ejercicio saltoComba   = ej(predefinidos, "Salto a la comba");
        Ejercicio fondos       = ej(predefinidos, "Fondos en paralelas");
        Ejercicio rueda        = ej(predefinidos, "Rueda abdominal");

        entrenamientoEjercicioRepository.saveAll(List.of(
                // FitGym — Yoga Flow
                ee(fgYoga, plancha,    1, 3, null, 60,   30, "Posición perfecta de columna"),
                ee(fgYoga, crunch,     2, 3, 15,   null, 30, "Movimiento lento y controlado"),
                ee(fgYoga, flexiones,  3, 3, 10,   null, 45, "Caderas alineadas"),

                // FitGym — CrossFit WOD
                ee(fgCrossfit, burpees,    1, 5, 10, null, 60,  "Máxima intensidad"),
                ee(fgCrossfit, sentadilla, 2, 4, 15, null, 45,  "Profundidad completa"),
                ee(fgCrossfit, pressBanca, 3, 4, 12, null, 60,  null),
                ee(fgCrossfit, dominadas,  4, 3, 8,  null, 90,  "Hasta el fallo si es posible"),
                ee(fgCrossfit, plancha,    5, 3, null,60,  30,  null),

                // FitGym — Personal
                ee(fgPersonal, pressBanca,   1, 4, 8,  null, 90,  "Peso progresivo cada semana"),
                ee(fgPersonal, pesoMuerto,   2, 4, 6,  null, 120, "Técnica prioritaria"),
                ee(fgPersonal, sentadilla,   3, 4, 8,  null, 90,  null),
                ee(fgPersonal, pressMilitar, 4, 3, 10, null, 60,  null),
                ee(fgPersonal, dominadas,    5, 3, null,null,60,  "Asistido si es necesario"),

                // FitGym — Pérdida de peso
                ee(fgPerdida, cinta,     1, 1, null, 1200, 0,  "20 min calentamiento"),
                ee(fgPerdida, burpees,   2, 4, 10,  null, 60,  null),
                ee(fgPerdida, sentadilla,3, 4, 15,  null, 45,  "Peso moderado"),
                ee(fgPerdida, plancha,   4, 3, null, 45,  30,  null),
                ee(fgPerdida, zancadas,  5, 3, 12,  null, 45,  "Cada pierna"),

                // PowerZone — Spinning
                ee(pzSpinning, cinta,     1, 1, null, 2700, 0,  "45 min spinning en bicicleta estática"),
                ee(pzSpinning, saltoComba,2, 3, null, 120,  30, "Intervalos de sprint"),

                // PowerZone — Funcional
                ee(pzFuncional, sentadilla, 1, 4, 12, null, 60, null),
                ee(pzFuncional, pressBanca, 2, 4, 12, null, 60, null),
                ee(pzFuncional, remoBarra,  3, 4, 12, null, 60, null),
                ee(pzFuncional, plancha,    4, 3, null,45,  30, null),
                ee(pzFuncional, burpees,    5, 3, 10, null, 60, null),
                ee(pzFuncional, zancadas,   6, 3, 10, null, 45, "Cada pierna"),

                // PowerZone — Power Personal
                ee(pzPower, pesoMuerto,   1, 5, 5,  null, 180, "Fuerza máxima — técnica impecable"),
                ee(pzPower, sentadilla,   2, 5, 5,  null, 180, "Alta intensidad"),
                ee(pzPower, pressBanca,   3, 5, 5,  null, 120, null),
                ee(pzPower, pressMilitar, 4, 4, 6,  null, 90,  null),
                ee(pzPower, dominadas,    5, 3, 8,  null, 90,  null),

                // PowerZone — Definición
                ee(pzDefinic, pressBanca,  1, 4, 12, null, 60, "Peso moderado-alto"),
                ee(pzDefinic, elevLateral, 2, 3, 15, null, 45, "Aislamiento deltoides"),
                ee(pzDefinic, curl,        3, 3, 12, null, 45, null),
                ee(pzDefinic, fondos,      4, 3, 15, null, 60, null),
                ee(pzDefinic, crunch,      5, 4, 20, null, 30, null),
                ee(pzDefinic, hipThrust,   6, 4, 15, null, 60, null),

                // IronBody — Powerlifting Grupal
                ee(ibPower, pesoMuerto, 1, 5, 5, null, 180, "Carga máxima del día"),
                ee(ibPower, sentadilla, 2, 5, 5, null, 180, "Sentadilla competición"),
                ee(ibPower, pressBanca, 3, 5, 5, null, 180, "Grip de competición"),

                // IronBody — HIIT
                ee(ibHiit, burpees,    1, 4, 15, null, 30,  "Sin descanso entre series"),
                ee(ibHiit, saltoComba, 2, 3, null,60,  30,  "Velocidad máxima"),
                ee(ibHiit, plancha,    3, 3, null,60,  30,  null),
                ee(ibHiit, sentadilla, 4, 3, 20, null, 45,  "Peso ligero, alta velocidad"),
                ee(ibHiit, rueda,      5, 3, 10, null, 60,  "Core finisher"),

                // IronBody — Competición
                ee(ibCompet, pesoMuerto, 1, 6, 3, null, 180, "Máximo absoluto"),
                ee(ibCompet, sentadilla, 2, 6, 3, null, 180, "Profundidad reglamentaria"),
                ee(ibCompet, pressBanca, 3, 6, 3, null, 180, "Pausa en pecho"),

                // IronBody — Volumen
                ee(ibVolumen, sentadilla,   1, 5, 8,  null, 90,  "Volumen — peso 70%"),
                ee(ibVolumen, pressBanca,   2, 5, 8,  null, 90,  null),
                ee(ibVolumen, remoBarra,    3, 5, 8,  null, 90,  null),
                ee(ibVolumen, pressMilitar, 4, 4, 10, null, 75,  null),
                ee(ibVolumen, pesoMuerto,   5, 3, 6,  null, 120, "Cierre de sesión"),
                ee(ibVolumen, curlFemoral,  6, 3, 12, null, 60,  null),
                ee(ibVolumen, prensa,       7, 3, 15, null, 60,  null)
        ));

        // ─── SESIONES ─────────────────────────────────────────────────────────
        LocalDateTime ahora = LocalDateTime.now();

        Sesion fgS1   = sesion(fgYoga,     ahora.plusDays(1).withHour(9).withMinute(0),   60, "Sala Yoga",       15);
        Sesion fgS2   = sesion(fgYoga,     ahora.plusDays(3).withHour(9).withMinute(0),   60, "Sala Yoga",       15);
        Sesion fgS3   = sesion(fgYoga,     ahora.plusDays(5).withHour(9).withMinute(0),   60, "Sala Yoga",       15);
        Sesion fgS4   = sesion(fgCrossfit, ahora.plusDays(1).withHour(18).withMinute(30), 45, "Sala CrossFit",   12);
        Sesion fgS5   = sesion(fgCrossfit, ahora.plusDays(4).withHour(18).withMinute(30), 45, "Sala CrossFit",   12);
        Sesion fgS6   = sesion(fgCrossfit, ahora.minusDays(2).withHour(18).withMinute(30),45, "Sala CrossFit",   12);
        Sesion fgSI1  = sesion(fgPersonal, ahora.plusDays(2).withHour(10).withMinute(0),  60, "Sala Máquinas",   null);
        Sesion fgSI2  = sesion(fgPersonal, ahora.plusDays(6).withHour(11).withMinute(0),  60, "Sala Máquinas",   null);

        Sesion pzS1   = sesion(pzSpinning,  ahora.plusDays(1).withHour(7).withMinute(30),  45, "Sala Spinning",   20);
        Sesion pzS2   = sesion(pzSpinning,  ahora.plusDays(3).withHour(7).withMinute(30),  45, "Sala Spinning",   20);
        Sesion pzS3   = sesion(pzSpinning,  ahora.plusDays(5).withHour(7).withMinute(30),  45, "Sala Spinning",   20);
        Sesion pzS4   = sesion(pzFuncional, ahora.plusDays(2).withHour(19).withMinute(0),  55, "Sala Funcional",  16);
        Sesion pzS5   = sesion(pzFuncional, ahora.plusDays(4).withHour(19).withMinute(0),  55, "Sala Funcional",  16);
        Sesion pzSI1  = sesion(pzPower,     ahora.plusDays(1).withHour(11).withMinute(0),  75, "Zona Olímpica",   null);
        Sesion pzSI2  = sesion(pzPower,     ahora.plusDays(3).withHour(11).withMinute(0),  75, "Zona Olímpica",   null);

        Sesion ibS1   = sesion(ibPower,  ahora.plusDays(2).withHour(8).withMinute(0),   90, "Plataforma Olímpica", 8);
        Sesion ibS2   = sesion(ibPower,  ahora.plusDays(5).withHour(8).withMinute(0),   90, "Plataforma Olímpica", 8);
        Sesion ibS3   = sesion(ibHiit,   ahora.plusDays(1).withHour(20).withMinute(0),  40, "Sala HIIT",           15);
        Sesion ibS4   = sesion(ibHiit,   ahora.plusDays(4).withHour(20).withMinute(0),  40, "Sala HIIT",           15);
        Sesion ibS5   = sesion(ibHiit,   ahora.minusDays(1).withHour(20).withMinute(0), 40, "Sala HIIT",           15);
        Sesion ibSI1  = sesion(ibCompet, ahora.plusDays(2).withHour(16).withMinute(0),  90, "Plataforma Olímpica", null);
        Sesion ibSI2  = sesion(ibCompet, ahora.plusDays(7).withHour(16).withMinute(0),  90, "Plataforma Olímpica", null);

        sesionRepository.saveAll(List.of(
                fgS1, fgS2, fgS3, fgS4, fgS5, fgS6, fgSI1, fgSI2,
                pzS1, pzS2, pzS3, pzS4, pzS5, pzSI1, pzSI2,
                ibS1, ibS2, ibS3, ibS4, ibS5, ibSI1, ibSI2
        ));

        // ─── INSCRIPCIONES EN SESIONES ────────────────────────────────────────
        sesionClienteRepository.saveAll(List.of(
                // FitGym Yoga — próximas
                inscribir(fgS1, c1), inscribir(fgS1, c2), inscribir(fgS1, c3), inscribir(fgS1, c4),
                inscribir(fgS2, c1), inscribir(fgS2, c3), inscribir(fgS2, c5),
                inscribir(fgS3, c2), inscribir(fgS3, c4),
                // FitGym CrossFit — pasada con asistencia
                inscribirAsistido(fgS6, c1, true), inscribirAsistido(fgS6, c2, true),
                inscribirAsistido(fgS6, c3, false), inscribirAsistido(fgS6, c5, true),
                // FitGym CrossFit — próximas
                inscribir(fgS4, c1), inscribir(fgS4, c2), inscribir(fgS4, c4),
                inscribir(fgS5, c3), inscribir(fgS5, c5),
                // FitGym individual
                inscribir(fgSI1, c1),
                inscribir(fgSI2, c2),

                // PowerZone Spinning
                inscribir(pzS1, c6), inscribir(pzS1, c7), inscribir(pzS1, c8), inscribir(pzS1, c9), inscribir(pzS1, c10),
                inscribir(pzS2, c6), inscribir(pzS2, c8), inscribir(pzS2, c10),
                inscribir(pzS3, c7), inscribir(pzS3, c9),
                // PowerZone Funcional
                inscribir(pzS4, c6), inscribir(pzS4, c7), inscribir(pzS4, c8),
                inscribir(pzS5, c9), inscribir(pzS5, c10),
                // PowerZone individual
                inscribir(pzSI1, c6),
                inscribir(pzSI2, c7),

                // IronBody Powerlifting
                inscribir(ibS1, c11), inscribir(ibS1, c12), inscribir(ibS1, c13),
                inscribir(ibS2, c11), inscribir(ibS2, c14),
                // IronBody HIIT — pasada
                inscribirAsistido(ibS5, c11, true), inscribirAsistido(ibS5, c12, true),
                inscribirAsistido(ibS5, c13, true), inscribirAsistido(ibS5, c14, false),
                // IronBody HIIT — próximas
                inscribir(ibS3, c11), inscribir(ibS3, c12), inscribir(ibS3, c13), inscribir(ibS3, c14), inscribir(ibS3, c15),
                inscribir(ibS4, c12), inscribir(ibS4, c15),
                // IronBody individual
                inscribir(ibSI1, c11),
                inscribir(ibSI2, c14)
        ));

        // ─── PLANES ESPECÍFICOS ASIGNADOS ─────────────────────────────────────
        entrenamientoEspecificoRepository.saveAll(List.of(
                especifico(fgPerdida, c3, "Objetivo: perder 5kg en 2 meses. Combinar con dieta hipocalórica."),
                especifico(fgPerdida, c4, "Enfocada en tonificación. Progresión semanal del cardio."),
                especifico(fgPerdida, c5, "Recuperación post-lesión de rodilla. Evitar impacto articular."),

                especifico(pzDefinic, c8,  "Preparación foto verano. 10 semanas de definición."),
                especifico(pzDefinic, c9,  "Definición para competición amateur. Añadir cardio en ayunas."),
                especifico(pzDefinic, c10, "Primer ciclo de definición. Déficit calórico moderado."),

                especifico(ibVolumen, c13, "Volumen limpio. Objetivo: +5kg de masa muscular en 12 semanas."),
                especifico(ibVolumen, c14, "Preparación offseason para competición de powerlifting."),
                especifico(ibVolumen, c15, "Primer programa de musculación. Base de fuerza y técnica.")
        ));
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private void crearPagoYFactura(ClienteSuscripcion s, BigDecimal importe, int num) {
        Pago p = new Pago();
        p.setClienteSuscripcion(s);
        p.setImporte(importe);
        p.setFecha(LocalDateTime.now().minusDays(num));
        p.setEstado(EstadoPago.COMPLETADO);
        pagoRepository.save(p);

        Factura f = new Factura();
        f.setPago(p);
        f.setNumeroFactura(String.format("FAC-2026-%05d", num));
        f.setFechaEmision(p.getFecha());
        f.setImporteTotal(importe);
        facturaRepository.save(f);
    }

    private Usuario usuario(String email, String pass, String nombre, String apellidos, String telefono, Rol rol) {
        Usuario u = new Usuario();
        u.setEmail(email); u.setPassword(pass); u.setNombre(nombre);
        u.setApellidos(apellidos); u.setTelefono(telefono); u.setRol(rol);
        return u;
    }

    private Gimnasio gimnasio(String nombre, String dir, String ciudad, String tel, String email, Usuario prop) {
        Gimnasio g = new Gimnasio();
        g.setNombre(nombre); g.setDireccion(dir); g.setCiudad(ciudad);
        g.setTelefono(tel); g.setEmail(email); g.setPropietario(prop);
        return g;
    }

    private EntrenadorGimnasio entrenadorGym(Usuario e, Gimnasio g) {
        EntrenadorGimnasio eg = new EntrenadorGimnasio();
        eg.setEntrenador(e); eg.setGimnasio(g); return eg;
    }

    private ClienteGimnasio clienteGym(Usuario c, Gimnasio g) {
        ClienteGimnasio cg = new ClienteGimnasio();
        cg.setCliente(c); cg.setGimnasio(g); return cg;
    }

    private TipoSuscripcion tipoSus(Gimnasio g, String nombre, String desc, BigDecimal precio, int dias) {
        TipoSuscripcion t = new TipoSuscripcion();
        t.setGimnasio(g); t.setNombre(nombre); t.setDescripcion(desc);
        t.setPrecio(precio); t.setDuracionDias(dias); return t;
    }

    private Extra extra(Gimnasio g, String nombre, String desc, BigDecimal precio) {
        Extra e = new Extra();
        e.setGimnasio(g); e.setNombre(nombre); e.setDescripcion(desc); e.setPrecio(precio); return e;
    }

    private ClienteSuscripcion suscripcion(Usuario c, TipoSuscripcion t, LocalDate ini, LocalDate fin, EstadoSuscripcion estado) {
        ClienteSuscripcion cs = new ClienteSuscripcion();
        cs.setCliente(c); cs.setTipoSuscripcion(t);
        cs.setFechaInicio(ini); cs.setFechaFin(fin); cs.setEstado(estado); return cs;
    }

    private ClienteSuscripcionExtra susExtra(ClienteSuscripcion cs, Extra e) {
        ClienteSuscripcionExtra cse = new ClienteSuscripcionExtra();
        cse.setClienteSuscripcion(cs); cse.setExtra(e); return cse;
    }

    private Entrenamiento entrenamiento(Gimnasio g, Usuario entrenador, String nombre, String desc, TipoEntrenamiento tipo) {
        Entrenamiento e = new Entrenamiento();
        e.setGimnasio(g); e.setEntrenador(entrenador); e.setNombre(nombre);
        e.setDescripcion(desc); e.setTipo(tipo); return e;
    }

    private EntrenamientoEjercicio ee(Entrenamiento e, Ejercicio ej, int orden,
                                      Integer series, Integer reps, Integer durSeg,
                                      Integer descSeg, String notas) {
        EntrenamientoEjercicio ee = new EntrenamientoEjercicio();
        ee.setEntrenamiento(e); ee.setEjercicio(ej); ee.setOrden(orden);
        ee.setSeries(series); ee.setRepeticiones(reps);
        ee.setDuracionSegundos(durSeg); ee.setDescansoSegundos(descSeg);
        ee.setNotas(notas); return ee;
    }

    private Sesion sesion(Entrenamiento e, LocalDateTime fh, int dur, String ubic, Integer cap) {
        Sesion s = new Sesion();
        s.setEntrenamiento(e); s.setFechaHora(fh);
        s.setDuracionMinutos(dur); s.setUbicacion(ubic); s.setCapacidadMaxima(cap); return s;
    }

    private SesionCliente inscribir(Sesion s, Usuario c) {
        SesionCliente sc = new SesionCliente();
        sc.setSesion(s); sc.setCliente(c); sc.setAsistio(false); return sc;
    }

    private SesionCliente inscribirAsistido(Sesion s, Usuario c, boolean asistio) {
        SesionCliente sc = new SesionCliente();
        sc.setSesion(s); sc.setCliente(c); sc.setAsistio(asistio); return sc;
    }

    private EntrenamientoEspecifico especifico(Entrenamiento e, Usuario c, String notas) {
        EntrenamientoEspecifico ee = new EntrenamientoEspecifico();
        ee.setEntrenamiento(e); ee.setCliente(c); ee.setNotas(notas); ee.setCompletado(false); return ee;
    }

    private Ejercicio ej(List<Ejercicio> lista, String nombre) {
        return lista.stream().filter(e -> e.getNombre().equals(nombre)).findFirst()
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado: " + nombre));
    }

    // ─── EJERCICIOS PREDEFINIDOS ──────────────────────────────────────────────

    private List<Ejercicio> ejerciciosPredefinidos() {
        return List.of(
                crearEjercicio("Press de banca",               "Ejercicio básico de empuje horizontal con barra.",                                       GrupoMuscular.PECHO,    Dificultad.INTERMEDIO),
                crearEjercicio("Flexiones",                    "Ejercicio de empuje con peso corporal.",                                                  GrupoMuscular.PECHO,    Dificultad.PRINCIPIANTE),
                crearEjercicio("Aperturas con mancuernas",     "Ejercicio de aislamiento para el pecho en banco plano.",                                  GrupoMuscular.PECHO,    Dificultad.INTERMEDIO),
                crearEjercicio("Press inclinado con barra",    "Variante del press de banca en banco inclinado para el pecho superior.",                  GrupoMuscular.PECHO,    Dificultad.INTERMEDIO),
                crearEjercicio("Dominadas",                    "Ejercicio de tracción vertical con peso corporal.",                                       GrupoMuscular.ESPALDA,  Dificultad.INTERMEDIO),
                crearEjercicio("Remo con barra",               "Ejercicio de tracción horizontal con carga libre.",                                       GrupoMuscular.ESPALDA,  Dificultad.INTERMEDIO),
                crearEjercicio("Jalón al pecho",               "Tracción vertical en polea alta.",                                                        GrupoMuscular.ESPALDA,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Peso muerto",                  "Ejercicio multiarticular que trabaja principalmente la cadena posterior.",                 GrupoMuscular.ESPALDA,  Dificultad.AVANZADO),
                crearEjercicio("Sentadilla con barra",         "Ejercicio multiarticular fundamental para el tren inferior.",                             GrupoMuscular.PIERNAS,  Dificultad.INTERMEDIO),
                crearEjercicio("Prensa de piernas",            "Ejercicio de empuje de piernas en máquina.",                                              GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Zancadas",                     "Ejercicio unilateral para cuádriceps y glúteos.",                                         GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Curl femoral",                 "Ejercicio de aislamiento para los isquiotibiales.",                                       GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Press militar",                "Ejercicio de empuje vertical para hombros con barra.",                                    GrupoMuscular.HOMBROS,  Dificultad.INTERMEDIO),
                crearEjercicio("Elevaciones laterales",        "Ejercicio de aislamiento para el deltoides lateral.",                                     GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Pájaro",                       "Ejercicio de aislamiento para el deltoides posterior.",                                   GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Curl con barra",               "Ejercicio básico de flexión de codo para bíceps.",                                        GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE),
                crearEjercicio("Curl martillo",                "Variante del curl que trabaja el braquial y braquiorradial.",                             GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE),
                crearEjercicio("Fondos en paralelas",          "Ejercicio de empuje con peso corporal para tríceps.",                                     GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO),
                crearEjercicio("Press francés",                "Ejercicio de aislamiento para tríceps con barra EZ.",                                     GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO),
                crearEjercicio("Extensión de tríceps en polea","Ejercicio de aislamiento en polea alta.",                                                 GrupoMuscular.TRICEPS,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Plancha",                      "Ejercicio isométrico de core.",                                                           GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Crunch abdominal",             "Ejercicio básico de flexión de tronco.",                                                  GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Rueda abdominal",              "Ejercicio avanzado de extensión de core.",                                                GrupoMuscular.ABDOMEN,  Dificultad.AVANZADO),
                crearEjercicio("Hip thrust",                   "Ejercicio de empuje de cadera para glúteos con barra.",                                   GrupoMuscular.GLUTEOS,  Dificultad.INTERMEDIO),
                crearEjercicio("Patada de glúteo en polea",    "Ejercicio de aislamiento de glúteo en polea baja.",                                       GrupoMuscular.GLUTEOS,  Dificultad.PRINCIPIANTE),
                crearEjercicio("Carrera en cinta",             "Cardio aeróbico en cinta de correr.",                                                     GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE),
                crearEjercicio("Burpees",                      "Ejercicio de cardio de alta intensidad con peso corporal.",                               GrupoMuscular.CARDIO,   Dificultad.AVANZADO),
                crearEjercicio("Salto a la comba",             "Cardio de bajo impacto con cuerda de saltar.",                                            GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE),
                crearEjercicio("Remo en máquina",              "Cardio de bajo impacto que trabaja todo el cuerpo.",                                      GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE)
        );
    }

    private Ejercicio crearEjercicio(String nombre, String descripcion, GrupoMuscular grupo, Dificultad dificultad) {
        Ejercicio e = new Ejercicio();
        e.setNombre(nombre); e.setDescripcion(descripcion);
        e.setGrupoMuscular(grupo); e.setDificultad(dificultad);
        e.setEsPredefinido(true); return e;
    }
}
