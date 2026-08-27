package com.micael.demo_park_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException
        (MethodArgumentNotValidException ex, HttpServletRequest request, BindingResult result){

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_CONTENT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_CONTENT, "Campo(s) inválidos!", result));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> accesDeniedException
        (AccessDeniedException ex, HttpServletRequest request){

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(request, HttpStatus.FORBIDDEN, ex.getMessage()));

    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException ex,
        HttpServletRequest request) {

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(
                request,
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP não suportado para esta rota"
            ));
    }

    @ExceptionHandler({UsernameUniqueViolationException.class,CpfUniqueViolationException.class, CodigoUniqueViolationException.class})
    public ResponseEntity<ErrorMessage> uniqueViolationExceptionPersonalized
        (RuntimeException ex, HttpServletRequest request){

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityNotFoundException
        (EntityNotFoundException ex, HttpServletRequest request){

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));

    }

    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<ErrorMessage> passwordInvalidException
        (RuntimeException ex, HttpServletRequest request){

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_CONTENT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> internalServerErrorException
        (Exception ex, HttpServletRequest request){

        ErrorMessage error = new ErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        log.error("Internal server error {} {} ", error ,ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(error);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpServletRequest request) {

        String message = "Requisição com corpo mal formatado ou valor inválido";

        if (ex.getCause() instanceof InvalidFormatException ife
            && ife.getTargetType().isEnum()) {
            String campo = ife.getPath().isEmpty() ? "campo"
                : ife.getPath().getLast().getPropertyName();
            String validos = Arrays.toString(ife.getTargetType().getEnumConstants());
            message = String.format("Valor inválido para o campo '%s'. Valores aceitos: %s", campo, validos);
        }

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_CONTENT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_CONTENT, message));
    }

}
