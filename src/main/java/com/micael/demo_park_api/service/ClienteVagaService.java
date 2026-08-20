package com.micael.demo_park_api.service;


import com.micael.demo_park_api.domain.ClienteVaga;
import com.micael.demo_park_api.exception.EntityNotFoundException;
import com.micael.demo_park_api.repository.ClienteVagaRepository;
import com.micael.demo_park_api.repository.projection.ClienteVagaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteVagaService {

    private final ClienteVagaRepository clienteVagaRepository;

    @Transactional
    public ClienteVaga registrarClienteVaga(ClienteVaga clienteVaga){

        return clienteVagaRepository.save(clienteVaga);
    }

    @Transactional(readOnly = true)
    public ClienteVaga procurarClienteViaRecibo(String recibo){
        return clienteVagaRepository.findByReciboCVAndDataSaidaCVIsNull(recibo).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Registro não econtrado para o recido N°%s, ou checkout já realizado.", recibo))
        );
    }

    @Transactional
    public Long estacionamentosRealizadosTotais(Long id){

        return clienteVagaRepository.countByIdClienteFK_IdClienteAndDataSaidaCVIsNotNull(id).orElseThrow(
        () -> new EntityNotFoundException(String.format("Não forma encontrados registros para o ID:%s", id)));
    }

    @Transactional(readOnly = true)
    public Page<ClienteVagaProjection> encontrarTodosEstPorCpf(Pageable pageable, String cpf){

        Page<ClienteVagaProjection> estacionamentos = clienteVagaRepository.findAllByIdClienteFKCpf(cpf,pageable);

        if(estacionamentos.isEmpty()){
            throw new RuntimeException(String.format("Nenhum registro encontrado encontrado para o CPF:%s", cpf));
        }

        return estacionamentos;

    }


}
