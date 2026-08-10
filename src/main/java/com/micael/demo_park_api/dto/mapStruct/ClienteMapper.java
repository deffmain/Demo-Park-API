package com.micael.demo_park_api.dto.mapStruct;


import com.micael.demo_park_api.dto.clienteDTO.ClientePageAbleDTO;
import com.micael.demo_park_api.repository.projection.ClienteProjection;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper
public interface ClienteMapper {

    ClientePageAbleDTO toCliPageAble(Page<ClienteProjection> clienteProjections);
}
