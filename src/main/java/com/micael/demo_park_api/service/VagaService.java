package com.micael.demo_park_api.service;

import com.micael.demo_park_api.domain.Vaga;
import com.micael.demo_park_api.dto.VagaDTO.VagaCreateDTO;
import com.micael.demo_park_api.exception.CodigoUniqueViolationException;
import com.micael.demo_park_api.exception.EntityNotFoundException;
import com.micael.demo_park_api.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;

    @Transactional
    public Vaga criarVaga(VagaCreateDTO vagaCreateDTO){
        try{

            Vaga vaga = new Vaga();
            vaga.setCodigoVaga(vagaCreateDTO.codigoVaga());
            vaga.setStatusVaga(vagaCreateDTO.statusVaga());

            return vagaRepository.save(vaga);
        }catch (DataIntegrityViolationException ex){
            throw new CodigoUniqueViolationException(
                String.format("Vaga com o codigo %s já cadastrada", vagaCreateDTO.codigoVaga()));
        }
    }

    @Transactional(readOnly = true)
    public Vaga procurarPorCodigo(String codigo){
        return vagaRepository.findByCodigoVaga(codigo)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Vaga com o código %s não encontrada", codigo)));
    }
}
