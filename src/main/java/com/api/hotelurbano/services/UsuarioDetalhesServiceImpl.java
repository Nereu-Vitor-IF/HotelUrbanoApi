package com.api.hotelurbano.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api.hotelurbano.models.Usuario;
import com.api.hotelurbano.repositories.UsuarioRepository;
import com.api.hotelurbano.security.UsuarioSpringSecurity;

@Service
public class UsuarioDetalhesServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // * Buscamos o usuário no banco usando o email como identificador único.
        // * Usamos o Optional para tratar de forma elegante caso o email não exista.
        Usuario usuario = this.usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com o email: " + email));

        // * Retornamos a nossa classe 'adaptadora'
        // * Ela converte os dados da Entity para o formato que o Spring Security entende.                
        return new UsuarioSpringSecurity(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getPerfis()
        );
    }

}
