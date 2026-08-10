package com.micael.demo_park_api.dto.mapStruct;

import com.micael.demo_park_api.domain.Cliente;
import com.micael.demo_park_api.dto.clienteDTO.ClientePageAbleDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ClienteMapper {

    ClientePageAbleDTO toCliPageAble(Page cliente);
}
