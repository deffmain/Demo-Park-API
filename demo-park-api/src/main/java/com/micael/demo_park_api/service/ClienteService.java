package com.micael.demo_park_api.service;

import com.micael.demo_park_api.domain.Cliente;
import com.micael.demo_park_api.exception.CpfUniqueViolationException;
import com.micael.demo_park_api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente createCliente(Cliente cliente){
        try{
            return clienteRepository.save(cliente);
        }catch(DataIntegrityViolationException ex){
            throw new CpfUniqueViolationException(
                String.format("CPF '%s' não pode ser cadastrado pois já existe no sistema", cliente.getCpf()));
        }
    }


}
