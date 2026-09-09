package com.micael.demo_park_api.web.controller;


import com.micael.demo_park_api.domain.Vaga;
import com.micael.demo_park_api.dto.vagaDTO.VagaCreateDTO;
import com.micael.demo_park_api.dto.vagaDTO.VagaResponseDTO;
import com.micael.demo_park_api.dto.mapStruct.VagaMapper;
import com.micael.demo_park_api.exception.ErrorMessage;
import com.micael.demo_park_api.service.VagaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Vagas", description = "Contem todas as operações relativas a leitura, registro e edição de uma vaga.")
@RequestMapping("/api/v1/vagas")
@RequiredArgsConstructor
@RestController
@Validated
public class VagaController {

    private final VagaService vagaService;
    private final VagaMapper vagaMapper;


    //-------------//Start
    @Operation(summary = "Criar uma nova vaga", description = "Recurso para criar uma nova vaga." +
        "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")),
            @ApiResponse(responseCode = "409", description = "Vaga já cadastrada",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = RuntimeException.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> registrarVaga(@Valid @RequestBody VagaCreateDTO vagaCreateDTO){
        Vaga vaga = vagaService.criarVaga(vagaCreateDTO);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{vagaCodigo}")
            .buildAndExpand(vaga.getCodigoVaga())
            .toUri();
        return ResponseEntity.created(location).build();
    }
    //-------------//End



    //-------------//Start
    @Operation(summary = "Localizar uma vaga", description = "Recurso para retornar uma vaga pelo seu código" +
        "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Recurso criado com sucesso",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = VagaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Vaga não localizada",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                content = @Content(mediaType = " application/json;charset=UTF-8",
                    schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = RuntimeException.class)))
    })
    @GetMapping("{vagaCodigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDTO> consultarvaga(@PathVariable
                                                             @Size(min = 4, max = 4, message = "{Size.vagaController.registrarVaga}")
                                                             @Pattern(regexp = "^[A-Z]-\\d{2}$", message = "{Pattern.vagaController.registrarVaga}")
                                                             @NotBlank(message = "{NotBlank.vagaController.registrarVaga}")
                                                             String vagaCodigo){
        Vaga vaga = vagaService.procurarPorCodigo(vagaCodigo);

        return ResponseEntity.status(HttpStatus.OK).body(vagaMapper.vagaToDto(vaga));
    }
    //-------------//End


}
