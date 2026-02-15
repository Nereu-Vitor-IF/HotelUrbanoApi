package com.api.hotelurbano.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Usuario criar(Usuario obj) {
        obj.setIdUsuario(null);
        obj = this.usuarioRepository.save(obj);
        return obj;
    }

    @Transactional
    public Usuario atualizar(Usuario obj) {
        Usuario novoObj = buscarUsuarioPorId(obj.getIdUsuario());
        novoObj.setSenha(obj.getSenha());
        return this.usuarioRepository.save(novoObj);
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
