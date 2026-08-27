package com.micael.demo_park_api.dto.vagaDTO;

import com.micael.demo_park_api.domain.Vaga;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VagaCreateDTO(
    @NotBlank(message = "{NotBlank.VagaCreateDTO.codigoVaga}")
    @Size(min = 4, max = 4, message = "{Size.VagaCreateDTO.codigoVaga}")
    String codigoVaga,
    @NotNull(message = "{NotNull.VagaCreateDTO.statusVaga}")
    Vaga.StatusVaga statusVaga
){}
