package com.micael.demo_park_api.service;


import com.micael.demo_park_api.domain.ClienteVaga;
import com.micael.demo_park_api.exception.EntityNotFoundException;
import com.micael.demo_park_api.repository.ClienteVagaRepository;
import lombok.RequiredArgsConstructor;
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


}
