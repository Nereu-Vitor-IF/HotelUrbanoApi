package com.api.hotelurbano.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = Reserva.NOME_TABELA)
public class Reserva {
    public static final String NOME_TABELA = "reserva";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReserva", unique = true)
    private Long idReserva;
        
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idQuarto", nullable = false)
    @NotNull
    private Quarto quarto;

    @Column(name = "dataEntrada", nullable = false)
    @NotNull
    private LocalDate dataEntrada;

    @Column(name = "dataSaida", nullable = false)
    @NotNull
    private LocalDate dataSaida; 

    private Double valorTotal;
}
