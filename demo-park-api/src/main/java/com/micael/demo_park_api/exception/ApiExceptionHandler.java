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

    @ExceptionHandler(UsernameUniqueViolationException.class)
    public ResponseEntity<ErrorMessage> usernameUniqueViolationException
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
    public ResponseEntity<ErrorMessage> PasswordInvalidException
        (RuntimeException ex, HttpServletRequest request){

        log.error("Api error - ", ex);
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_CONTENT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage()));

    }

}
