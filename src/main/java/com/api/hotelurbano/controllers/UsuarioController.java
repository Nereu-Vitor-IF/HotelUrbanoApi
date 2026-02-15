package com.api.hotelurbano.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.hotelurbano.models.Usuario;
import com.api.hotelurbano.models.Usuario.AtualizarUsuario;
import com.api.hotelurbano.models.Usuario.CriarUsuario;
import com.api.hotelurbano.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
@Validated
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario obj = this.usuarioService.buscarUsuarioPorId(id);
        
        return ResponseEntity.ok().body(obj);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> list = this.usuarioService.buscarTodos();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    @Validated(CriarUsuario.class)
    public ResponseEntity<Void> criar(@Valid @RequestBody Usuario obj) {
        this.usuarioService.criar(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(obj.getIdUsuario()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @Validated(AtualizarUsuario.class)
    public ResponseEntity<Void> atualizar(@Valid @RequestBody Usuario obj, @PathVariable Long id) {
        obj.setIdUsuario(id);
        this.usuarioService.atualizar(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
