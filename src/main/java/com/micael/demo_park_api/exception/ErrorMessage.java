package com.micael.demo_park_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
public class ErrorMessage {

    private String path;
    private String method;
    private int statusCode;
    private String statusText;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> erros;

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message){
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.statusCode = status.value();
        this.statusText= status.getReasonPhrase();
        this.message = message;
    }

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult result){
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.statusCode = status.value();
        this.statusText= status.getReasonPhrase();
        this.message = message;
        addErros(result);
    }

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult result, MessageSource messageSource) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.statusCode = status.value();
        this.statusText= status.getReasonPhrase();
        this.message = message;
        addErros(result, messageSource, request.getLocale());
    }

    private void addErros(BindingResult result, MessageSource messageSource, Locale locale) {
        this.erros = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            String code = fieldError.getCodes()[0];
            String message = messageSource.getMessage(code, fieldError.getArguments(),locale);
            this.erros.put(fieldError.getField(),message);

        }
    }

    private void addErros(BindingResult result) {
        this.erros = new HashMap<>();
        for(FieldError fieldError : result.getFieldErrors()){
            this.erros.put(fieldError.getField(), fieldError.getDefaultMessage());

        }
    }


}
