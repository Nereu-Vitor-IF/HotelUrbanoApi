package com.api.hotelurbano.dtos;

import com.api.hotelurbano.models.enums.PerfilEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UsuarioDTO(
    @NotNull @NotEmpty String nome,
    @NotNull @NotEmpty @Email String email,
    @NotNull @NotEmpty String senha,
    @NotNull PerfilEnum perfil,
    String telefone
) {}
