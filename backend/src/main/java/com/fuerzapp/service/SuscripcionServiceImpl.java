package com.fuerzapp.service;

import com.fuerzapp.dto.*;
import com.fuerzapp.entity.*;
import com.fuerzapp.enums.EstadoSuscripcion;
import com.fuerzapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuscripcionServiceImpl implements SuscripcionService {

    private final TipoSuscripcionRepository tipoSuscripcionRepository;
    private final ExtraRepository extraRepository;
    private final ClienteSuscripcionRepository clienteSuscripcionRepository;
    private final ClienteSuscripcionExtraRepository clienteSuscripcionExtraRepository;
    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRepository usuarioRepository;

    // ─── Tipos de suscripción ─────────────────────────────────────────────────

    @Override
    public List<TipoSuscripcionResponse> listarTipos(Long gimnasioId) {
        return tipoSuscripcionRepository.findByGimnasioIdAndActivoTrue(gimnasioId).stream()
                .map(this::toTipoResponse)
                .toList();
    }

    @Override
    @Transactional
    public TipoSuscripcionResponse crearTipo(Long gimnasioId, TipoSuscripcionRequest request) {
        Gimnasio gimnasio = findGimnasioById(gimnasioId);

        TipoSuscripcion tipo = new TipoSuscripcion();
        tipo.setGimnasio(gimnasio);
        tipo.setNombre(request.getNombre());
        tipo.setDescripcion(request.getDescripcion());
        tipo.setPrecio(request.getPrecio());
        tipo.setDuracionDias(request.getDuracionDias());

        return toTipoResponse(tipoSuscripcionRepository.save(tipo));
    }

    @Override
    @Transactional
    public TipoSuscripcionResponse actualizarTipo(Long tipoId, TipoSuscripcionRequest request) {
        TipoSuscripcion tipo = findTipoById(tipoId);
        tipo.setNombre(request.getNombre());
        tipo.setDescripcion(request.getDescripcion());
        tipo.setPrecio(request.getPrecio());
        tipo.setDuracionDias(request.getDuracionDias());
        return toTipoResponse(tipoSuscripcionRepository.save(tipo));
    }

    @Override
    @Transactional
    public void cambiarEstadoTipo(Long tipoId, boolean activo) {
        TipoSuscripcion tipo = findTipoById(tipoId);
        tipo.setActivo(activo);
        tipoSuscripcionRepository.save(tipo);
    }

    // ─── Extras ──────────────────────────────────────────────────────────────

    @Override
    public List<ExtraResponse> listarExtras(Long gimnasioId) {
        return extraRepository.findByGimnasioIdAndActivoTrue(gimnasioId).stream()
                .map(this::toExtraResponse)
                .toList();
    }

    @Override
    @Transactional
    public ExtraResponse crearExtra(Long gimnasioId, ExtraRequest request) {
        Gimnasio gimnasio = findGimnasioById(gimnasioId);

        Extra extra = new Extra();
        extra.setGimnasio(gimnasio);
        extra.setNombre(request.getNombre());
        extra.setDescripcion(request.getDescripcion());
        extra.setPrecio(request.getPrecio());

        return toExtraResponse(extraRepository.save(extra));
    }

    @Override
    @Transactional
    public ExtraResponse actualizarExtra(Long extraId, ExtraRequest request) {
        Extra extra = findExtraById(extraId);
        extra.setNombre(request.getNombre());
        extra.setDescripcion(request.getDescripcion());
        extra.setPrecio(request.getPrecio());
        return toExtraResponse(extraRepository.save(extra));
    }

    @Override
    @Transactional
    public void cambiarEstadoExtra(Long extraId, boolean activo) {
        Extra extra = findExtraById(extraId);
        extra.setActivo(activo);
        extraRepository.save(extra);
    }

    // ─── Suscripciones de clientes ───────────────────────────────────────────

    @Override
    public List<ClienteSuscripcionResponse> listarSuscripcionesGimnasio(Long gimnasioId) {
        return clienteSuscripcionRepository.findAllByGimnasioId(gimnasioId).stream()
                .map(this::toClienteSuscripcionResponse)
                .toList();
    }

    @Override
    public ClienteSuscripcionResponse obtenerSuscripcionCliente(Long clienteId) {
        ClienteSuscripcion suscripcion = clienteSuscripcionRepository
                .findActivaVigenteByClienteId(clienteId)
                .orElseThrow(() -> new RuntimeException("El cliente no tiene una suscripción activa"));
        return toClienteSuscripcionResponse(suscripcion);
    }

    @Override
    @Transactional
    public ClienteSuscripcionResponse asignarSuscripcion(Long clienteId, ClienteSuscripcionRequest request) {
        Usuario cliente = findUsuarioById(clienteId);
        TipoSuscripcion tipo = findTipoById(request.getTipoSuscripcionId());

        clienteSuscripcionRepository
                .findByClienteIdAndEstado(clienteId, EstadoSuscripcion.ACTIVA)
                .ifPresent(s -> {
                    s.setEstado(EstadoSuscripcion.CANCELADA);
                    clienteSuscripcionRepository.save(s);
                });

        ClienteSuscripcion suscripcion = new ClienteSuscripcion();
        suscripcion.setCliente(cliente);
        suscripcion.setTipoSuscripcion(tipo);
        suscripcion.setFechaInicio(request.getFechaInicio());
        suscripcion.setFechaFin(request.getFechaInicio().plusDays(tipo.getDuracionDias()));
        suscripcion.setEstado(EstadoSuscripcion.PENDIENTE);

        return toClienteSuscripcionResponse(clienteSuscripcionRepository.save(suscripcion));
    }

    @Override
    @Transactional
    public ClienteSuscripcionResponse cambiarTipoSuscripcion(Long clienteId, ClienteSuscripcionRequest request) {
        return asignarSuscripcion(clienteId, request);
    }

    @Override
    @Transactional
    public ClienteSuscripcionResponse agregarExtra(Long clienteId, Long extraId) {
        ClienteSuscripcion suscripcion = clienteSuscripcionRepository
                .findActivaVigenteByClienteId(clienteId)
                .orElseThrow(() -> new RuntimeException("El cliente no tiene una suscripción activa"));

        if (clienteSuscripcionExtraRepository.existsByClienteSuscripcionIdAndExtraId(suscripcion.getId(), extraId)) {
            throw new RuntimeException("El cliente ya tiene este extra contratado");
        }

        Extra extra = findExtraById(extraId);

        ClienteSuscripcionExtra cse = new ClienteSuscripcionExtra();
        cse.setClienteSuscripcion(suscripcion);
        cse.setExtra(extra);
        clienteSuscripcionExtraRepository.save(cse);

        return toClienteSuscripcionResponse(suscripcion);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Gimnasio findGimnasioById(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado con id: " + id));
    }

    private TipoSuscripcion findTipoById(Long id) {
        return tipoSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de suscripción no encontrado con id: " + id));
    }

    private Extra findExtraById(Long id) {
        return extraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Extra no encontrado con id: " + id));
    }

    private Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    private TipoSuscripcionResponse toTipoResponse(TipoSuscripcion t) {
        TipoSuscripcionResponse r = new TipoSuscripcionResponse();
        r.setId(t.getId());
        r.setNombre(t.getNombre());
        r.setDescripcion(t.getDescripcion());
        r.setPrecio(t.getPrecio());
        r.setDuracionDias(t.getDuracionDias());
        r.setActivo(t.getActivo());
        r.setGimnasioId(t.getGimnasio().getId());
        return r;
    }

    private ExtraResponse toExtraResponse(Extra e) {
        ExtraResponse r = new ExtraResponse();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setDescripcion(e.getDescripcion());
        r.setPrecio(e.getPrecio());
        r.setActivo(e.getActivo());
        r.setGimnasioId(e.getGimnasio().getId());
        return r;
    }

    private ClienteSuscripcionResponse toClienteSuscripcionResponse(ClienteSuscripcion cs) {
        ClienteSuscripcionResponse r = new ClienteSuscripcionResponse();
        r.setId(cs.getId());
        r.setClienteId(cs.getCliente().getId());
        r.setClienteNombre(cs.getCliente().getNombre() + " " + cs.getCliente().getApellidos());
        r.setClienteEmail(cs.getCliente().getEmail());
        r.setTipoSuscripcionId(cs.getTipoSuscripcion().getId());
        r.setTipoSuscripcionNombre(cs.getTipoSuscripcion().getNombre());
        r.setPrecio(cs.getTipoSuscripcion().getPrecio());
        r.setFechaInicio(cs.getFechaInicio());
        r.setFechaFin(cs.getFechaFin());
        r.setEstado(cs.getEstado());

        List<ExtraResponse> extras = clienteSuscripcionExtraRepository
                .findByClienteSuscripcionId(cs.getId()).stream()
                .map(cse -> toExtraResponse(cse.getExtra()))
                .toList();
        r.setExtras(extras);

        return r;
    }
}
