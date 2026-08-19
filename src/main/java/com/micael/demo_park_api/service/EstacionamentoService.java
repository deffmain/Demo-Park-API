package com.micael.demo_park_api.service;


import com.micael.demo_park_api.domain.ClienteVaga;
import com.micael.demo_park_api.domain.Vaga;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoCreateDTO;
import com.micael.demo_park_api.dto.mapStruct.ClienteVagaMapper;
import com.micael.demo_park_api.util.EstacionamentoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EstacionamentoService {

    private final ClienteVagaService clienteVagaService;
    private final ClienteService clienteService;
    private final VagaService vagaService;
    private final ClienteVagaMapper clienteVagaMapper;

    @Transactional
    public ClienteVaga checkIn(EstacionamentoCreateDTO etcDTO){

        ClienteVaga clienteVaga = clienteVagaMapper.toClienteVaga(etcDTO);

        clienteVaga.setIdClienteFK(clienteService.encontrarPorCpf(etcDTO.cpf()));
        Vaga vaga = vagaService.procurarVagaLivre();
        vaga.setStatusVaga(Vaga.StatusVaga.OCUPADA);
        clienteVaga.setIdVagaFK(vaga);

        clienteVaga.setDataEntradaCV(LocalDateTime.now());

        clienteVaga.setReciboCV(EstacionamentoUtils.gerarRecibo());

        return clienteVagaService.registrarClienteVaga(clienteVaga);
    }

    @Transactional
    public ClienteVaga checkout(String recibo){

        ClienteVaga clienteVaga = clienteVagaService.procurarClienteViaRecibo(recibo);

        clienteVaga.setDataSaidaCV(LocalDateTime.now());

        clienteVaga.setValorCV(EstacionamentoUtils
            .calcularCusto(clienteVaga.getDataEntradaCV(),clienteVaga.getDataSaidaCV()));

        clienteVaga.setDescontoCV(
            EstacionamentoUtils
                .calcularDesconto(
                    clienteVaga.getValorCV(),
                    clienteVagaService.estacionamentosRealizadosTotais(clienteVaga.getIdClienteFK().getIdCliente())));

        clienteVaga.getIdVagaFK().setStatusVaga(Vaga.StatusVaga.LIVRE);

        if(!clienteVaga.getDescontoCV().equals(BigDecimal.valueOf(0))){
            clienteVaga
                .setValorCV(clienteVaga.getValorCV().subtract(clienteVaga.getDescontoCV()));
        }

        return clienteVagaService.registrarClienteVaga(clienteVaga);
    }

}
