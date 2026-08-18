package com.micael.demo_park_api.dto.clienteVagaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record EstacionamentoCreateDTO(

    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "[A-Z]{3}-[0-9]{4}", message = "A placa do veiculo deve seguir o padrão XXX-0000")
    String placaCV,
    @NotBlank
    String marcaCV,
    @NotBlank
    String modeloCV,
    @NotBlank
    String corCV,
    @NotBlank
    @Size(min = 11, max = 11)
    @CPF
    String cpf
){}
