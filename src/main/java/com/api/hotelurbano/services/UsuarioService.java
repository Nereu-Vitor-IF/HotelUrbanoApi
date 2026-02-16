package com.api.hotelurbano.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.hotelurbano.dtos.UsuarioDTO;
import com.api.hotelurbano.models.Usuario;
import com.api.hotelurbano.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(id);
        return usuario.orElseThrow(() -> new RuntimeException(
            "Usuário não encontrado! Id: " + id
        ));
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return this.usuarioRepository.findAll();
    }

    @Transactional
    public Usuario criar(UsuarioDTO dto) {
        Usuario obj = new Usuario();
        obj.setNome(dto.nome());
        obj.setEmail(dto.email());
        obj.setSenha(dto.senha());
        obj.setTelefone(dto.telefone());
        obj.setPerfil(dto.perfil());
        obj = this.usuarioRepository.save(obj);
        return obj;
    }

    @Transactional
    public Usuario atualizar(UsuarioDTO dto, Long id) {
        Usuario obj = this.buscarUsuarioPorId(id);
        obj.setSenha(dto.senha());
        obj.setTelefone(dto.telefone());
        return this.usuarioRepository.save(obj);
    }

    @Transactional
    public void deletar(Long id) {
        buscarUsuarioPorId(id);
        try {
            this.usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível excluir pois há reservas relacionadas!");
        }
    }

}
