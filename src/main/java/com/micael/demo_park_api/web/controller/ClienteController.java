package com.micael.demo_park_api.web.controller;


import com.micael.demo_park_api.domain.Cliente;
import com.micael.demo_park_api.dto.clienteDTO.ClienteCreateDTO;
import com.micael.demo_park_api.dto.clienteDTO.ClientePageAbleDTO;
import com.micael.demo_park_api.dto.clienteDTO.ClienteResponseDTO;
import com.micael.demo_park_api.dto.mapStruct.ClienteMapper;
import com.micael.demo_park_api.exception.ErrorMessage;
import com.micael.demo_park_api.jwt.JwtUserDetails;
import com.micael.demo_park_api.repository.projection.ClienteProjection;
import com.micael.demo_park_api.service.ClienteService;
import com.micael.demo_park_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Clientes", description = "Contem todas as operacoes relativas ao recurso de um cliente")
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final UserService userService;
    private final ClienteMapper clienteMapper;


    @Operation(summary = "Operacao utilizada para criar um cliente para um usuário.",
        description = "Requisição exige um bearer token, apenas um usuário que é CLIENTE pode utilizar o recurso de criação.",
        security = @SecurityRequirement(name  = "security"),
        responses ={
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso!",
                content = @Content(mediaType = "application/Json",
                    schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "O CPF não pode ser cadastrado pois já existe no sistema",
                content = @Content(mediaType = "application/Json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados invalidos",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não é permitido para usuário ADMIN",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))}
    )
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDTO> createCliente(@Valid @RequestBody ClienteCreateDTO cliente,
        @AuthenticationPrincipal JwtUserDetails jwtUserDetails){

        Cliente clienteIn = new Cliente();

        clienteIn.setName(cliente.name());
        clienteIn.setCpf(cliente.cpf());
        clienteIn.setUser(userService.encontrarUserPorId(jwtUserDetails.getId()));

        clienteService.createCliente(clienteIn);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ClienteResponseDTO(clienteIn.getIdCliente(), clienteIn.getName(), clienteIn.getCpf()));
    }


    @Operation(summary = "Operacao utilizada para realizar a leitura de um cliente, utilizando o id como parametro de busca.",
        description = "Requisição exige um bearer token, apenas um usuário que é ADMIN pode utilizar o recurso de leitura.",
        security = @SecurityRequirement(name  = "security"),
        responses ={
            @ApiResponse(responseCode = "200", description = "Cliente localizado com sucesso!",
                content = @Content(mediaType = "application/Json",
                    schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente com o id informado não foi encontrado!",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não é permitido para usuário CLIENTE",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))}
    )
    @GetMapping("/{idCliente}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> procurarClientePorId(@PathVariable Long idCliente){
        Cliente cliente = clienteService.encontrarClientePorId(idCliente);

        return ResponseEntity.status(HttpStatus.OK)
            .body(new ClienteResponseDTO(cliente.getIdCliente(), cliente.getName(), cliente.getCpf()));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientePageAbleDTO> acessarTodosClientes(Pageable pageable){
        Page<ClienteProjection> clientes = clienteService
            .encontrarTodosClientes(pageable);

        return ResponseEntity.ok(clienteMapper.toCliPageAble(clientes));

    }

}
