package com.micael.demo_park_api.dto.mapStruct;

import com.micael.demo_park_api.domain.Vaga;
import com.micael.demo_park_api.dto.vagaDTO.VagaResponseDTO;
import org.mapstruct.Mapper;

@Mapper
public interface VagaMapper {

    VagaResponseDTO vagaToDto(Vaga vaga);

}
