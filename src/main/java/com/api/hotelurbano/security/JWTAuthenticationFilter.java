package com.api.hotelurbano.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.hotelurbano.exceptions.GlobalExceptionHandler;
import com.api.hotelurbano.models.Usuario;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

// * Filtro personalizado que intercepta a requisição de login(/login)
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private JWTUtil jwtUtil;

    // * Recebe o AuthenticationManager(para validar credenciais) e a JWTUtil(para gerar o token em caso de sucesso).
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setAuthenticationFailureHandler(new GlobalExceptionHandler());
    }

    // * Lê o JSON de login da requisição, converte para o model Usuario e solicita ao Spring que valide as credenciais no banco. 
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            Usuario userCredentials = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userCredentials.getEmail(), userCredentials.getSenha(), new ArrayList<>());

            Authentication authentication = this.authenticationManager.authenticate(authToken);
            return authentication;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    // * Executado após a validação bem-sucedida. 
    // * Gera o token JWT e o injeta no cabeça Authorization da resposta HTTP.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) throws IOException, ServletException {
        UsuarioSpringSecurity usuarioSpringSecurity = (UsuarioSpringSecurity) authentication.getPrincipal(); 
        String email = usuarioSpringSecurity.getUsername();
        String token = this.jwtUtil.generatedToken(email);
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("access-control-expose-headers", "Authorization");
    }

}
