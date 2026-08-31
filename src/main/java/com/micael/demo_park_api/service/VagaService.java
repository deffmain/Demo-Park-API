package com.micael.demo_park_api.service;

import com.micael.demo_park_api.domain.Vaga;
import com.micael.demo_park_api.dto.vagaDTO.VagaCreateDTO;
import com.micael.demo_park_api.exception.CodigoUniqueViolationException;
import com.micael.demo_park_api.exception.EntityNotFoundException;
import com.micael.demo_park_api.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.micael.demo_park_api.domain.Vaga.StatusVaga.LIVRE;

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
            throw new CodigoUniqueViolationException("Vaga", vagaCreateDTO.codigoVaga());
        }
    }

    @Transactional(readOnly = true)
    public Vaga procurarPorCodigo(String codigo){
        return vagaRepository.findByCodigoVaga(codigo)
            .orElseThrow(() ->
                    new EntityNotFoundException("Exception.entityNotFoundException.procurarPorCodigo", codigo));
    }

    public Vaga procurarVagaLivre() {
        return vagaRepository.findFirstByStatusVaga(LIVRE).orElseThrow(
            () -> new EntityNotFoundException("Exception.entityNotFoundException.procurarVagaLivre")
        );
    }
}
