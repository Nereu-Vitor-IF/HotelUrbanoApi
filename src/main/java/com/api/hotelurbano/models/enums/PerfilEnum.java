package com.api.hotelurbano.models.enums;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerfilEnum {

    // * O Spring Security exige o prefixo 'ROLE_' para identificar uma autoridade
    ADMIN(1, "ROLE_ADMIN"),
    CLIENTE(2, "ROLE_CLIENTE");

    private Integer cod;
    private String descricao;

    // * Método auxiliar para converter um código inteiro (do banco) para o Enum no Java
    public static PerfilEnum toEnum(Integer cod) {
        if (Objects.isNull(cod)) {
            return null;
        }

        for (PerfilEnum x : PerfilEnum.values()) {
            if (cod.equals(x.getCod())) {
                return x;
            }
        }

        throw new IllegalArgumentException("Código inválido: " + cod);
    }    

}
