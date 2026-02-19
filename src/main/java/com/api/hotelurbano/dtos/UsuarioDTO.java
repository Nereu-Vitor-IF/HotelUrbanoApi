package com.api.hotelurbano.dtos;

import com.api.hotelurbano.models.Usuario.AtualizarUsuario;
import com.api.hotelurbano.models.Usuario.CriarUsuario;
import com.api.hotelurbano.models.enums.PerfilEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(
    @NotBlank(groups = CriarUsuario.class)
    String nome,

    @NotBlank(groups = CriarUsuario.class)
    @Email(groups = CriarUsuario.class)    
    @Size(groups = CriarUsuario.class, min = 2, max = 100)
    String email,

    @NotBlank(groups = {CriarUsuario.class, AtualizarUsuario.class})
    @Size(groups = {CriarUsuario.class, AtualizarUsuario.class}, min = 8, max = 60)
    String senha,
    
    @NotNull 
    PerfilEnum perfil,
    
    String telefone
) {}
