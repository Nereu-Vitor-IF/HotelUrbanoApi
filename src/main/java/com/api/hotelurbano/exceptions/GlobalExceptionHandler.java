package com.api.hotelurbano.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.api.hotelurbano.services.exceptions.DataBindingViolationException;
import com.api.hotelurbano.services.exceptions.ObjectNotFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${server.error.include-exception}")
    private boolean printStackTrace;

    // Método responsavél por capturar erros de validação(@NotNull, @Size) de campos enviados no corpo do JSON
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Existem campos inválidos. Por favor, corrija-os e tente novamente");

        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(),
            fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(errorResponse);
    }

    // Método responsavél por capturar qualquer erro inesperado no servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception exception,
            WebRequest request) {

        final String errorMessage = "Ocorreu um erro inesperado no servidor.";
        log.error(errorMessage, exception);

        return buildErrorResponse(
                exception,
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);

    }

    // Método responsável por capturar as violações de integridade do banco (ex: chaves duplicadas ou restrições de tabela)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request) {

        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Falha de integridade no banco: " + errorMessage, dataIntegrityViolationException);

                String messageToUser = "Erro de integridade de dados.";

                if (errorMessage != null && errorMessage.contains("email")) {
                    messageToUser = "Este email já está cadastrado no sistema.";
                } else if (errorMessage != null && errorMessage.contains("telefone")) {
                    messageToUser = "Este telefone já está em uso por outro usuário.";
                }

        return buildErrorResponse(
                dataIntegrityViolationException,
                messageToUser,
                HttpStatus.CONFLICT,
                request);
    }

    // Método responsavél por capturar violações de restrições de validação impostas nas entidades ou nos parâmetros dos serviços 
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request) {
        log.error("Falha ao validar elemento, ", constraintViolationException);

        return buildErrorResponse(
                constraintViolationException,
                HttpStatus.UNPROCESSABLE_CONTENT,
                request);
    }

    // Método responsavél por capturar erros de busca quando um ID não existe no banco
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException objectNotFoundException,
            WebRequest request) {
        log.error("Não foi possível encontrar elemento, ", objectNotFoundException);

        return buildErrorResponse(
                objectNotFoundException,
                HttpStatus.NOT_FOUND,
                request);
    } 

    // Método responsavél por capturar erros de integridade ao tentar excluir registros que possuem dependências (chaves estrangeiras)
    @ExceptionHandler(DataBindingViolationException.class)
    public ResponseEntity<Object> handleDataBindingViolationException(
            DataBindingViolationException dataBindingViolationException,
            WebRequest request) {
        log.error("Conflito de integridade: Não foi possível excluir o recurso pois ele possui dependências, ", dataBindingViolationException);

        return buildErrorResponse(
                dataBindingViolationException,
                HttpStatus.CONFLICT,
                request);
    }
    
    // Sobrecarga de método para facilitar a criação da resposta usando a mensagem padrão da exceção
    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {

        return buildErrorResponse(
                exception,
                exception.getMessage(),
                httpStatus,
                request);
    }

    // Método principal responsavél por construir o objeto ErrorResponse e o encapsular em um ResponseEntity
    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);

        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }            

}
