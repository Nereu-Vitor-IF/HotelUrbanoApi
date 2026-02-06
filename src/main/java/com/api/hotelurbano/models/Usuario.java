package com.api.hotelurbano.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = Usuario.NOME_TABELA)
public class Usuario {

    public static final String NOME_TABELA = "usuario";
    
    public interface CriarUsuario {}
    public interface AtualizarUsuario {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario", unique = true)
    private Long idUsuario;

    @Column(name = "nome", length = 100, nullable = false)
    @NotNull
    @NotEmpty
    private String nome;

    @Column(name = "telefone", length = 15)
    private String telefone;
        
    @Column(name = "email", length = 100, nullable = false, unique = true)
    @NotNull(groups = CriarUsuario.class)
    @NotEmpty(groups = CriarUsuario.class)
    @Size(groups = CriarUsuario.class, min = 2, max = 100)
    private String email;
    
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "senha", length = 60, nullable = false)
    @NotNull(groups = {CriarUsuario.class, AtualizarUsuario.class})
    @NotEmpty(groups = {CriarUsuario.class, AtualizarUsuario.class})
    @Size(groups = {CriarUsuario.class, AtualizarUsuario.class}, min = 8, max = 60)
    private String senha;

}
