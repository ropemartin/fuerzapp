package com.fuerzapp.service;

import com.fuerzapp.config.CustomUserDetailsService;
import com.fuerzapp.config.JwtService;
import com.fuerzapp.dto.LoginRequest;
import com.fuerzapp.dto.LoginResponse;
import com.fuerzapp.entity.Usuario;
import com.fuerzapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(usuario.getId(), token, usuario.getEmail(), usuario.getNombre(), usuario.getRol());
    }
}
