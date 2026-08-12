package com.micael.demo_park_api.dto.VagaDTO;

import com.micael.demo_park_api.domain.Vaga;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VagaCreateDTO(
    @NotBlank
    @Size(min = 4, max = 4)
    String codigoVaga,
    @NotNull
    Vaga.StatusVaga statusVaga
){}
