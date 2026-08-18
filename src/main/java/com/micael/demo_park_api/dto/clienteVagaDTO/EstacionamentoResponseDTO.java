package com.micael.demo_park_api.dto.clienteVagaDTO;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EstacionamentoResponseDTO(

    String placaCV,
    String marcaCV,
    String modeloCV,
    String corCV,
    String cpf,
    String reciboCV,
    LocalDateTime dataEntradaCV,
    LocalDateTime dataSaidaCV,
    String vagaCodigo,
    BigDecimal valorCV,
    BigDecimal descontoCV

){}
