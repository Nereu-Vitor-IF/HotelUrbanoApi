package com.api.hotelurbano.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuartoDTO(
    @NotNull @NotEmpty String numeroQuarto,
    @NotNull @NotEmpty String tipoQuarto,
    @NotNull Double precoDiaria,
    Boolean disponivel,
    String descricao,
    String urlImagem
) {}
