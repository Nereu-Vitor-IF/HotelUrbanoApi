package com.api.hotelurbano.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.api.hotelurbano.exceptions.GlobalExceptionHandler;
import com.api.hotelurbano.security.JWTAuthenticationFilter;
import com.api.hotelurbano.security.JWTAuthorizationFilter;
import com.api.hotelurbano.security.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    // * Lista de URLS que qualquer pessoa sem login pode acessar
    private final String[] PUBLIC_MATCHERS_GET = {
            "/quartos/**"
    };

    // * Lista de URLs que aceitam o método POST sem estar logado
    private final String[] PUBLIC_MATCHERS_POST = {
            "/usuarios",
            "/login"
    };

    // * Lista de URLs que só o ADMIN tem acesso
    private final String[] ADMIN_MATCHERS = {
            "/quartos/**",
            "/reservas/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager(
                http.getSharedObject(AuthenticationConfiguration.class));

        JWTAuthenticationFilter jwtFilter = new JWTAuthenticationFilter(authenticationManager, jwtUtil);

        JWTAuthorizationFilter authFilter = new JWTAuthorizationFilter(authenticationManager, jwtUtil,
                userDetailsService);

        jwtFilter.setFilterProcessesUrl("/login");

        http.cors(Customizer.withDefaults());

        // * Desabilita CSRF e ativa o CORS
        http.csrf(csrf -> csrf.disable());

        // * Define as regras de autorização de requisições
        http.authorizeHttpRequests(auth -> auth

                // * Libera a leitura para visitantes
                .requestMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET)
                .permitAll()

                // * Libera o cadastro e o login para visitantes
                .requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST)
                .permitAll()

                // * Tranca total dos quartos (POST, PUT, DELETE), somente admin tem acesso
                .requestMatchers(HttpMethod.POST, ADMIN_MATCHERS)
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, ADMIN_MATCHERS)
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, ADMIN_MATCHERS)
                .hasRole("ADMIN")

                .anyRequest().authenticated()); // * Qualquer outra rota exige login

        // * Define que a aplicação não guadará estado de sessão no servidor
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new GlobalExceptionHandler()));

        http.addFilter(jwtFilter);
        http.addFilter(authFilter);

        return http.build();
    }

    // * Define as configurações de CORS, para garantir que o Front-end acesse o
    // Back-end
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        // * Cria um objeto de configuração com os valores padrão de permissão
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();

        // * Define quais "origens" podem acessar a API
        configuration.setAllowedOrigins(Arrays.asList("*"));
        
        // * Define explicitamente quais métodos HTTP o Front-end poderá usar
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        
        // * Permite que o Front-end envie o Token JWT nos cabeçalhos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        
        // * Expõe o Header "Authorization" para o script JS conseguir ler o Token no Login 
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // * Aplica essa configuração a todas as rotas da API(/**)
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // * Este método configura o gerenciador de autenticação do Spring Security.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // * Define o algoritmo de criptografia de senha que será usado em toda a
    // aplicação
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
