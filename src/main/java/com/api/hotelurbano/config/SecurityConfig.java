package com.api.hotelurbano.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.api.hotelurbano.security.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired 
    private JWTUtil jwtUtil;

    private final String[] PUBLIC_MATCHERS = {
            "/"
    };

    // * Lista URLs que aceitam o método POST sem estar logado
    private final String[] PUBLIC_MATCHERS_POST = {
            "/usuario",
            "/login"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // * Desabilita CSRF e ativa o CORS
        http.cors(cors -> cors.configure(http))
            .csrf(csrf -> csrf.disable());        

        // * Define as regras de autorização de requisições
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST)
            .permitAll() // * Libera o cadastro e o login para qualquer pessoa
            .requestMatchers(PUBLIC_MATCHERS)
            .permitAll() // * Libera outras rotas públicas gerais
            .anyRequest().authenticated()); // * Qualquer outra rota exige login

        // * Define que a aplicação não guadará estado de sessão no servidor
        http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // * Define as configurações de CORS, para garantir que o Front-end acesse o Back-end    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        
        // * Cria um objeto de configuração com os valores padrão de permissão
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();

        // * Define explicitamente quais métodos HTTP o Front-end poderá usar
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));

        // * Aplica essa configuração a todas as rotas da API(/**)
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }    

    // * Este método configura o gerenciador de autenticação do Spring Security.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // * Define o algoritmo de criptografia de senha que será usado em toda a aplicação
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
