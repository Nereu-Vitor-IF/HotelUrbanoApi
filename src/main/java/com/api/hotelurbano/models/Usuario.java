package com.api.hotelurbano.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.api.hotelurbano.models.enums.PerfilEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
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
    @NotBlank(groups = CriarUsuario.class)
    private String nome;

    @Column(name = "telefone", length = 15, nullable = true)
    private String telefone;
        
    @Column(name = "email", length = 100, nullable = false, unique = true)
    @NotBlank(groups = CriarUsuario.class)
    @Email(groups = CriarUsuario.class)    
    @Size(groups = CriarUsuario.class, min = 2, max = 100)
    private String email;
    
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "senha", length = 60, nullable = false)
    @NotBlank(groups = {CriarUsuario.class, AtualizarUsuario.class})
    @Size(groups = {CriarUsuario.class, AtualizarUsuario.class}, min = 8, max = 60)
    private String senha;

    // * Define uma coleção de perfis que será salva em uma tabela auxiliar    
    @ElementCollection(fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @CollectionTable(name = "perfil_usuario")
    @Column(name = "perfil", nullable = false)
    private Set<Integer> perfis = new HashSet<>();

    // * Converte a coleção de números inteiros do banco para o tipo Enum que o Java e o Spring Security utilizam
    public Set<PerfilEnum> getPerfis() {
        return this.perfis.stream().map(x -> PerfilEnum.toEnum(x)).collect(Collectors.toSet());
    }

    // * Adiciona um novo perfil à coleção, garantindo que apenas o código identificador seja persistido
    public void addPerfil(PerfilEnum perfil) {
        this.perfis.add(perfil.getCod());
    }

    public Usuario() {
        this.addPerfil(PerfilEnum.CLIENTE);
    }
}
