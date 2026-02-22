package com.api.hotelurbano.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.hotelurbano.dtos.UsuarioDTO;
import com.api.hotelurbano.models.Usuario;
import com.api.hotelurbano.models.enums.PerfilEnum;
import com.api.hotelurbano.repositories.UsuarioRepository;
import com.api.hotelurbano.services.exceptions.DataBindingViolationException;
import com.api.hotelurbano.services.exceptions.ObjectNotFoundException;

@Service
public class UsuarioService {

    @Autowired
    private BCryptPasswordEncoder codificador;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(id);
        return usuario.orElseThrow(() -> new ObjectNotFoundException(
                "Usuário não encontrado! Id: " + id));
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

        // * Transforma a senha em texto purodo DTO em um hash seguro, antes de enviar
        // * para o banco, garantindo segurança do sistema
        obj.setSenha(this.codificador.encode(dto.senha()));        
        obj.setTelefone(dto.telefone());

        // * Regra de Negócios de Perfis:
        // * Se o DTO trouxer perfis, eles ão adicionados um a um.
        // * Caso contrário, o sistema define automaticamente o perfil "CLIENTE" como padrão 
        if (Objects.nonNull(dto.perfis()) && !dto.perfis().isEmpty()) {
            dto.perfis().stream().forEach(perfil -> obj.addPerfil(perfil));                
        } else {
            obj.addPerfil(PerfilEnum.CLIENTE);
        }

        return this.usuarioRepository.save(obj);
    }

    @Transactional
    public Usuario atualizar(UsuarioDTO dto, Long id) {
        Usuario obj = this.buscarUsuarioPorId(id);
        obj.setSenha(this.codificador.encode(dto.senha()));
        obj.setTelefone(dto.telefone());
        return this.usuarioRepository.save(obj);
    }

    @Transactional
    public void deletar(Long id) {
        buscarUsuarioPorId(id);
        try {
            this.usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há reservas relacionadas!");
        }
    }

}
