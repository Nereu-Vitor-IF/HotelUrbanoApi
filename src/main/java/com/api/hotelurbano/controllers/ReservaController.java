package com.api.hotelurbano.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.hotelurbano.models.Reserva;
import com.api.hotelurbano.services.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reserva")
@Validated
public class ReservaController {
    
    @Autowired
    private ReservaService reservaService;

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        Reserva obj = this.reservaService.buscarReservaPorId(id);

        return ResponseEntity.ok().body(obj);
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> buscarTodas() {
        List<Reserva> list = this.reservaService.buscarTodas();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody Reserva obj) {
        this.reservaService.criar(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(obj.getIdReserva()).toUri();
        return ResponseEntity.created(uri).build();
    }   

    @PatchMapping("/{id}/checkout")
    public ResponseEntity<Void> checkout(@PathVariable Long id) {
        this.reservaService.realizarCkeckout(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
}
