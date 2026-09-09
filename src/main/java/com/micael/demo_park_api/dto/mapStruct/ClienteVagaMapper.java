package com.micael.demo_park_api.dto.mapStruct;

import com.micael.demo_park_api.domain.ClienteVaga;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoCreateDTO;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoPageAbleDTO;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoResponseDTO;
import com.micael.demo_park_api.repository.projection.ClienteVagaProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ClienteVagaMapper {

    @Mapping(target = "idClienteFK", ignore = true)
    ClienteVaga toClienteVaga(
            EstacionamentoCreateDTO estacionamentoCreateDTO
    );

    @Mapping(target = "cpf", source = "idClienteFK.cpf")
    EstacionamentoResponseDTO clienteVagaToEstacionamentoResponseDTO(
            ClienteVaga clienteVaga
    );

    @Mapping(target = "cpf", source = "idClienteFKCpf")
    @Mapping(target = "vagaCodigo", source = "idVagaFKCodigoVaga")
    EstacionamentoResponseDTO toEstacionamentoResponseDTO(
            ClienteVagaProjection projection
    );

    default <T> EstacionamentoPageAbleDTO<T> toPageAbleDto(Page<T> page) {
        return new EstacionamentoPageAbleDTO<>(
                page.getContent(),
                page.isFirst(),
                page.isLast(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}

