package com.micael.demo_park_api.dto.clienteDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteCreateDTO (
    @Size(min = 5, max = 100) @NotNull String name,
    @Size(min = 11, max = 11) @NotNull @CPF String cpf){}
