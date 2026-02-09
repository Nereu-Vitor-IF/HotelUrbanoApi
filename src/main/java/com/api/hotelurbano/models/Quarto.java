package com.api.hotelurbano.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
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
@Table(name = Quarto.NOME_TABELA)
public class Quarto {
    
    public static final String NOME_TABELA = "quarto"; 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idQuarto", unique = true)
    private Long idQuarto;

    @Column(nullable = false)
    @NotNull
    @NotEmpty
    private String numeroQuarto;

    @Column(nullable = false)
    @NotNull
    @NotEmpty
    private String tipoQuarto;
    
    @Column(nullable = false)
    @NotNull
    private Double precoDiaria;
    
    private Boolean disponivel = true;

}
