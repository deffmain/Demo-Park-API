package com.micael.demo_park_api.service;

import com.micael.demo_park_api.domain.Cliente;
import com.micael.demo_park_api.exception.CpfUniqueViolationException;
import com.micael.demo_park_api.exception.EntityNotFoundException;
import com.micael.demo_park_api.repository.ClienteRepository;
import com.micael.demo_park_api.repository.projection.ClienteProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public Cliente encontrarClientePorId(Long idCliente){
        return clienteRepository.findById(idCliente).
            orElseThrow(() -> new EntityNotFoundException(String.format("Cliente com id = %s não encontrado.", idCliente)));
    }

    @Transactional(readOnly = true)
    public Page<ClienteProjection> encontrarTodosClientes(Pageable pageable){

        Page<ClienteProjection> clientes = clienteRepository.findAllCliente(pageable);

        if(clientes.isEmpty()){
            throw new RuntimeException("Nenhum cliente encontrado");
        }
        return clientes;
    }

    @Transactional(readOnly = true)
    public Cliente encontrarUsuarioPorId(Long id) {
        return clienteRepository.findByidUserFK(id);
    }

    public Cliente encontrarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).orElseThrow(
            () -> new EntityNotFoundException(String.format("Cliente com o CPF:%s não encontrado.", cpf))
        );
    }
}
