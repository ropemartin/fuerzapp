package com.fuerzapp.service;

import com.fuerzapp.dto.EjercicioRequest;
import com.fuerzapp.dto.EjercicioResponse;
import com.fuerzapp.entity.Ejercicio;
import com.fuerzapp.entity.Gimnasio;
import com.fuerzapp.entity.Usuario;
import com.fuerzapp.enums.GrupoMuscular;
import com.fuerzapp.repository.EjercicioRepository;
import com.fuerzapp.repository.GimnasioRepository;
import com.fuerzapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EjercicioServiceImpl implements EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRepository usuarioRepository;

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Override
    public List<EjercicioResponse> listarPredefinidos() {
        return ejercicioRepository.findByEsPredefinidoTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<EjercicioResponse> listarDisponiblesPorGimnasio(Long gimnasioId, GrupoMuscular grupoMuscular) {
        List<Ejercicio> ejercicios = (grupoMuscular != null)
                ? ejercicioRepository.findDisponiblesByGimnasioIdAndGrupoMuscular(gimnasioId, grupoMuscular)
                : ejercicioRepository.findDisponiblesByGimnasioId(gimnasioId);

        return ejercicios.stream().map(this::toResponse).toList();
    }

    @Override
    public EjercicioResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    // ─── Alta de ejercicio por entrenador ─────────────────────────────────────

    @Override
    @Transactional
    public EjercicioResponse crearEjercicioEnGimnasio(Long gimnasioId, EjercicioRequest request) {
        Gimnasio gimnasio = gimnasioRepository.findById(gimnasioId)
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado con id: " + gimnasioId));

        String emailEntrenador = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario entrenador = usuarioRepository.findByEmail(emailEntrenador)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setNombre(request.getNombre());
        ejercicio.setDescripcion(request.getDescripcion());
        ejercicio.setGrupoMuscular(request.getGrupoMuscular());
        ejercicio.setDificultad(request.getDificultad());
        ejercicio.setVideoUrl(request.getVideoUrl());
        ejercicio.setImagenUrl(request.getImagenUrl());
        ejercicio.setEsPredefinido(false);
        ejercicio.setGimnasio(gimnasio);
        ejercicio.setCreadoPor(entrenador);

        return toResponse(ejercicioRepository.save(ejercicio));
    }

    // ─── Edición y borrado ────────────────────────────────────────────────────

    @Override
    @Transactional
    public EjercicioResponse actualizar(Long id, EjercicioRequest request) {
        Ejercicio ejercicio = findById(id);

        ejercicio.setNombre(request.getNombre());
        ejercicio.setDescripcion(request.getDescripcion());
        ejercicio.setGrupoMuscular(request.getGrupoMuscular());
        ejercicio.setDificultad(request.getDificultad());
        ejercicio.setVideoUrl(request.getVideoUrl());
        ejercicio.setImagenUrl(request.getImagenUrl());

        return toResponse(ejercicioRepository.save(ejercicio));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Ejercicio ejercicio = findById(id);
        if (ejercicio.getEsPredefinido()) {
            throw new RuntimeException("No se pueden eliminar ejercicios de la biblioteca global");
        }
        ejercicioRepository.delete(ejercicio);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Ejercicio findById(Long id) {
        return ejercicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado con id: " + id));
    }

    private EjercicioResponse toResponse(Ejercicio e) {
        EjercicioResponse r = new EjercicioResponse();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setDescripcion(e.getDescripcion());
        r.setGrupoMuscular(e.getGrupoMuscular());
        r.setDificultad(e.getDificultad());
        r.setVideoUrl(e.getVideoUrl());
        r.setImagenUrl(e.getImagenUrl());
        r.setEsPredefinido(e.getEsPredefinido());
        r.setGimnasioId(e.getGimnasio() != null ? e.getGimnasio().getId() : null);
        r.setCreadoPorNombre(e.getCreadoPor() != null
                ? e.getCreadoPor().getNombre() + " " + e.getCreadoPor().getApellidos()
                : "Fuerzapp");
        return r;
    }
}
