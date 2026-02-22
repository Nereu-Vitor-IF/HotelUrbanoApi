package com.api.hotelurbano.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.api.hotelurbano.models.enums.PerfilEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UsuarioSpringSecurity implements UserDetails {

    private Long id;
    private String email;
    private String senha;
    private Collection<? extends GrantedAuthority> autoridades;

    public UsuarioSpringSecurity(Long id, String email, String senha, Set<PerfilEnum> perfis) {
        this.id = id;
        this.email = email;
        this.senha = senha;

        // * Conversão dos Enums(ex: ROLE_ADMIN) para SimpleGrantedAuthority.
        // * O Spring Security exige que as permissões estejam nesse formato para
        // controlar às rotas.
        this.autoridades = perfis.stream()
                .map(perfil -> new SimpleGrantedAuthority(perfil.getDescricao()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return autoridades;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }

    // * Método para verificar se o usuário possuem algum dos perfis
    public boolean hasRole(PerfilEnum perfil) {
        return getAuthorities().contains(new SimpleGrantedAuthority(perfil.getDescricao()));
    }

}
