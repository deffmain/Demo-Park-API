package com.micael.demo_park_api.dto.mapStruct;


import com.micael.demo_park_api.domain.Cliente;
import com.micael.demo_park_api.dto.clienteDTO.ClientePageAbleDTO;
import com.micael.demo_park_api.dto.clienteDTO.ClienteResponseDTO;
import com.micael.demo_park_api.repository.projection.ClienteProjection;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper
public interface ClienteMapper {


    ClienteResponseDTO toClienteResponseDTO(ClienteProjection cliente);

    ClienteResponseDTO clienteToResponseDTO(Cliente cliente);

    default<T> ClientePageAbleDTO<T> toclientePageAbleDTO(Page<T> page){
        return new ClientePageAbleDTO<>(
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
