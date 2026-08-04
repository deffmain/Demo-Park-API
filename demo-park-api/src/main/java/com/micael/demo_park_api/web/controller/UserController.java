package com.micael.demo_park_api.web.controller;


import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.dto.PasswordDTO;
import com.micael.demo_park_api.dto.UserRegisterDTO;
import com.micael.demo_park_api.dto.UserResponseDTO;
import com.micael.demo_park_api.exception.ErrorMessage;
import com.micael.demo_park_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Tag(name = "Usuarios", description = "Contem todas as operacoes relativas a leitura, registro e edicao de um usuario.")
@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

        @Operation(summary = "Operacao para o registro de usuarios.",
            description = "Acesso não exige um bearer token",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso",
                        content = @Content(mediaType = "application/Json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Usuario ja cadastrado no sistema",
                        content = @Content(mediaType = "application/Json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso nao processado por campo(s) invalido(s).",
                        content = @Content(mediaType = "application/Json",
                            schema = @Schema(implementation = ErrorMessage.class)))}
            )
    @PostMapping
    public ResponseEntity<UserResponseDTO> registrarUsuario(@Valid @RequestBody UserRegisterDTO userBody) {

        User user = userService.register(userBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user.getIdUser(), user.getUsername(), user.getRole().getDisplayName()));
    }


    @Operation(summary = "Operacao para realizar a leitura de um usuario, utilizando id como parametro de busca.",
           description = "Requisição exige um bearer token, acesso restrito a ADMIN/CLIENTE",
           security = @SecurityRequirement(name  = "security"),
           responses = {
                   @ApiResponse(responseCode = "200", description = "Usuario encontrado com sucesso",
                       content = @Content(mediaType = "application/Json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                   @ApiResponse(responseCode = "403", description = "Usuario sem permissão para acessar o recurso",
                       content = @Content(mediaType = "application/Json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Usuario com o id informado não encontrado",
                       content = @Content(mediaType = "application/Json",
                           schema = @Schema(implementation = ErrorMessage.class)))}
    )
    @GetMapping("/{idUser}")
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENTE') AND #idUser == authentication.principal.id)")
    public ResponseEntity<UserResponseDTO> procurarPorId(@PathVariable Long idUser){

        User user = this.userService.encontrarPorId(idUser);
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(user.getIdUser(), user.getUsername(), user.getRole().getDisplayName()));
    }

    @Operation(summary = "Operacao para realizar a edicao da senha de acesso de um usuario.",
        description = "Requisição exige um bearer token, acesso restrito a ADMIN/CLIENTE",
        security = @SecurityRequirement(name  = "security"),
        responses ={
                   @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso!"),
                   @ApiResponse(responseCode = "422", description = "A nova senha não pode ser igual a senha usada anteriormente!",
                       content = @Content(mediaType = "application/Json",
                           schema = @Schema(implementation = ErrorMessage.class))),
                   @ApiResponse(responseCode = "422", description = "Os campos para inserir a nova senha e confimá-la não são iguais.",
                       content = @Content(mediaType = "application/Json",
                           schema = @Schema(implementation = ErrorMessage.class))),
                   @ApiResponse(responseCode = "422", description = "O campo de última senha utilizada não está de acordo com o que é utilizado.",
                       content = @Content(mediaType = "application/Json",
                           schema = @Schema(implementation = ErrorMessage.class))),
                   @ApiResponse(responseCode = "403", description = "Usuario sem permissão para acessar o recurso",
                       content = @Content(mediaType = "application/Json",
                           schema = @Schema(implementation =  ErrorMessage.class)))
    }
    )
    @PatchMapping("/{idUser}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE') AND #idUser == authentication.principal.id")
    public ResponseEntity<Void> atualizarSenha(@PathVariable Long idUser,@Valid @RequestBody PasswordDTO newPassword){

        userService.alterarSenha(idUser, newPassword);
        return ResponseEntity.noContent().build();
    }

    //-----
    @Operation(summary = "Operacao utilizada para realizar a leitura de todos os usuarios.",
        description = "Requisição exige um bearer token, acesso restrito a ADMIN",
        security = @SecurityRequirement(name  = "security"),
        responses ={
                   @ApiResponse(responseCode = "200", description = "Usuarios econtrados retornados",
                       content = @Content(mediaType = "application/Json",
                           schema = @Schema(implementation = UserResponseDTO.class))),
                   @ApiResponse(responseCode = "403", description = "Usuario sem permissão para acessar o recurso",
                       content = @Content(mediaType = "application/Json",
                           schema = @Schema(implementation = UserResponseDTO.class))),
                   @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                       content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = RuntimeException.class)))}
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> acessarTodos(){

        List<UserResponseDTO> users =
            userService
                .listarTodosUsurarios()
                .stream()
                .map(user -> new UserResponseDTO(user.getIdUser(), user.getUsername(), user.getRole().getDisplayName()))
                .toList();

        return ResponseEntity.ok(users);
    }
    //----//

}
