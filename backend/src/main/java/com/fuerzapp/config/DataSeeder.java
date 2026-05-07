package com.fuerzapp.config;

import com.fuerzapp.entity.*;
import com.fuerzapp.enums.*;
import com.fuerzapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        Gimnasio fitgym    = gimnasio("FitGym Madrid",        "Calle Gran Vía 45",       "Madrid",    "910000001", "info@fitgym.com",    carlos, 21);
        Gimnasio powerzone = gimnasio("PowerZone Barcelona",  "Passeig de Gràcia 88",    "Barcelona", "930000002", "info@powerzone.com", laura,  21);
        Gimnasio ironbody  = gimnasio("IronBody Valencia",    "Avenida del Puerto 12",   "Valencia",  "960000003", "info@ironbody.com",  miguel, 10);
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

        // ─── EJERCICIOS — FITGYM ──────────────────────────────────────────────
        Ejercicio fg_pressBanca    = ej(fitgym, ana,   "Press de banca",              "Empuje horizontal con barra en banco plano.",                         GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+banca+tecnica+correcta");
        Ejercicio fg_flexiones     = ej(fitgym, ana,   "Flexiones",                   "Empuje con peso corporal, codos a 45°.",                              GrupoMuscular.PECHO,    Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=flexiones+push+up+tecnica+correcta");
        Ejercicio fg_pressInclin   = ej(fitgym, ana,   "Press inclinado con barra",   "Variante inclinada para trabajar el pecho superior.",                 GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+inclinado+barra+pecho+superior");
        Ejercicio fg_aperturas     = ej(fitgym, ana,   "Aperturas con mancuernas",    "Aislamiento de pecho en banco plano.",                                GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=aperturas+mancuernas+pecho+tecnica");
        Ejercicio fg_dominadas     = ej(fitgym, ana,   "Dominadas",                   "Tracción vertical con peso corporal, agarre prono.",                  GrupoMuscular.ESPALDA,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=dominadas+pull+up+tecnica+espalda");
        Ejercicio fg_remoBarra     = ej(fitgym, ana,   "Remo con barra",              "Tracción horizontal con barra, torso inclinado.",                     GrupoMuscular.ESPALDA,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=remo+con+barra+espalda+tecnica");
        Ejercicio fg_jalón         = ej(fitgym, ana,   "Jalón al pecho",              "Tracción vertical en polea alta.",                                    GrupoMuscular.ESPALDA,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=jalon+al+pecho+polea+alta+espalda");
        Ejercicio fg_pesoMuerto    = ej(fitgym, ana,   "Peso muerto",                 "Cadena posterior completa, técnica imprescindible.",                  GrupoMuscular.ESPALDA,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=peso+muerto+deadlift+tecnica+correcta");
        Ejercicio fg_remoMancuerna = ej(fitgym, ana,   "Remo con mancuerna",          "Remo unilateral apoyado en banco para mayor rango de movimiento.",    GrupoMuscular.ESPALDA,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=remo+mancuerna+unilateral+espalda");
        Ejercicio fg_sentadilla    = ej(fitgym, pedro, "Sentadilla con barra",        "Ejercicio rey del tren inferior.",                                    GrupoMuscular.PIERNAS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=sentadilla+barra+squat+tecnica+correcta");
        Ejercicio fg_prensa        = ej(fitgym, pedro, "Prensa de piernas",           "Empuje de piernas en máquina, variando ángulo.",                      GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=prensa+piernas+leg+press+tecnica");
        Ejercicio fg_zancadas      = ej(fitgym, pedro, "Zancadas",                    "Ejercicio unilateral para cuádriceps y glúteos.",                     GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=zancadas+lunges+piernas+tecnica");
        Ejercicio fg_curlFemoral   = ej(fitgym, pedro, "Curl femoral",                "Aislamiento de isquiotibiales en máquina.",                           GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+femoral+leg+curl+isquiotibiales");
        Ejercicio fg_sentGoblet    = ej(fitgym, pedro, "Sentadilla goblet",           "Sentadilla frontal sosteniendo una mancuerna, ideal para técnica.",   GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=sentadilla+goblet+mancuerna+tecnica");
        Ejercicio fg_pressMilitar  = ej(fitgym, pedro, "Press militar",               "Empuje vertical para hombros con barra.",                             GrupoMuscular.HOMBROS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+militar+overhead+press+hombros");
        Ejercicio fg_elevLateral   = ej(fitgym, pedro, "Elevaciones laterales",       "Aislamiento del deltoides lateral.",                                  GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=elevaciones+laterales+deltoides+hombros");
        Ejercicio fg_pajaro        = ej(fitgym, pedro, "Pájaro",                      "Aislamiento del deltoides posterior en inclinación.",                 GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=pajaro+deltoides+posterior+rear+delt+fly");
        Ejercicio fg_curlBarra     = ej(fitgym, ana,   "Curl con barra",              "Flexión de codo con barra recta.",                                    GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+biceps+barra+recta+tecnica");
        Ejercicio fg_curlMartillo  = ej(fitgym, ana,   "Curl martillo",               "Trabaja bíceps y braquiorradial con agarre neutro.",                  GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+martillo+hammer+curl+biceps");
        Ejercicio fg_fondos        = ej(fitgym, pedro, "Fondos en paralelas",         "Empuje con peso corporal para tríceps y pecho.",                      GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=fondos+paralelas+triceps+tecnica");
        Ejercicio fg_pressFrances  = ej(fitgym, pedro, "Press francés",               "Aislamiento de tríceps con barra EZ tumbado.",                        GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+frances+skull+crusher+triceps");
        Ejercicio fg_extTriPolea   = ej(fitgym, ana,   "Extensión de tríceps polea",  "Aislamiento de tríceps en polea alta.",                               GrupoMuscular.TRICEPS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=extension+triceps+polea+alta+aislamiento");
        Ejercicio fg_plancha       = ej(fitgym, ana,   "Plancha",                     "Ejercicio isométrico de core, columna neutra.",                       GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=plancha+plank+core+isometrico+tecnica");
        Ejercicio fg_crunch        = ej(fitgym, ana,   "Crunch abdominal",            "Flexión de tronco controlada.",                                       GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=crunch+abdominal+tecnica+correcta");
        Ejercicio fg_rueda         = ej(fitgym, ana,   "Rueda abdominal",             "Extensión de core avanzada con rueda.",                               GrupoMuscular.ABDOMEN,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=rueda+abdominal+ab+wheel+rollout+core");
        Ejercicio fg_hipThrust     = ej(fitgym, pedro, "Hip thrust",                  "Empuje de cadera con barra para glúteos.",                            GrupoMuscular.GLUTEOS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=hip+thrust+gluteos+barra+tecnica");
        Ejercicio fg_patadaGluteo  = ej(fitgym, pedro, "Patada de glúteo",            "Extensión de cadera unilateral en polea o máquina.",                  GrupoMuscular.GLUTEOS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=patada+gluteo+glute+kickback+polea");
        Ejercicio fg_cinta         = ej(fitgym, pedro, "Carrera en cinta",            "Cardio aeróbico continuo o en intervalos.",                           GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=correr+cinta+treadmill+cardio+tecnica");
        Ejercicio fg_burpees       = ej(fitgym, pedro, "Burpees",                     "Ejercicio de alta intensidad: sentadilla, plancha y salto.",           GrupoMuscular.CARDIO,   Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=burpees+tecnica+correcta+hiit");
        Ejercicio fg_comba         = ej(fitgym, pedro, "Salto a la comba",            "Cardio de coordinación con cuerda de saltar.",                        GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=salto+comba+jump+rope+tecnica+cardio");

        ejercicioRepository.saveAll(List.of(
                fg_pressBanca, fg_flexiones, fg_pressInclin, fg_aperturas,
                fg_dominadas, fg_remoBarra, fg_jalón, fg_pesoMuerto, fg_remoMancuerna,
                fg_sentadilla, fg_prensa, fg_zancadas, fg_curlFemoral, fg_sentGoblet,
                fg_pressMilitar, fg_elevLateral, fg_pajaro,
                fg_curlBarra, fg_curlMartillo,
                fg_fondos, fg_pressFrances, fg_extTriPolea,
                fg_plancha, fg_crunch, fg_rueda,
                fg_hipThrust, fg_patadaGluteo,
                fg_cinta, fg_burpees, fg_comba
        ));

        // ─── EJERCICIOS — POWERZONE ───────────────────────────────────────────
        Ejercicio pz_pressBanca    = ej(powerzone, sofia, "Press de banca",              "Empuje horizontal con barra, técnica de competición.",                GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+banca+tecnica+correcta");
        Ejercicio pz_flexiones     = ej(powerzone, sofia, "Flexiones con lastre",        "Flexiones estándar con chaleco lastrado.",                            GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=flexiones+con+lastre+weighted+push+up");
        Ejercicio pz_apertPolea    = ej(powerzone, sofia, "Aperturas en polea",          "Cruce de poleas para trabajar el pecho en estiramiento.",             GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=aperturas+polea+cable+crossover+pecho");
        Ejercicio pz_pressIncManc  = ej(powerzone, sofia, "Press inclinado mancuernas",  "Pecho superior con mayor rango de movimiento que la barra.",          GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+inclinado+mancuernas+pecho+superior");
        Ejercicio pz_dominadas     = ej(powerzone, diego, "Dominadas",                   "Tracción vertical con peso corporal.",                                GrupoMuscular.ESPALDA,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=dominadas+pull+up+tecnica+espalda");
        Ejercicio pz_remoManc      = ej(powerzone, diego, "Remo con mancuerna",          "Remo unilateral para mayor amplitud de movimiento.",                  GrupoMuscular.ESPALDA,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=remo+mancuerna+unilateral+espalda");
        Ejercicio pz_jalón         = ej(powerzone, diego, "Jalón al pecho",              "Tracción en polea alta, agarre ancho.",                               GrupoMuscular.ESPALDA,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=jalon+al+pecho+polea+alta+espalda");
        Ejercicio pz_pesoMuerto    = ej(powerzone, diego, "Peso muerto rumano",          "Cadena posterior con rodillas semi-flexionadas.",                     GrupoMuscular.ESPALDA,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=peso+muerto+rumano+romanian+deadlift");
        Ejercicio pz_remoMaquina   = ej(powerzone, diego, "Remo en máquina",             "Tracción horizontal en máquina de remo.",                             GrupoMuscular.ESPALDA,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=remo+maquina+machine+row+espalda");
        Ejercicio pz_sentadilla    = ej(powerzone, sofia, "Sentadilla con barra",        "Ejercicio fundamental de tren inferior.",                             GrupoMuscular.PIERNAS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=sentadilla+barra+squat+tecnica+correcta");
        Ejercicio pz_prensa        = ej(powerzone, sofia, "Prensa de piernas",           "Cuádriceps y glúteos en máquina.",                                    GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=prensa+piernas+leg+press+tecnica");
        Ejercicio pz_zancadas      = ej(powerzone, sofia, "Zancadas con mancuernas",     "Zancadas caminando con mancuernas para mayor trabajo de glúteos.",    GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=zancadas+mancuernas+walking+lunges");
        Ejercicio pz_sentGoblet    = ej(powerzone, sofia, "Sentadilla goblet",           "Sentadilla frontal con mancuerna, ideal para principiantes.",         GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=sentadilla+goblet+mancuerna+tecnica");
        Ejercicio pz_extensionCuad = ej(powerzone, diego, "Extensión de cuádriceps",     "Aislamiento de cuádriceps en máquina.",                               GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=extension+cuadriceps+leg+extension+maquina");
        Ejercicio pz_pressMilitar  = ej(powerzone, diego, "Press militar con barra",     "Empuje vertical para hombros con barra libre.",                       GrupoMuscular.HOMBROS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+militar+overhead+press+hombros");
        Ejercicio pz_elevLateral   = ej(powerzone, diego, "Elevaciones laterales",       "Aislamiento del deltoides medial.",                                   GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=elevaciones+laterales+deltoides+hombros");
        Ejercicio pz_facePull      = ej(powerzone, diego, "Face pull",                   "Trabajo de deltoides posterior y manguito en polea alta.",            GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=face+pull+deltoides+posterior+manguito");
        Ejercicio pz_curlManc      = ej(powerzone, sofia, "Curl con mancuernas",         "Curl alterno sentado para mayor concentración.",                      GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+mancuernas+biceps+alterno+sentado");
        Ejercicio pz_curlMartillo  = ej(powerzone, sofia, "Curl martillo",               "Agarre neutro para braquial y braquiorradial.",                       GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+martillo+hammer+curl+biceps");
        Ejercicio pz_fondos        = ej(powerzone, sofia, "Fondos en paralelas",         "Tríceps y pecho inferior con peso corporal.",                         GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=fondos+paralelas+triceps+tecnica");
        Ejercicio pz_extTriPolea   = ej(powerzone, diego, "Extensión de tríceps polea",  "Aislamiento de tríceps con polea alta.",                              GrupoMuscular.TRICEPS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=extension+triceps+polea+alta+aislamiento");
        Ejercicio pz_pressFrances  = ej(powerzone, diego, "Press francés",               "Tríceps con barra EZ tumbado en banco.",                              GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+frances+skull+crusher+triceps");
        Ejercicio pz_plancha       = ej(powerzone, sofia, "Plancha",                     "Core isométrico con columna neutra.",                                 GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=plancha+plank+core+isometrico+tecnica");
        Ejercicio pz_crunch        = ej(powerzone, sofia, "Crunch abdominal",            "Flexión de tronco básica.",                                           GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=crunch+abdominal+tecnica+correcta");
        Ejercicio pz_elevPiernas   = ej(powerzone, diego, "Elevación de piernas",        "Core inferior colgado en barra.",                                     GrupoMuscular.ABDOMEN,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=elevacion+piernas+colgado+barra+abdomen");
        Ejercicio pz_hipThrust     = ej(powerzone, sofia, "Hip thrust",                  "Glúteos con barra en banco.",                                         GrupoMuscular.GLUTEOS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=hip+thrust+gluteos+barra+tecnica");
        Ejercicio pz_patadaPolea   = ej(powerzone, diego, "Patada de glúteo en polea",   "Extensión de cadera en polea baja.",                                  GrupoMuscular.GLUTEOS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=patada+gluteo+polea+baja+cable");
        Ejercicio pz_bicicleta     = ej(powerzone, sofia, "Bicicleta estática",          "Cardio de bajo impacto, spinning o rodillo.",                         GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=bicicleta+estatica+spinning+cardio");
        Ejercicio pz_burpees       = ej(powerzone, diego, "Burpees",                     "Ejercicio de cardio de alta intensidad.",                             GrupoMuscular.CARDIO,   Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=burpees+tecnica+correcta+hiit");
        Ejercicio pz_hiitCinta     = ej(powerzone, diego, "HIIT en cinta",               "Intervalos de alta intensidad en cinta de correr.",                   GrupoMuscular.CARDIO,   Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=hiit+cinta+intervalos+alta+intensidad");
        Ejercicio pz_comba         = ej(powerzone, sofia, "Salto a la comba",            "Cardio de coordinación.",                                             GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=salto+comba+jump+rope+tecnica+cardio");

        ejercicioRepository.saveAll(List.of(
                pz_pressBanca, pz_flexiones, pz_apertPolea, pz_pressIncManc,
                pz_dominadas, pz_remoManc, pz_jalón, pz_pesoMuerto, pz_remoMaquina,
                pz_sentadilla, pz_prensa, pz_zancadas, pz_sentGoblet, pz_extensionCuad,
                pz_pressMilitar, pz_elevLateral, pz_facePull,
                pz_curlManc, pz_curlMartillo,
                pz_fondos, pz_extTriPolea, pz_pressFrances,
                pz_plancha, pz_crunch, pz_elevPiernas,
                pz_hipThrust, pz_patadaPolea,
                pz_bicicleta, pz_burpees, pz_hiitCinta, pz_comba
        ));

        // ─── EJERCICIOS — IRONBODY ────────────────────────────────────────────
        Ejercicio ib_pressBanca    = ej(ironbody, lucia,  "Press de banca",              "El gran empuje horizontal de powerlifting.",                          GrupoMuscular.PECHO,    Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=press+banca+tecnica+correcta");
        Ejercicio ib_pressInclin   = ej(ironbody, lucia,  "Press inclinado con barra",   "Pecho superior con barra inclinada a 30°.",                           GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+inclinado+barra+pecho+superior");
        Ejercicio ib_pressDeclin   = ej(ironbody, lucia,  "Press declinado con barra",   "Pecho inferior en banco declinado.",                                  GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+declinado+barra+pecho+inferior");
        Ejercicio ib_fondosPecho   = ej(ironbody, marcos, "Fondos pecho",                "Fondos con inclinación hacia delante para mayor activación de pecho.", GrupoMuscular.PECHO,    Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=fondos+pecho+inclinado+paralelas");
        Ejercicio ib_pesoMuerto    = ej(ironbody, lucia,  "Peso muerto",                 "El rey de los ejercicios de fuerza — cadena posterior completa.",      GrupoMuscular.ESPALDA,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=peso+muerto+deadlift+tecnica+correcta");
        Ejercicio ib_pesoMuertoSum = ej(ironbody, lucia,  "Peso muerto sumo",            "Variante con piernas abiertas y grip estrecho.",                      GrupoMuscular.ESPALDA,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=peso+muerto+sumo+sumo+deadlift+tecnica");
        Ejercicio ib_dominadas     = ej(ironbody, lucia,  "Dominadas lastradas",         "Dominadas con cinturón de lastre.",                                   GrupoMuscular.ESPALDA,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=dominadas+lastradas+weighted+pull+up");
        Ejercicio ib_remoBarra     = ej(ironbody, lucia,  "Remo con barra",              "Tracción horizontal con barra, espalda baja paralela al suelo.",       GrupoMuscular.ESPALDA,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=remo+con+barra+espalda+tecnica");
        Ejercicio ib_remoPendlay   = ej(ironbody, lucia,  "Remo Pendlay",                "Remo estricto desde el suelo, sin impulso.",                          GrupoMuscular.ESPALDA,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=remo+pendlay+pendlay+row+tecnica");
        Ejercicio ib_jalón         = ej(ironbody, marcos, "Jalón al pecho",              "Polea alta para espalda, útil para progresar hacia dominadas.",       GrupoMuscular.ESPALDA,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=jalon+al+pecho+polea+alta+espalda");
        Ejercicio ib_sentadilla    = ej(ironbody, lucia,  "Sentadilla con barra",        "Sentadilla de powerlifting con barra alta o baja.",                   GrupoMuscular.PIERNAS,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=sentadilla+barra+squat+tecnica+correcta");
        Ejercicio ib_sentFrontal   = ej(ironbody, lucia,  "Sentadilla frontal",          "Barra al frente para mayor activación de cuádriceps.",                GrupoMuscular.PIERNAS,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=sentadilla+frontal+front+squat+tecnica");
        Ejercicio ib_prensa        = ej(ironbody, marcos, "Prensa de piernas",           "Volumen de piernas en máquina.",                                      GrupoMuscular.PIERNAS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=prensa+piernas+leg+press+tecnica");
        Ejercicio ib_zancadasBarra = ej(ironbody, marcos, "Zancadas con barra",          "Zancadas con barra en la espalda para mayor carga.",                  GrupoMuscular.PIERNAS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=zancadas+barra+barbell+lunge+piernas");
        Ejercicio ib_curlFemoral   = ej(ironbody, marcos, "Curl femoral",                "Aislamiento de isquiotibiales en máquina.",                           GrupoMuscular.PIERNAS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+femoral+leg+curl+isquiotibiales");
        Ejercicio ib_pressMilitar  = ej(ironbody, lucia,  "Press militar",               "Empuje vertical estricto para hombros.",                              GrupoMuscular.HOMBROS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+militar+overhead+press+hombros");
        Ejercicio ib_elevLateral   = ej(ironbody, marcos, "Elevaciones laterales",       "Aislamiento de deltoides medial.",                                    GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=elevaciones+laterales+deltoides+hombros");
        Ejercicio ib_facePull      = ej(ironbody, marcos, "Face pull",                   "Hombros posteriores y manguito rotador.",                             GrupoMuscular.HOMBROS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=face+pull+deltoides+posterior+manguito");
        Ejercicio ib_curlBarra     = ej(ironbody, marcos, "Curl con barra",              "Bíceps con barra recta.",                                             GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+biceps+barra+recta+tecnica");
        Ejercicio ib_curlConcentr  = ej(ironbody, marcos, "Curl concentrado",            "Máximo aislamiento del bíceps apoyando el codo en el muslo.",         GrupoMuscular.BICEPS,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=curl+concentrado+concentration+curl+biceps");
        Ejercicio ib_fondos        = ej(ironbody, lucia,  "Fondos en paralelas",         "Tríceps con peso corporal + lastre.",                                 GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=fondos+paralelas+triceps+tecnica");
        Ejercicio ib_pressFrancesEZ= ej(ironbody, lucia,  "Press francés barra EZ",      "Tríceps tumbado con barra EZ.",                                       GrupoMuscular.TRICEPS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=press+frances+barra+ez+skull+crusher");
        Ejercicio ib_plancha       = ej(ironbody, marcos, "Plancha",                     "Core isométrico.",                                                    GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=plancha+plank+core+isometrico+tecnica");
        Ejercicio ib_rueda         = ej(ironbody, marcos, "Rueda abdominal",             "Core extensión avanzada.",                                            GrupoMuscular.ABDOMEN,  Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=rueda+abdominal+ab+wheel+rollout+core");
        Ejercicio ib_crunch        = ej(ironbody, marcos, "Crunch abdominal",            "Flexión de tronco básica.",                                           GrupoMuscular.ABDOMEN,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=crunch+abdominal+tecnica+correcta");
        Ejercicio ib_hipThrust     = ej(ironbody, lucia,  "Hip thrust con barra",        "Glúteos máxima activación con barra en banco.",                       GrupoMuscular.GLUTEOS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=hip+thrust+gluteos+barra+tecnica");
        Ejercicio ib_extCadera     = ej(ironbody, marcos, "Extensión de cadera",         "Glúteo en máquina específica o polea.",                               GrupoMuscular.GLUTEOS,  Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=extension+cadera+gluteos+maquina+polea");
        Ejercicio ib_burpees       = ej(ironbody, marcos, "Burpees",                     "HIIT clásico de alta intensidad.",                                    GrupoMuscular.CARDIO,   Dificultad.AVANZADO,     "https://www.youtube.com/results?search_query=burpees+tecnica+correcta+hiit");
        Ejercicio ib_comba         = ej(ironbody, marcos, "Salto a la comba",            "Cardio de calentamiento y acondicionamiento.",                        GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=salto+comba+jump+rope+tecnica+cardio");
        Ejercicio ib_remoMaquina   = ej(ironbody, marcos, "Remo en máquina",             "Cardio + cadena posterior en máquina de remo.",                       GrupoMuscular.CARDIO,   Dificultad.PRINCIPIANTE, "https://www.youtube.com/results?search_query=remo+maquina+rowing+machine+cardio");
        Ejercicio ib_kettlebell    = ej(ironbody, marcos, "Kettlebell swing",             "Potencia de cadera con pesa rusa.",                                   GrupoMuscular.GLUTEOS,  Dificultad.INTERMEDIO,   "https://www.youtube.com/results?search_query=kettlebell+swing+pesa+rusa+tecnica");

        ejercicioRepository.saveAll(List.of(
                ib_pressBanca, ib_pressInclin, ib_pressDeclin, ib_fondosPecho,
                ib_pesoMuerto, ib_pesoMuertoSum, ib_dominadas, ib_remoBarra, ib_remoPendlay, ib_jalón,
                ib_sentadilla, ib_sentFrontal, ib_prensa, ib_zancadasBarra, ib_curlFemoral,
                ib_pressMilitar, ib_elevLateral, ib_facePull,
                ib_curlBarra, ib_curlConcentr,
                ib_fondos, ib_pressFrancesEZ,
                ib_plancha, ib_rueda, ib_crunch,
                ib_hipThrust, ib_extCadera, ib_kettlebell,
                ib_burpees, ib_comba, ib_remoMaquina
        ));

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
        entrenamientoEjercicioRepository.saveAll(List.of(
                // FitGym — Yoga Flow
                ee(fgYoga, fg_plancha,    1, 3, null, 60,   30, "Posición perfecta de columna"),
                ee(fgYoga, fg_crunch,     2, 3, 15,   null, 30, "Movimiento lento y controlado"),
                ee(fgYoga, fg_flexiones,  3, 3, 10,   null, 45, "Caderas alineadas"),

                // FitGym — CrossFit WOD
                ee(fgCrossfit, fg_burpees,    1, 5, 10, null, 60,  "Máxima intensidad"),
                ee(fgCrossfit, fg_sentadilla, 2, 4, 15, null, 45,  "Profundidad completa"),
                ee(fgCrossfit, fg_pressBanca, 3, 4, 12, null, 60,  null),
                ee(fgCrossfit, fg_dominadas,  4, 3, 8,  null, 90,  "Hasta el fallo si es posible"),
                ee(fgCrossfit, fg_plancha,    5, 3, null,60,  30,  null),

                // FitGym — Personal
                ee(fgPersonal, fg_pressBanca,   1, 4, 8,  null, 90,  "Peso progresivo cada semana"),
                ee(fgPersonal, fg_pesoMuerto,   2, 4, 6,  null, 120, "Técnica prioritaria"),
                ee(fgPersonal, fg_sentadilla,   3, 4, 8,  null, 90,  null),
                ee(fgPersonal, fg_pressMilitar, 4, 3, 10, null, 60,  null),
                ee(fgPersonal, fg_dominadas,    5, 3, null,null,60,  "Asistido si es necesario"),

                // FitGym — Pérdida de peso
                ee(fgPerdida, fg_cinta,     1, 1, null, 1200, 0,  "20 min calentamiento"),
                ee(fgPerdida, fg_burpees,   2, 4, 10,  null, 60,  null),
                ee(fgPerdida, fg_sentadilla,3, 4, 15,  null, 45,  "Peso moderado"),
                ee(fgPerdida, fg_plancha,   4, 3, null, 45,  30,  null),
                ee(fgPerdida, fg_zancadas,  5, 3, 12,  null, 45,  "Cada pierna"),

                // PowerZone — Spinning
                ee(pzSpinning, pz_bicicleta, 1, 1, null, 2700, 0,  "45 min spinning en bicicleta estática"),
                ee(pzSpinning, pz_comba,     2, 3, null, 120,  30, "Intervalos de sprint"),

                // PowerZone — Funcional
                ee(pzFuncional, pz_sentadilla, 1, 4, 12, null, 60, null),
                ee(pzFuncional, pz_pressBanca, 2, 4, 12, null, 60, null),
                ee(pzFuncional, pz_remoManc,   3, 4, 12, null, 60, null),
                ee(pzFuncional, pz_plancha,    4, 3, null,45,  30, null),
                ee(pzFuncional, pz_burpees,    5, 3, 10, null, 60, null),
                ee(pzFuncional, pz_zancadas,   6, 3, 10, null, 45, "Cada pierna"),

                // PowerZone — Power Personal
                ee(pzPower, pz_pesoMuerto,   1, 5, 5,  null, 180, "Fuerza máxima — técnica impecable"),
                ee(pzPower, pz_sentadilla,   2, 5, 5,  null, 180, "Alta intensidad"),
                ee(pzPower, pz_pressBanca,   3, 5, 5,  null, 120, null),
                ee(pzPower, pz_pressMilitar, 4, 4, 6,  null, 90,  null),
                ee(pzPower, pz_dominadas,    5, 3, 8,  null, 90,  null),

                // PowerZone — Definición
                ee(pzDefinic, pz_pressBanca,  1, 4, 12, null, 60, "Peso moderado-alto"),
                ee(pzDefinic, pz_elevLateral, 2, 3, 15, null, 45, "Aislamiento deltoides"),
                ee(pzDefinic, pz_curlManc,    3, 3, 12, null, 45, null),
                ee(pzDefinic, pz_fondos,      4, 3, 15, null, 60, null),
                ee(pzDefinic, pz_crunch,      5, 4, 20, null, 30, null),
                ee(pzDefinic, pz_hipThrust,   6, 4, 15, null, 60, null),

                // IronBody — Powerlifting Grupal
                ee(ibPower, ib_pesoMuerto, 1, 5, 5, null, 180, "Carga máxima del día"),
                ee(ibPower, ib_sentadilla, 2, 5, 5, null, 180, "Sentadilla competición"),
                ee(ibPower, ib_pressBanca, 3, 5, 5, null, 180, "Grip de competición"),

                // IronBody — HIIT
                ee(ibHiit, ib_burpees,    1, 4, 15, null, 30,  "Sin descanso entre series"),
                ee(ibHiit, ib_comba,      2, 3, null,60,  30,  "Velocidad máxima"),
                ee(ibHiit, ib_plancha,    3, 3, null,60,  30,  null),
                ee(ibHiit, ib_sentadilla, 4, 3, 20, null, 45,  "Peso ligero, alta velocidad"),
                ee(ibHiit, ib_rueda,      5, 3, 10, null, 60,  "Core finisher"),

                // IronBody — Competición
                ee(ibCompet, ib_pesoMuerto, 1, 6, 3, null, 180, "Máximo absoluto"),
                ee(ibCompet, ib_sentadilla, 2, 6, 3, null, 180, "Profundidad reglamentaria"),
                ee(ibCompet, ib_pressBanca, 3, 6, 3, null, 180, "Pausa en pecho"),

                // IronBody — Volumen
                ee(ibVolumen, ib_sentadilla,   1, 5, 8,  null, 90,  "Volumen — peso 70%"),
                ee(ibVolumen, ib_pressBanca,   2, 5, 8,  null, 90,  null),
                ee(ibVolumen, ib_remoBarra,    3, 5, 8,  null, 90,  null),
                ee(ibVolumen, ib_pressMilitar, 4, 4, 10, null, 75,  null),
                ee(ibVolumen, ib_pesoMuerto,   5, 3, 6,  null, 120, "Cierre de sesión"),
                ee(ibVolumen, ib_curlFemoral,  6, 3, 12, null, 60,  null),
                ee(ibVolumen, ib_prensa,       7, 3, 15, null, 60,  null)
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
                inscribir(fgS1, c1), inscribir(fgS1, c2), inscribir(fgS1, c3), inscribir(fgS1, c4),
                inscribir(fgS2, c1), inscribir(fgS2, c3), inscribir(fgS2, c5),
                inscribir(fgS3, c2), inscribir(fgS3, c4),
                inscribirAsistido(fgS6, c1, true), inscribirAsistido(fgS6, c2, true),
                inscribirAsistido(fgS6, c3, false), inscribirAsistido(fgS6, c5, true),
                inscribir(fgS4, c1), inscribir(fgS4, c2), inscribir(fgS4, c4),
                inscribir(fgS5, c3), inscribir(fgS5, c5),
                inscribir(fgSI1, c1),
                inscribir(fgSI2, c2),

                inscribir(pzS1, c6), inscribir(pzS1, c7), inscribir(pzS1, c8), inscribir(pzS1, c9), inscribir(pzS1, c10),
                inscribir(pzS2, c6), inscribir(pzS2, c8), inscribir(pzS2, c10),
                inscribir(pzS3, c7), inscribir(pzS3, c9),
                inscribir(pzS4, c6), inscribir(pzS4, c7), inscribir(pzS4, c8),
                inscribir(pzS5, c9), inscribir(pzS5, c10),
                inscribir(pzSI1, c6),
                inscribir(pzSI2, c7),

                inscribir(ibS1, c11), inscribir(ibS1, c12), inscribir(ibS1, c13),
                inscribir(ibS2, c11), inscribir(ibS2, c14),
                inscribirAsistido(ibS5, c11, true), inscribirAsistido(ibS5, c12, true),
                inscribirAsistido(ibS5, c13, true), inscribirAsistido(ibS5, c14, false),
                inscribir(ibS3, c11), inscribir(ibS3, c12), inscribir(ibS3, c13), inscribir(ibS3, c14), inscribir(ibS3, c15),
                inscribir(ibS4, c12), inscribir(ibS4, c15),
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

        Usuario cliente = s.getCliente();
        TipoSuscripcion tipo = s.getTipoSuscripcion();
        Gimnasio gym = tipo.getGimnasio();

        int pctIva = gym.getPorcentajeIva() != null ? gym.getPorcentajeIva() : 0;
        BigDecimal base = importe;
        BigDecimal importeIva = base.multiply(BigDecimal.valueOf(pctIva))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = base.add(importeIva);

        Factura f = new Factura();
        f.setPago(p);
        f.setNumeroFactura(String.format("FAC-2026-%05d", num));
        f.setFechaEmision(p.getFecha());

        f.setClienteNombre(cliente.getNombre() + " " + cliente.getApellidos());
        f.setClienteEmail(cliente.getEmail());
        f.setTipoSuscripcionNombre(tipo.getNombre());

        f.setGimnasioNombre(gym.getNombre());
        f.setGimnasioDireccion(gym.getDireccion());
        f.setGimnasioTelefono(gym.getTelefono());
        f.setGimnasioEmail(gym.getEmail());

        f.setBaseImponible(base);
        f.setPorcentajeIva(pctIva);
        f.setImporteIva(importeIva);
        f.setImporteTotal(total);

        facturaRepository.save(f);

        FacturaLinea linea = new FacturaLinea();
        linea.setFactura(f);
        linea.setDescripcion(tipo.getNombre());
        linea.setPrecioUnitario(importe);
        linea.setCantidad(1);
        f.getLineas().add(linea);

        facturaRepository.save(f);
    }

    private Usuario usuario(String email, String pass, String nombre, String apellidos, String telefono, Rol rol) {
        Usuario u = new Usuario();
        u.setEmail(email); u.setPassword(pass); u.setNombre(nombre);
        u.setApellidos(apellidos); u.setTelefono(telefono); u.setRol(rol);
        return u;
    }

    private Gimnasio gimnasio(String nombre, String dir, String ciudad, String tel, String email, Usuario prop, int iva) {
        Gimnasio g = new Gimnasio();
        g.setNombre(nombre); g.setDireccion(dir); g.setCiudad(ciudad);
        g.setTelefono(tel); g.setEmail(email); g.setPropietario(prop);
        g.setPorcentajeIva(iva);
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
        cse.setClienteSuscripcion(cs); cse.setExtra(e);
        cse.setPrecioContratacion(e.getPrecio());
        return cse;
    }

    private Entrenamiento entrenamiento(Gimnasio g, Usuario entrenador, String nombre, String desc, TipoEntrenamiento tipo) {
        Entrenamiento e = new Entrenamiento();
        e.setGimnasio(g); e.setEntrenador(entrenador); e.setNombre(nombre);
        e.setDescripcion(desc); e.setTipo(tipo); return e;
    }

    private Ejercicio ej(Gimnasio g, Usuario creadoPor, String nombre, String descripcion,
                         GrupoMuscular grupo, Dificultad dificultad, String videoUrl) {
        Ejercicio e = new Ejercicio();
        e.setNombre(nombre); e.setDescripcion(descripcion);
        e.setGrupoMuscular(grupo); e.setDificultad(dificultad);
        e.setGimnasio(g); e.setCreadoPor(creadoPor);
        e.setVideoUrl(videoUrl);
        return e;
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
}
