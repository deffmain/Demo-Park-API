package com.micael.demo_park_api.dto.clienteVagaDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EstacionamentoPageAbleDTO (

    List content,

    boolean first,

    boolean last,

    @JsonProperty("page")
    int number,

    int size,

    @JsonProperty("pageElements")
    int numberOfElements,

    int totalPages,

    long totalElements
) {}
