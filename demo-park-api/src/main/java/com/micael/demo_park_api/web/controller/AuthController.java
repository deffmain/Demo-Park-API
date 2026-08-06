package com.micael.demo_park_api.web.controller;


import com.micael.demo_park_api.dto.userDTO.UserLoginDTO;
import com.micael.demo_park_api.exception.ErrorMessage;
import com.micael.demo_park_api.jwt.JwtToken;
import com.micael.demo_park_api.jwt.JwtUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Recurso para proceder com autenticação na API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class AuthController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;



    @Operation(summary = "Autenticar na API", description = "Recurso de autenticação na API",
        responses ={ @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso e retorno de bearer token!",
            content = @Content(mediaType = "application/Json",
                schema = @Schema(implementation = JwtToken.class))),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas",
                content = @Content(mediaType = "application/Json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Campo(s) inválido(s)",
                content = @Content(mediaType = "application/Json",
                    schema = @Schema(implementation = ErrorMessage.class)))}
    )
    @PostMapping("/auth")
    public ResponseEntity<?> authentication(@RequestBody @Valid UserLoginDTO body, HttpServletRequest request){
        log.info("Processo de autenticação pelo login {}", body.username());
        try{
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(body.username(), body.password());
            authenticationManager.authenticate(authenticationToken);
            JwtToken token = detailsService.getTokenAuthenticated(body.username());
            return ResponseEntity.ok(token);
        }catch(AuthenticationException ex){
            log.warn("Bad credentials from username {}", body.username());
        }

        return ResponseEntity
            .badRequest()
            .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credenciaias invalidas"));

    }

}
