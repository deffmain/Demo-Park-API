package com.micael.demo_park_api.web.controller;



import com.micael.demo_park_api.domain.ClienteVaga;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoCreateDTO;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoResponseDTO;
import com.micael.demo_park_api.dto.mapStruct.ClienteVagaMapper;

import com.micael.demo_park_api.exception.ErrorMessage;
import com.micael.demo_park_api.service.ClienteVagaService;
import com.micael.demo_park_api.service.EstacionamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@RequestMapping("api/v1/estacionamentos")
@RequiredArgsConstructor
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;
    private final ClienteVagaMapper clienteVagaMapper;
    private final ClienteVagaService clienteVagaService;

    @Operation(summary = "Operação de check-in", description = "Recurso para dar entrada de um veículo no estacionamento. " +
        "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado"),
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Causas possiveis: <br/>" +
                "- CPF do cliente não cadastrado no sistema; <br/>" +
                "- Nenhuma vaga livre foi localizada;",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class)))
        })

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDTO> registrarEstacionamento(@Valid @RequestBody EstacionamentoCreateDTO etcDTO){

        ClienteVaga clienteVaga = estacionamentoService.checkIn(etcDTO);

         URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{recibo}")
            .buildAndExpand(clienteVaga.getReciboCV())
            .toUri();
        return ResponseEntity.created(location).body(clienteVagaMapper.toEstacionamentoResponseDTO(clienteVaga));
    }



    @Operation(summary = "Operação para cunsulta de dados dos registros do estacionamento"
    ,description = "Recurso permite consultar os dados de um registro via recibo enviado na URI."
    ,security = @SecurityRequirement(name = "security"),
        parameters ={@Parameter(in = PATH, name = "recibo", description = "numero do recibo gerado no check-in.")},
        responses = {
            @ApiResponse(responseCode = "200", description = "Consulta retornada com sucesso!",
                content = @Content(mediaType = "application/json;charset=UTF-8",
                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Registro com o código do recibo procurado não foi encontrado.",
                content = @Content(mediaType = "application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/check-in/{reciboCV}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDTO> procurarRegistroPorRecibo(@PathVariable String reciboCV){

        ClienteVaga clienteVaga = clienteVagaService.procurarClienteViaRecibo(reciboCV);

        return ResponseEntity.ok().body(clienteVagaMapper.toEstacionamentoResponseDTO(clienteVaga));
    }


    @PatchMapping("/check-out/{reciboCV}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDTO> checkout(@PathVariable String reciboCV){

        ClienteVaga clienteVaga = estacionamentoService.checkout(reciboCV);

        return ResponseEntity.ok().body(clienteVagaMapper.toEstacionamentoResponseDTO(clienteVaga));
    }



}
