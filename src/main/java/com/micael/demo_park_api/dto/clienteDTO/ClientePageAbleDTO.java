package com.micael.demo_park_api.dto.clienteDTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ClientePageAbleDTO<T>(
    List<T> content,

    boolean first,

    boolean last,

    @JsonProperty("page")
    int number,

    int size,

    @JsonProperty("pageElements")
    int numberOfElements,

    int totalPages,

    long totalElements

){
    @ArraySchema(
            schema = @Schema(implementation = ClienteResponseDTO.class)
    )
    @Override
    public List<T> content() {
        return content;
    }
}
