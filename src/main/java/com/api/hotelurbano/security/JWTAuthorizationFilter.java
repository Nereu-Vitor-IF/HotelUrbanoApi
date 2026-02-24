package com.api.hotelurbano.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// * Este filtro intercepta todas as requisições para verificar se existe um token válido no cabeçalho.
// * Ele é o responsável por garantir que o usuário está autorizado a acessar rotas protegidas.
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private JWTUtil jwtUtil;

    private UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
            UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // * Busca o cabeçalho "Authorization" da requisição.
        String authorizationHeader = request.getHeader("Authorization");

        // * Verifica se o cabeçalho existe e se começa com "Bearer ".
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {

            // * Extrai o token e tenta autenticar o usuário.
            String token = authorizationHeader.substring(7);
            UsernamePasswordAuthenticationToken auth = getAuthentication(token);

            if (Objects.nonNull(auth)) {
                // * Se o token for válido, salva a autenticação no contexto do Spring.
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // * Continua o fluxo do requisição para o próximo próximo filtro.
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        // * Verifica se o token JWT é íntegro e não expirou.
        if (this.jwtUtil.isValidToken(token)) {

            // * Recupera o email do usuário que foi gravado dentrodo token
            String email = this.jwtUtil.getEmail(token);

            // * Busca os detalhes as permissões do usuário no banco de dados.
            UserDetails user = this.userDetailsService.loadUserByUsername(email);

            // * Cria o objeto de autenticação final para o Spring Security.
            UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());
            return authenticatedUser;
        }
        return null;
    }

}
