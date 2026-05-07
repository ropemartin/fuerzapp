package com.fuerzapp.service;

import com.fuerzapp.dto.*;
import com.fuerzapp.entity.*;
import com.fuerzapp.enums.Rol;
import com.fuerzapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GimnasioServiceImpl implements GimnasioService {

    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntrenadorGimnasioRepository entrenadorGimnasioRepository;
    private final ClienteGimnasioRepository clienteGimnasioRepository;
    private final ClienteSuscripcionRepository clienteSuscripcionRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── ADMIN: gestión de gimnasios ─────────────────────────────────────────

    @Override
    @Transactional
    public GimnasioResponse crearGimnasioConPropietario(AltaGimnasioRequest request) {
        if (usuarioRepository.existsByEmail(request.getPropietario().getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        Usuario propietario = new Usuario();
        propietario.setNombre(request.getPropietario().getNombre());
        propietario.setApellidos(request.getPropietario().getApellidos());
        propietario.setEmail(request.getPropietario().getEmail());
        propietario.setPassword(passwordEncoder.encode(request.getPropietario().getPassword()));
        propietario.setTelefono(request.getPropietario().getTelefono());
        propietario.setRol(Rol.PROPIETARIO);
        usuarioRepository.save(propietario);

        Gimnasio gimnasio = new Gimnasio();
        mapearGimnasio(request.getGimnasio(), gimnasio);
        gimnasio.setPropietario(propietario);
        gimnasioRepository.save(gimnasio);

        return toResponse(gimnasio);
    }

    @Override
    public List<GimnasioResponse> listarTodos() {
        return gimnasioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public GimnasioResponse obtenerPorId(Long id) {
        return toResponse(findGimnasioById(id));
    }

    @Override
    @Transactional
    public GimnasioResponse actualizarGimnasio(Long id, GimnasioRequest request) {
        Gimnasio gimnasio = findGimnasioById(id);
        mapearGimnasio(request, gimnasio);
        if (request.getPropietarioId() != null) {
            Usuario propietario = usuarioRepository.findById(request.getPropietarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + request.getPropietarioId()));
            gimnasio.setPropietario(propietario);
        }
        return toResponse(gimnasioRepository.save(gimnasio));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        Gimnasio gimnasio = findGimnasioById(id);
        gimnasio.setActivo(activo);
        gimnasioRepository.save(gimnasio);
    }

    @Override
    @Transactional
    public GimnasioResponse actualizarDatosPropietario(Long id, GimnasioRequest request) {
        Gimnasio gimnasio = findGimnasioById(id);
        mapearGimnasio(request, gimnasio);
        return toResponse(gimnasioRepository.save(gimnasio));
    }

    // ─── PROPIETARIO: gimnasios propios ──────────────────────────────────────

    @Override
    public List<GimnasioResponse> listarPorPropietario(Long propietarioId) {
        return gimnasioRepository.findByPropietarioId(propietarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── ENTRENADOR: gimnasios donde trabaja ─────────────────────────────────

    @Override
    public List<GimnasioResponse> listarPorEntrenador(Long entrenadorId) {
        return entrenadorGimnasioRepository.findByEntrenadorIdActivo(entrenadorId).stream()
                .map(rel -> toResponse(rel.getGimnasio()))
                .toList();
    }

    // ─── CLIENTE: gimnasio al que pertenece ──────────────────────────────────

    @Override
    public List<GimnasioResponse> listarPorCliente(Long clienteId) {
        return clienteGimnasioRepository.findByClienteIdAndActivoTrue(clienteId).stream()
                .map(rel -> toResponse(rel.getGimnasio()))
                .toList();
    }

    // ─── ENTRENADORES ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public UsuarioResponse darDeAltaEntrenador(Long gimnasioId, RegistroUsuarioRequest request) {
        Gimnasio gimnasio = findGimnasioById(gimnasioId);

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        Usuario entrenador = new Usuario();
        entrenador.setNombre(request.getNombre());
        entrenador.setApellidos(request.getApellidos());
        entrenador.setEmail(request.getEmail());
        entrenador.setPassword(passwordEncoder.encode(request.getPassword()));
        entrenador.setTelefono(request.getTelefono());
        entrenador.setRol(Rol.ENTRENADOR);
        usuarioRepository.save(entrenador);

        EntrenadorGimnasio relacion = new EntrenadorGimnasio();
        relacion.setEntrenador(entrenador);
        relacion.setGimnasio(gimnasio);
        entrenadorGimnasioRepository.save(relacion);

        return toUsuarioResponse(entrenador);
    }

    @Override
    public List<UsuarioResponse> listarEntrenadores(Long gimnasioId) {
        return entrenadorGimnasioRepository.findByGimnasioId(gimnasioId).stream()
                .map(rel -> toUsuarioResponse(rel.getEntrenador(), rel.getActivo()))
                .toList();
    }

    @Override
    @Transactional
    public void darDeBajaEntrenador(Long gimnasioId, Long entrenadorId) {
        EntrenadorGimnasio relacion = entrenadorGimnasioRepository
                .findByEntrenadorIdAndGimnasioId(entrenadorId, gimnasioId)
                .orElseThrow(() -> new RuntimeException("El entrenador no pertenece a este gimnasio"));
        relacion.setActivo(false);
        entrenadorGimnasioRepository.save(relacion);
    }

    @Override
    @Transactional
    public void reactivarEntrenador(Long gimnasioId, Long entrenadorId) {
        EntrenadorGimnasio relacion = entrenadorGimnasioRepository
                .findByEntrenadorIdAndGimnasioId(entrenadorId, gimnasioId)
                .orElseThrow(() -> new RuntimeException("El entrenador no pertenece a este gimnasio"));
        relacion.setActivo(true);
        entrenadorGimnasioRepository.save(relacion);
    }

    // ─── CLIENTES ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public UsuarioResponse darDeAltaCliente(Long gimnasioId, RegistroUsuarioRequest request) {
        Gimnasio gimnasio = findGimnasioById(gimnasioId);

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        Usuario cliente = new Usuario();
        cliente.setNombre(request.getNombre());
        cliente.setApellidos(request.getApellidos());
        cliente.setEmail(request.getEmail());
        cliente.setPassword(passwordEncoder.encode(request.getPassword()));
        cliente.setTelefono(request.getTelefono());
        cliente.setRol(Rol.CLIENTE);
        usuarioRepository.save(cliente);

        ClienteGimnasio relacion = new ClienteGimnasio();
        relacion.setCliente(cliente);
        relacion.setGimnasio(gimnasio);
        clienteGimnasioRepository.save(relacion);

        return toUsuarioResponse(cliente);
    }

    @Override
    public List<UsuarioResponse> listarClientes(Long gimnasioId) {
        return clienteGimnasioRepository.findByGimnasioId(gimnasioId).stream()
                .map(rel -> toUsuarioResponse(rel.getCliente(), rel.getActivo()))
                .toList();
    }

    @Override
    @Transactional
    public void darDeBajaCliente(Long gimnasioId, Long clienteId) {
        ClienteGimnasio relacion = clienteGimnasioRepository
                .findByClienteIdAndGimnasioId(clienteId, gimnasioId)
                .orElseThrow(() -> new RuntimeException("El cliente no pertenece a este gimnasio"));
        relacion.setActivo(false);
        clienteGimnasioRepository.save(relacion);
    }

    @Override
    @Transactional
    public void reactivarCliente(Long gimnasioId, Long clienteId) {
        ClienteGimnasio relacion = clienteGimnasioRepository
                .findByClienteIdAndGimnasioId(clienteId, gimnasioId)
                .orElseThrow(() -> new RuntimeException("El cliente no pertenece a este gimnasio"));
        relacion.setActivo(true);
        clienteGimnasioRepository.save(relacion);
    }

    @Override
    public Optional<SuscripcionActivaInfoResponse> obtenerSuscripcionActivaCliente(Long gimnasioId, Long clienteId) {
        return clienteSuscripcionRepository.findActivaByClienteIdAndGimnasioId(clienteId, gimnasioId)
                .map(cs -> {
                    SuscripcionActivaInfoResponse r = new SuscripcionActivaInfoResponse();
                    r.setTipoNombre(cs.getTipoSuscripcion().getNombre());
                    r.setFechaFin(cs.getFechaFin());
                    return r;
                });
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarPerfilEntrenador(Long gimnasioId, Long entrenadorId, PerfilUsuarioRequest request) {
        entrenadorGimnasioRepository.findByEntrenadorIdAndGimnasioId(entrenadorId, gimnasioId)
                .orElseThrow(() -> new RuntimeException("El entrenador no pertenece a este gimnasio"));
        Usuario u = usuarioRepository.findById(entrenadorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        actualizarDatosUsuario(u, request);
        return toUsuarioResponse(usuarioRepository.save(u));
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarPerfilCliente(Long gimnasioId, Long clienteId, PerfilUsuarioRequest request) {
        clienteGimnasioRepository.findByClienteIdAndGimnasioId(clienteId, gimnasioId)
                .orElseThrow(() -> new RuntimeException("El cliente no pertenece a este gimnasio"));
        Usuario u = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        actualizarDatosUsuario(u, request);
        return toUsuarioResponse(usuarioRepository.save(u));
    }

    private void actualizarDatosUsuario(Usuario u, PerfilUsuarioRequest request) {
        if (!request.getEmail().equalsIgnoreCase(u.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso por otro usuario");
        }
        u.setNombre(request.getNombre());
        u.setApellidos(request.getApellidos());
        u.setEmail(request.getEmail());
        u.setTelefono(request.getTelefono());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Gimnasio findGimnasioById(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado con id: " + id));
    }

    private void mapearGimnasio(GimnasioRequest request, Gimnasio gimnasio) {
        gimnasio.setNombre(request.getNombre());
        gimnasio.setDireccion(request.getDireccion());
        gimnasio.setCiudad(request.getCiudad());
        gimnasio.setTelefono(request.getTelefono());
        gimnasio.setEmail(request.getEmail());
        gimnasio.setLogoUrl(request.getLogoUrl());
        if (request.getPorcentajeIva() != null) {
            gimnasio.setPorcentajeIva(request.getPorcentajeIva());
        }
    }

    private GimnasioResponse toResponse(Gimnasio g) {
        GimnasioResponse response = new GimnasioResponse();
        response.setId(g.getId());
        response.setNombre(g.getNombre());
        response.setDireccion(g.getDireccion());
        response.setCiudad(g.getCiudad());
        response.setTelefono(g.getTelefono());
        response.setEmail(g.getEmail());
        response.setLogoUrl(g.getLogoUrl());
        response.setPorcentajeIva(g.getPorcentajeIva());
        response.setFechaAlta(g.getFechaAlta());
        response.setActivo(g.getActivo());
        response.setPropietarioId(g.getPropietario().getId());
        response.setPropietarioNombre(g.getPropietario().getNombre() + " " + g.getPropietario().getApellidos());
        response.setPropietarioEmail(g.getPropietario().getEmail());
        return response;
    }

    private UsuarioResponse toUsuarioResponse(Usuario u) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(u.getId());
        response.setNombre(u.getNombre());
        response.setApellidos(u.getApellidos());
        response.setEmail(u.getEmail());
        response.setTelefono(u.getTelefono());
        response.setActivo(u.getActivo());
        response.setFechaRegistro(u.getFechaRegistro());
        response.setRol(u.getRol());
        return response;
    }

    private UsuarioResponse toUsuarioResponse(Usuario u, Boolean activoEnGimnasio) {
        UsuarioResponse response = toUsuarioResponse(u);
        response.setActivoEnGimnasio(activoEnGimnasio);
        return response;
    }
}
