package com.fuerzapp.service;

import com.fuerzapp.dto.AltaGimnasioRequest;
import com.fuerzapp.dto.GimnasioRequest;
import com.fuerzapp.dto.GimnasioResponse;
import com.fuerzapp.dto.PerfilUsuarioRequest;
import com.fuerzapp.dto.RegistroUsuarioRequest;
import com.fuerzapp.dto.UsuarioResponse;
import com.fuerzapp.entity.ClienteGimnasio;
import com.fuerzapp.entity.EntrenadorGimnasio;
import com.fuerzapp.entity.Gimnasio;
import com.fuerzapp.entity.Usuario;
import com.fuerzapp.enums.Rol;
import com.fuerzapp.repository.ClienteGimnasioRepository;
import com.fuerzapp.repository.ClienteSuscripcionRepository;
import com.fuerzapp.repository.EntrenadorGimnasioRepository;
import com.fuerzapp.repository.GimnasioRepository;
import com.fuerzapp.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GimnasioService — tests unitarios")
class GimnasioServiceTest {

    @Mock private GimnasioRepository gimnasioRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EntrenadorGimnasioRepository entrenadorGimnasioRepository;
    @Mock private ClienteGimnasioRepository clienteGimnasioRepository;
    @Mock private ClienteSuscripcionRepository clienteSuscripcionRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private GimnasioServiceImpl gimnasioService;

    private Gimnasio gimnasio;
    private Usuario propietario;

    @BeforeEach
    void setUp() {
        propietario = new Usuario();
        propietario.setId(1L);
        propietario.setNombre("Carlos");
        propietario.setApellidos("López");
        propietario.setEmail("carlos@gimnasio.com");
        propietario.setRol(Rol.PROPIETARIO);
        propietario.setActivo(true);

        gimnasio = new Gimnasio();
        gimnasio.setId(10L);
        gimnasio.setNombre("FitGym");
        gimnasio.setDireccion("Calle Mayor 1");
        gimnasio.setCiudad("Madrid");
        gimnasio.setTelefono("600000000");
        gimnasio.setEmail("fitgym@email.com");
        gimnasio.setActivo(true);
        gimnasio.setPropietario(propietario);
    }

    // ─── crearGimnasioConPropietario ─────────────────────────────────────────

    @Test
    @DisplayName("crear gimnasio: email duplicado lanza excepción")
    void crearGimnasio_emailPropietarioDuplicado_lanzaExcepcion() {
        RegistroUsuarioRequest datosPropietario = new RegistroUsuarioRequest();
        datosPropietario.setEmail("duplicado@email.com");

        AltaGimnasioRequest request = new AltaGimnasioRequest();
        request.setPropietario(datosPropietario);

        when(usuarioRepository.existsByEmail("duplicado@email.com")).thenReturn(true);

        assertThatThrownBy(() -> gimnasioService.crearGimnasioConPropietario(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con ese email");

        verify(usuarioRepository, never()).save(any());
        verify(gimnasioRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear gimnasio: flujo exitoso crea propietario y gimnasio")
    void crearGimnasio_datosValidos_creaAmbasEntidades() {
        RegistroUsuarioRequest datosPropietario = new RegistroUsuarioRequest();
        datosPropietario.setNombre("Carlos");
        datosPropietario.setApellidos("López");
        datosPropietario.setEmail("carlos@email.com");
        datosPropietario.setPassword("pass123");
        datosPropietario.setTelefono("600000000");

        GimnasioRequest datosGimnasio = new GimnasioRequest();
        datosGimnasio.setNombre("FitGym");
        datosGimnasio.setDireccion("Calle Mayor 1");
        datosGimnasio.setCiudad("Madrid");
        datosGimnasio.setTelefono("910000000");
        datosGimnasio.setEmail("fitgym@email.com");

        AltaGimnasioRequest request = new AltaGimnasioRequest();
        request.setPropietario(datosPropietario);
        request.setGimnasio(datosGimnasio);

        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(propietario);
        when(gimnasioRepository.save(any(Gimnasio.class))).thenReturn(gimnasio);

        GimnasioResponse response = gimnasioService.crearGimnasioConPropietario(request);

        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("FitGym");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(gimnasioRepository).save(any(Gimnasio.class));
    }

    // ─── cambiarEstado ────────────────────────────────────────────────────────

    @Test
    @DisplayName("cambiar estado: desactiva el gimnasio correctamente")
    void cambiarEstado_desactivaGimnasio() {
        when(gimnasioRepository.findById(10L)).thenReturn(Optional.of(gimnasio));
        when(gimnasioRepository.save(gimnasio)).thenReturn(gimnasio);

        gimnasioService.cambiarEstado(10L, false);

        ArgumentCaptor<Gimnasio> captor = ArgumentCaptor.forClass(Gimnasio.class);
        verify(gimnasioRepository).save(captor.capture());
        assertThat(captor.getValue().getActivo()).isFalse();
    }

    @Test
    @DisplayName("cambiar estado: gimnasio inexistente lanza excepción")
    void cambiarEstado_gimnasioNoExiste_lanzaExcepcion() {
        when(gimnasioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gimnasioService.cambiarEstado(99L, false))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Gimnasio no encontrado");
    }

    // ─── darDeAltaEntrenador ──────────────────────────────────────────────────

    @Test
    @DisplayName("alta entrenador: email duplicado lanza excepción")
    void darDeAltaEntrenador_emailDuplicado_lanzaExcepcion() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest();
        request.setEmail("existente@email.com");

        when(gimnasioRepository.findById(10L)).thenReturn(Optional.of(gimnasio));
        when(usuarioRepository.existsByEmail("existente@email.com")).thenReturn(true);

        assertThatThrownBy(() -> gimnasioService.darDeAltaEntrenador(10L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con ese email");

        verify(entrenadorGimnasioRepository, never()).save(any());
    }

    @Test
    @DisplayName("alta entrenador: flujo exitoso crea usuario con rol ENTRENADOR y la relación")
    void darDeAltaEntrenador_datosValidos_creaUsuarioYRelacion() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest();
        request.setNombre("Ana");
        request.setApellidos("García");
        request.setEmail("ana@email.com");
        request.setPassword("pass123");
        request.setTelefono("611111111");

        Usuario entrenador = new Usuario();
        entrenador.setId(2L);
        entrenador.setNombre("Ana");
        entrenador.setApellidos("García");
        entrenador.setEmail("ana@email.com");
        entrenador.setRol(Rol.ENTRENADOR);
        entrenador.setActivo(true);

        when(gimnasioRepository.findById(10L)).thenReturn(Optional.of(gimnasio));
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(entrenador);
        when(entrenadorGimnasioRepository.save(any())).thenReturn(new EntrenadorGimnasio());

        UsuarioResponse response = gimnasioService.darDeAltaEntrenador(10L, request);

        assertThat(response.getEmail()).isEqualTo("ana@email.com");

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue().getRol()).isEqualTo(Rol.ENTRENADOR);
        verify(entrenadorGimnasioRepository).save(any(EntrenadorGimnasio.class));
    }

    // ─── listarEntrenadores ───────────────────────────────────────────────────

    @Test
    @DisplayName("listar entrenadores: devuelve activos e inactivos con activoEnGimnasio correcto")
    void listarEntrenadores_devuelveActivosEInactivos_conActivoEnGimnasioPopulado() {
        Usuario entrenadorActivo = new Usuario();
        entrenadorActivo.setId(2L);
        entrenadorActivo.setNombre("Ana");
        entrenadorActivo.setApellidos("García");
        entrenadorActivo.setEmail("ana@gym.com");
        entrenadorActivo.setActivo(true);
        entrenadorActivo.setRol(Rol.ENTRENADOR);

        Usuario entrenadorInactivo = new Usuario();
        entrenadorInactivo.setId(3L);
        entrenadorInactivo.setNombre("Luis");
        entrenadorInactivo.setApellidos("Pérez");
        entrenadorInactivo.setEmail("luis@gym.com");
        entrenadorInactivo.setActivo(true);
        entrenadorInactivo.setRol(Rol.ENTRENADOR);

        EntrenadorGimnasio relActivo = new EntrenadorGimnasio();
        relActivo.setEntrenador(entrenadorActivo);
        relActivo.setActivo(true);

        EntrenadorGimnasio relInactivo = new EntrenadorGimnasio();
        relInactivo.setEntrenador(entrenadorInactivo);
        relInactivo.setActivo(false);

        when(entrenadorGimnasioRepository.findByGimnasioId(10L))
                .thenReturn(List.of(relActivo, relInactivo));

        List<UsuarioResponse> resultado = gimnasioService.listarEntrenadores(10L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getActivoEnGimnasio()).isTrue();
        assertThat(resultado.get(1).getActivoEnGimnasio()).isFalse();
    }

    // ─── reactivarEntrenador ──────────────────────────────────────────────────

    @Test
    @DisplayName("reactivar entrenador: marca la relación como activa")
    void reactivarEntrenador_relacionInactiva_marcaActivo() {
        EntrenadorGimnasio relacion = new EntrenadorGimnasio();
        relacion.setActivo(false);

        when(entrenadorGimnasioRepository.findByEntrenadorIdAndGimnasioId(2L, 10L))
                .thenReturn(Optional.of(relacion));
        when(entrenadorGimnasioRepository.save(relacion)).thenReturn(relacion);

        gimnasioService.reactivarEntrenador(10L, 2L);

        assertThat(relacion.getActivo()).isTrue();
        verify(entrenadorGimnasioRepository).save(relacion);
    }

    @Test
    @DisplayName("reactivar entrenador: entrenador no pertenece al gimnasio lanza excepción")
    void reactivarEntrenador_noPertenece_lanzaExcepcion() {
        when(entrenadorGimnasioRepository.findByEntrenadorIdAndGimnasioId(5L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> gimnasioService.reactivarEntrenador(10L, 5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El entrenador no pertenece a este gimnasio");

        verify(entrenadorGimnasioRepository, never()).save(any());
    }

    // ─── darDeBajaEntrenador ──────────────────────────────────────────────────

    @Test
    @DisplayName("baja entrenador: entrenador no pertenece al gimnasio lanza excepción")
    void darDeBajaEntrenador_noPertenece_lanzaExcepcion() {
        when(entrenadorGimnasioRepository.findByEntrenadorIdAndGimnasioId(5L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> gimnasioService.darDeBajaEntrenador(10L, 5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El entrenador no pertenece a este gimnasio");
    }

    @Test
    @DisplayName("baja entrenador: marca la relación como inactiva")
    void darDeBajaEntrenador_relacionExistente_marcaInactivo() {
        EntrenadorGimnasio relacion = new EntrenadorGimnasio();
        relacion.setActivo(true);

        when(entrenadorGimnasioRepository.findByEntrenadorIdAndGimnasioId(2L, 10L))
                .thenReturn(Optional.of(relacion));
        when(entrenadorGimnasioRepository.save(relacion)).thenReturn(relacion);

        gimnasioService.darDeBajaEntrenador(10L, 2L);

        assertThat(relacion.getActivo()).isFalse();
        verify(entrenadorGimnasioRepository).save(relacion);
    }

    // ─── listarClientes ───────────────────────────────────────────────────────

    @Test
    @DisplayName("listar clientes: devuelve activos e inactivos con activoEnGimnasio correcto")
    void listarClientes_devuelveActivosEInactivos_conActivoEnGimnasioPopulado() {
        Usuario clienteActivo = new Usuario();
        clienteActivo.setId(4L);
        clienteActivo.setNombre("María");
        clienteActivo.setApellidos("Sanz");
        clienteActivo.setEmail("maria@gym.com");
        clienteActivo.setActivo(true);
        clienteActivo.setRol(Rol.CLIENTE);

        Usuario clienteInactivo = new Usuario();
        clienteInactivo.setId(5L);
        clienteInactivo.setNombre("Pedro");
        clienteInactivo.setApellidos("Ruiz");
        clienteInactivo.setEmail("pedro@gym.com");
        clienteInactivo.setActivo(true);
        clienteInactivo.setRol(Rol.CLIENTE);

        ClienteGimnasio relActivo = new ClienteGimnasio();
        relActivo.setCliente(clienteActivo);
        relActivo.setActivo(true);

        ClienteGimnasio relInactivo = new ClienteGimnasio();
        relInactivo.setCliente(clienteInactivo);
        relInactivo.setActivo(false);

        when(clienteGimnasioRepository.findByGimnasioId(10L))
                .thenReturn(List.of(relActivo, relInactivo));

        List<UsuarioResponse> resultado = gimnasioService.listarClientes(10L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getActivoEnGimnasio()).isTrue();
        assertThat(resultado.get(1).getActivoEnGimnasio()).isFalse();
    }

    // ─── reactivarCliente ─────────────────────────────────────────────────────

    @Test
    @DisplayName("reactivar cliente: marca la relación como activa")
    void reactivarCliente_relacionInactiva_marcaActivo() {
        ClienteGimnasio relacion = new ClienteGimnasio();
        relacion.setActivo(false);

        when(clienteGimnasioRepository.findByClienteIdAndGimnasioId(4L, 10L))
                .thenReturn(Optional.of(relacion));
        when(clienteGimnasioRepository.save(relacion)).thenReturn(relacion);

        gimnasioService.reactivarCliente(10L, 4L);

        assertThat(relacion.getActivo()).isTrue();
        verify(clienteGimnasioRepository).save(relacion);
    }

    @Test
    @DisplayName("reactivar cliente: cliente no pertenece al gimnasio lanza excepción")
    void reactivarCliente_noPertenece_lanzaExcepcion() {
        when(clienteGimnasioRepository.findByClienteIdAndGimnasioId(9L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> gimnasioService.reactivarCliente(10L, 9L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El cliente no pertenece a este gimnasio");

        verify(clienteGimnasioRepository, never()).save(any());
    }

    // ─── actualizarPerfilCliente ──────────────────────────────────────────────

    @Test
    @DisplayName("actualizar perfil cliente: flujo exitoso actualiza datos del usuario")
    void actualizarPerfilCliente_flujoExitoso_actualizaDatos() {
        Usuario cliente = new Usuario();
        cliente.setId(4L);
        cliente.setNombre("María");
        cliente.setApellidos("Sanz");
        cliente.setEmail("maria@gym.com");
        cliente.setTelefono("611111111");
        cliente.setRol(Rol.CLIENTE);
        cliente.setActivo(true);

        ClienteGimnasio relacion = new ClienteGimnasio();
        relacion.setCliente(cliente);
        relacion.setActivo(true);

        PerfilUsuarioRequest request = new PerfilUsuarioRequest();
        request.setNombre("María José");
        request.setApellidos("Sanz Ruiz");
        request.setEmail("maria@gym.com");
        request.setTelefono("622222222");

        when(clienteGimnasioRepository.findByClienteIdAndGimnasioId(4L, 10L))
                .thenReturn(Optional.of(relacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.save(cliente)).thenReturn(cliente);

        UsuarioResponse response = gimnasioService.actualizarPerfilCliente(10L, 4L, request);

        assertThat(response).isNotNull();
        assertThat(cliente.getNombre()).isEqualTo("María José");
        assertThat(cliente.getTelefono()).isEqualTo("622222222");
        verify(usuarioRepository).save(cliente);
    }

    @Test
    @DisplayName("actualizar perfil cliente: email duplicado lanza excepción")
    void actualizarPerfilCliente_emailDuplicado_lanzaExcepcion() {
        Usuario cliente = new Usuario();
        cliente.setId(4L);
        cliente.setEmail("maria@gym.com");

        ClienteGimnasio relacion = new ClienteGimnasio();
        relacion.setCliente(cliente);

        PerfilUsuarioRequest request = new PerfilUsuarioRequest();
        request.setNombre("María");
        request.setApellidos("Sanz");
        request.setEmail("otro@gym.com");

        when(clienteGimnasioRepository.findByClienteIdAndGimnasioId(4L, 10L))
                .thenReturn(Optional.of(relacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.existsByEmail("otro@gym.com")).thenReturn(true);

        assertThatThrownBy(() -> gimnasioService.actualizarPerfilCliente(10L, 4L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El email ya está en uso");

        verify(usuarioRepository, never()).save(any());
    }
}
