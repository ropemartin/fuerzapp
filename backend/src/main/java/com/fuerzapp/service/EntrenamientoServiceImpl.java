package com.fuerzapp.service;

import com.fuerzapp.dto.*;
import com.fuerzapp.entity.*;
import com.fuerzapp.enums.TipoEntrenamiento;
import com.fuerzapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntrenamientoServiceImpl implements EntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoEjercicioRepository entrenamientoEjercicioRepository;
    private final SesionRepository sesionRepository;
    private final SesionClienteRepository sesionClienteRepository;
    private final EntrenamientoEspecificoRepository entrenamientoEspecificoRepository;
    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EjercicioRepository ejercicioRepository;

    // ─── Entrenamientos ───────────────────────────────────────────────────────

    @Override
    public List<EntrenamientoResponse> listarPorGimnasioYEntrenador(Long gimnasioId, Long entrenadorId) {
        return entrenamientoRepository.findActivosConSesionesFuturas(gimnasioId, entrenadorId).stream()
                .map(this::toEntrenamientoResponse)
                .toList();
    }

    @Override
    @Transactional
    public void reasignarEntrenador(Long entrenamientoId, Long nuevoEntrenadorId) {
        Entrenamiento entrenamiento = findEntrenamientoById(entrenamientoId);
        Usuario nuevoEntrenador = usuarioRepository.findById(nuevoEntrenadorId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado con id: " + nuevoEntrenadorId));
        entrenamiento.setEntrenador(nuevoEntrenador);
        entrenamientoRepository.save(entrenamiento);
    }

    @Override
    public List<EntrenamientoResponse> listarPorGimnasio(Long gimnasioId, TipoEntrenamiento tipo) {
        List<Entrenamiento> lista = (tipo != null)
                ? entrenamientoRepository.findByGimnasioIdAndTipoAndActivoTrue(gimnasioId, tipo)
                : entrenamientoRepository.findByGimnasioIdAndActivoTrue(gimnasioId);
        return lista.stream().map(this::toEntrenamientoResponse).toList();
    }

    @Override
    public EntrenamientoResponse obtenerPorId(Long id) {
        return toEntrenamientoResponse(findEntrenamientoById(id));
    }

    @Override
    @Transactional
    public EntrenamientoResponse crear(Long gimnasioId, EntrenamientoRequest request) {
        Gimnasio gimnasio = gimnasioRepository.findById(gimnasioId)
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado con id: " + gimnasioId));

        Usuario entrenador = getUsuarioAutenticado();

        Entrenamiento entrenamiento = new Entrenamiento();
        entrenamiento.setGimnasio(gimnasio);
        entrenamiento.setEntrenador(entrenador);
        entrenamiento.setNombre(request.getNombre());
        entrenamiento.setDescripcion(request.getDescripcion());
        entrenamiento.setTipo(request.getTipo());

        return toEntrenamientoResponse(entrenamientoRepository.save(entrenamiento));
    }

    @Override
    @Transactional
    public EntrenamientoResponse actualizar(Long id, EntrenamientoRequest request) {
        Entrenamiento entrenamiento = findEntrenamientoById(id);
        entrenamiento.setNombre(request.getNombre());
        entrenamiento.setDescripcion(request.getDescripcion());
        entrenamiento.setTipo(request.getTipo());
        return toEntrenamientoResponse(entrenamientoRepository.save(entrenamiento));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Entrenamiento entrenamiento = findEntrenamientoById(id);
        entrenamiento.setActivo(false);
        entrenamientoRepository.save(entrenamiento);
    }

    // ─── Ejercicios del entrenamiento ─────────────────────────────────────────

    @Override
    public List<EntrenamientoEjercicioResponse> listarEjercicios(Long entrenamientoId) {
        return entrenamientoEjercicioRepository
                .findByEntrenamientoIdOrderByOrden(entrenamientoId).stream()
                .map(this::toEjercicioResponse)
                .toList();
    }

    @Override
    @Transactional
    public EntrenamientoEjercicioResponse agregarEjercicio(Long entrenamientoId,
                                                            EntrenamientoEjercicioRequest request) {
        Entrenamiento entrenamiento = findEntrenamientoById(entrenamientoId);
        Ejercicio ejercicio = ejercicioRepository.findById(request.getEjercicioId())
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado con id: " + request.getEjercicioId()));

        EntrenamientoEjercicio ee = new EntrenamientoEjercicio();
        ee.setEntrenamiento(entrenamiento);
        ee.setEjercicio(ejercicio);
        ee.setOrden(request.getOrden());
        ee.setSeries(request.getSeries());
        ee.setRepeticiones(request.getRepeticiones());
        ee.setDuracionSegundos(request.getDuracionSegundos());
        ee.setDescansoSegundos(request.getDescansoSegundos());
        ee.setNotas(request.getNotas());

        return toEjercicioResponse(entrenamientoEjercicioRepository.save(ee));
    }

    @Override
    @Transactional
    public EntrenamientoEjercicioResponse actualizarEjercicio(Long entrenamientoId, Long eeId,
                                                               EntrenamientoEjercicioRequest request) {
        EntrenamientoEjercicio ee = entrenamientoEjercicioRepository.findById(eeId)
                .orElseThrow(() -> new RuntimeException("Relación entrenamiento-ejercicio no encontrada"));

        ee.setOrden(request.getOrden());
        ee.setSeries(request.getSeries());
        ee.setRepeticiones(request.getRepeticiones());
        ee.setDuracionSegundos(request.getDuracionSegundos());
        ee.setDescansoSegundos(request.getDescansoSegundos());
        ee.setNotas(request.getNotas());

        return toEjercicioResponse(entrenamientoEjercicioRepository.save(ee));
    }

    @Override
    @Transactional
    public void eliminarEjercicio(Long eeId) {
        entrenamientoEjercicioRepository.deleteById(eeId);
    }

    // ─── Sesiones (GRUPAL e INDIVIDUAL) ──────────────────────────────────────

    @Override
    public List<SesionResponse> listarSesiones(Long entrenamientoId) {
        return sesionRepository.findByEntrenamientoIdOrderByFechaHora(entrenamientoId).stream()
                .map(this::toSesionResponse)
                .toList();
    }

    @Override
    public List<SesionResponse> listarSesionesFuturasPorGimnasio(Long gimnasioId) {
        return sesionRepository
                .findByEntrenamientoGimnasioIdAndFechaHoraAfterOrderByFechaHora(gimnasioId, LocalDateTime.now())
                .stream()
                .map(this::toSesionResponse)
                .toList();
    }

    @Override
    @Transactional
    public SesionResponse crearSesion(Long entrenamientoId, SesionRequest request) {
        Entrenamiento entrenamiento = findEntrenamientoById(entrenamientoId);

        if (entrenamiento.getTipo() == TipoEntrenamiento.ESPECIFICO) {
            throw new RuntimeException("Los entrenamientos de tipo ESPECIFICO no tienen sesiones con horario");
        }

        Sesion sesion = new Sesion();
        sesion.setEntrenamiento(entrenamiento);
        sesion.setFechaHora(request.getFechaHora());
        sesion.setDuracionMinutos(request.getDuracionMinutos());
        sesion.setUbicacion(request.getUbicacion());
        sesion.setCapacidadMaxima(request.getCapacidadMaxima());

        return toSesionResponse(sesionRepository.save(sesion));
    }

    @Override
    @Transactional
    public SesionResponse actualizarSesion(Long sesionId, SesionRequest request) {
        Sesion sesion = findSesionById(sesionId);
        sesion.setFechaHora(request.getFechaHora());
        sesion.setDuracionMinutos(request.getDuracionMinutos());
        sesion.setUbicacion(request.getUbicacion());
        sesion.setCapacidadMaxima(request.getCapacidadMaxima());
        return toSesionResponse(sesionRepository.save(sesion));
    }

    @Override
    @Transactional
    public void eliminarSesion(Long sesionId) {
        sesionRepository.deleteById(sesionId);
    }

    // ─── Inscripción en sesión GRUPAL (cliente) ───────────────────────────────

    @Override
    @Transactional
    public void inscribirseEnSesion(Long sesionId) {
        Sesion sesion = findSesionById(sesionId);
        Usuario cliente = getUsuarioAutenticado();

        if (sesionClienteRepository.existsBySesionIdAndClienteId(sesionId, cliente.getId())) {
            throw new RuntimeException("Ya estás inscrito en esta sesión");
        }

        long inscritos = sesionClienteRepository.countBySesionId(sesionId);
        if (sesion.getCapacidadMaxima() != null && inscritos >= sesion.getCapacidadMaxima()) {
            throw new RuntimeException("La sesión está completa");
        }

        SesionCliente sc = new SesionCliente();
        sc.setSesion(sesion);
        sc.setCliente(cliente);
        sesionClienteRepository.save(sc);
    }

    @Override
    @Transactional
    public void cancelarInscripcion(Long sesionId) {
        Usuario cliente = getUsuarioAutenticado();
        SesionCliente sc = sesionClienteRepository
                .findBySesionIdAndClienteId(sesionId, cliente.getId())
                .orElseThrow(() -> new RuntimeException("No estás inscrito en esta sesión"));
        sesionClienteRepository.delete(sc);
    }

    // ─── Asignación de cliente a sesión INDIVIDUAL (entrenador) ──────────────

    @Override
    @Transactional
    public SesionResponse asignarClienteASesion(Long sesionId, AsignarClienteRequest request) {
        Sesion sesion = findSesionById(sesionId);

        if (sesion.getEntrenamiento().getTipo() != TipoEntrenamiento.INDIVIDUAL) {
            throw new RuntimeException("Solo se puede asignar un cliente a sesiones de tipo INDIVIDUAL");
        }

        if (sesionClienteRepository.countBySesionId(sesionId) > 0) {
            throw new RuntimeException("Esta sesión individual ya tiene un cliente asignado");
        }

        Usuario cliente = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + request.getClienteId()));

        SesionCliente sc = new SesionCliente();
        sc.setSesion(sesion);
        sc.setCliente(cliente);
        sc.setNotas(request.getNotas());
        sesionClienteRepository.save(sc);

        return toSesionResponse(sesion);
    }

    // ─── Entrenamientos ESPECÍFICOS ───────────────────────────────────────────

    @Override
    @Transactional
    public EntrenamientoEspecificoResponse asignarEspecifico(Long entrenamientoId,
                                                              AsignarClienteRequest request) {
        Entrenamiento entrenamiento = findEntrenamientoById(entrenamientoId);

        if (entrenamiento.getTipo() != TipoEntrenamiento.ESPECIFICO) {
            throw new RuntimeException("El entrenamiento debe ser de tipo ESPECIFICO");
        }

        Usuario cliente = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + request.getClienteId()));

        EntrenamientoEspecifico ee = new EntrenamientoEspecifico();
        ee.setEntrenamiento(entrenamiento);
        ee.setCliente(cliente);
        ee.setNotas(request.getNotas());

        return toEspecificoResponse(entrenamientoEspecificoRepository.save(ee));
    }

    @Override
    @Transactional
    public EntrenamientoEspecificoResponse marcarCompletado(Long especificoId, boolean completado) {
        EntrenamientoEspecifico ee = entrenamientoEspecificoRepository.findById(especificoId)
                .orElseThrow(() -> new RuntimeException("Entrenamiento específico no encontrado"));
        ee.setCompletado(completado);
        return toEspecificoResponse(entrenamientoEspecificoRepository.save(ee));
    }

    // ─── Vista del cliente ────────────────────────────────────────────────────

    @Override
    public List<SesionResponse> listarSesionesPorCliente(Long clienteId) {
        return sesionClienteRepository.findByClienteId(clienteId).stream()
                .map(sc -> toSesionResponse(sc.getSesion()))
                .toList();
    }

    @Override
    public List<EntrenamientoEspecificoResponse> listarEspecificosPorCliente(Long clienteId) {
        return entrenamientoEspecificoRepository
                .findByClienteIdOrderByFechaAsignacionDesc(clienteId).stream()
                .map(this::toEspecificoResponse)
                .toList();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Entrenamiento findEntrenamientoById(Long id) {
        return entrenamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenamiento no encontrado con id: " + id));
    }

    private Sesion findSesionById(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + id));
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private EntrenamientoResponse toEntrenamientoResponse(Entrenamiento e) {
        EntrenamientoResponse r = new EntrenamientoResponse();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setDescripcion(e.getDescripcion());
        r.setTipo(e.getTipo());
        r.setFechaCreacion(e.getFechaCreacion());
        r.setActivo(e.getActivo());
        r.setGimnasioId(e.getGimnasio().getId());
        r.setEntrenadorId(e.getEntrenador().getId());
        r.setEntrenadorNombre(e.getEntrenador().getNombre() + " " + e.getEntrenador().getApellidos());
        r.setEjercicios(
                entrenamientoEjercicioRepository.findByEntrenamientoIdOrderByOrden(e.getId()).stream()
                        .map(this::toEjercicioResponse)
                        .toList()
        );
        return r;
    }

    private EntrenamientoEjercicioResponse toEjercicioResponse(EntrenamientoEjercicio ee) {
        EntrenamientoEjercicioResponse r = new EntrenamientoEjercicioResponse();
        r.setId(ee.getId());
        r.setOrden(ee.getOrden());
        r.setEjercicioId(ee.getEjercicio().getId());
        r.setEjercicioNombre(ee.getEjercicio().getNombre());
        r.setEjercicioDescripcion(ee.getEjercicio().getDescripcion());
        r.setGrupoMuscular(ee.getEjercicio().getGrupoMuscular().name());
        r.setDificultad(ee.getEjercicio().getDificultad().name());
        r.setImagenUrl(ee.getEjercicio().getImagenUrl());
        r.setVideoUrl(ee.getEjercicio().getVideoUrl());
        r.setSeries(ee.getSeries());
        r.setRepeticiones(ee.getRepeticiones());
        r.setDuracionSegundos(ee.getDuracionSegundos());
        r.setDescansoSegundos(ee.getDescansoSegundos());
        r.setNotas(ee.getNotas());
        return r;
    }

    private SesionResponse toSesionResponse(Sesion s) {
        SesionResponse r = new SesionResponse();
        r.setId(s.getId());
        r.setEntrenamientoId(s.getEntrenamiento().getId());
        r.setEntrenamientoNombre(s.getEntrenamiento().getNombre());
        r.setTipoEntrenamiento(s.getEntrenamiento().getTipo());
        r.setEntrenadorNombre(
                s.getEntrenamiento().getEntrenador().getNombre() + " " +
                s.getEntrenamiento().getEntrenador().getApellidos()
        );
        r.setFechaHora(s.getFechaHora());
        r.setDuracionMinutos(s.getDuracionMinutos());
        r.setUbicacion(s.getUbicacion());
        r.setCapacidadMaxima(s.getCapacidadMaxima());
        r.setPlazasOcupadas(sesionClienteRepository.countBySesionId(s.getId()));
        r.setEjercicios(
                entrenamientoEjercicioRepository
                        .findByEntrenamientoIdOrderByOrden(s.getEntrenamiento().getId()).stream()
                        .map(this::toEjercicioResponse)
                        .toList()
        );
        r.setClientesInscritos(
                sesionClienteRepository.findBySesionId(s.getId()).stream()
                        .map(sc -> {
                            UsuarioResponse u = new UsuarioResponse();
                            u.setId(sc.getCliente().getId());
                            u.setNombre(sc.getCliente().getNombre());
                            u.setApellidos(sc.getCliente().getApellidos());
                            u.setEmail(sc.getCliente().getEmail());
                            u.setTelefono(sc.getCliente().getTelefono());
                            u.setActivo(sc.getCliente().getActivo());
                            u.setRol(sc.getCliente().getRol());
                            return u;
                        })
                        .toList()
        );
        return r;
    }

    private EntrenamientoEspecificoResponse toEspecificoResponse(EntrenamientoEspecifico ee) {
        EntrenamientoEspecificoResponse r = new EntrenamientoEspecificoResponse();
        r.setId(ee.getId());
        r.setEntrenamientoId(ee.getEntrenamiento().getId());
        r.setEntrenamientoNombre(ee.getEntrenamiento().getNombre());
        r.setEntrenadorNombre(
                ee.getEntrenamiento().getEntrenador().getNombre() + " " +
                ee.getEntrenamiento().getEntrenador().getApellidos()
        );
        r.setClienteId(ee.getCliente().getId());
        r.setClienteNombre(ee.getCliente().getNombre() + " " + ee.getCliente().getApellidos());
        r.setFechaAsignacion(ee.getFechaAsignacion());
        r.setNotas(ee.getNotas());
        r.setCompletado(ee.getCompletado());
        r.setEjercicios(
                entrenamientoEjercicioRepository
                        .findByEntrenamientoIdOrderByOrden(ee.getEntrenamiento().getId()).stream()
                        .map(this::toEjercicioResponse)
                        .toList()
        );
        return r;
    }
}
