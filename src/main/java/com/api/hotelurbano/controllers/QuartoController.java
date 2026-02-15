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

import com.api.hotelurbano.models.Quarto;
import com.api.hotelurbano.services.QuartoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/quarto")
@Validated
public class QuartoController {

    @Autowired
    private QuartoService quartoService;

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscarPorId(@PathVariable Long id) {
        Quarto obj = this.quartoService.buscarQuartoPorId(id);
        
        return ResponseEntity.ok().body(obj);
    }    

    @GetMapping    
    public ResponseEntity<List<Quarto>> buscarTodos() {
        List<Quarto> list = this.quartoService.buscarTodos();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody Quarto obj) {
        this.quartoService.criar(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(obj.getIdQuarto()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@Valid @RequestBody Quarto obj, @PathVariable Long id) {
        obj.setIdQuarto(id);
        this.quartoService.atualizar(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.quartoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
