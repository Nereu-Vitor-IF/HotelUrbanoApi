package com.api.hotelurbano.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.ObjectMapper;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final int status;
    private final String message;
    private String stackTrace;
    private List<ValidationError> erros;

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }

    public void addValidationError(String field, String message) {
        if (Objects.isNull(erros)) {
            this.erros = new ArrayList<>();
        }
        this.erros.add(new ValidationError(field, message));
    }

    // * Converte o objeto de erro atual em um a String JSON usando o ObjectMapper. 
    // * Usado para enviar respostas de erros manuais em filtros de segurança.
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return "{\"status\": " + this.getStatus() + ", " +
                    "\"message\": \"Erro na conversão dos dados\"}";
        }
    }

}
