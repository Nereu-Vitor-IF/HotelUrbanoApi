package com.api.hotelurbano.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ReservaDTO(
    @NotNull Long idUsuario,
    @NotNull Long idQuarto,
    @NotNull LocalDate dataCheckIn,
    @NotNull LocalDate dataCheckOut,
    Double valorTotal
) {}
