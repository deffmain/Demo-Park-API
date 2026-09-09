package com.micael.demo_park_api.service;


import com.micael.demo_park_api.domain.ClienteVaga;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoResponseDTO;
import com.micael.demo_park_api.dto.mapStruct.ClienteVagaMapper;
import com.micael.demo_park_api.exception.EntityNotFoundException;
import com.micael.demo_park_api.jwt.JwtUserDetails;
import com.micael.demo_park_api.repository.ClienteVagaRepository;
import com.micael.demo_park_api.repository.projection.ClienteVagaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteVagaService {

    private final ClienteVagaRepository clienteVagaRepository;
    private final ClienteVagaMapper clienteVagaMapper;

    @Transactional
    public ClienteVaga registrarClienteVaga(ClienteVaga clienteVaga){

        return clienteVagaRepository.save(clienteVaga);
    }

    @Transactional(readOnly = true)
    public ClienteVaga procurarClienteViaRecibo(String recibo){

        ClienteVaga cv = clienteVagaRepository.findByReciboCVAndDataSaidaCVIsNull(recibo).orElseThrow(
            () -> new EntityNotFoundException("Exception.entityNotFoundException.procurarClienteViaRecibo", recibo));

        JwtUserDetails user = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean isAdmin = user.getRole().equals("ROLE_ADMIN");

        Boolean isClienteDonoDoRecibo = cv.getIdClienteFK().getIdCliente().equals(user.getId());

        if(!isAdmin && !isClienteDonoDoRecibo){
            throw new AccessDeniedException("Acesso negado, este recibo não pertence a você");
        }

        return cv;
    }

    @Transactional
    public Long estacionamentosRealizadosTotais(Long id){

        return clienteVagaRepository.countByIdClienteFK_IdClienteAndDataSaidaCVIsNotNull(id).orElseThrow(
        () -> new EntityNotFoundException("Exception.entityNotFoundException.estacionamentosRealizadosTotais", id));
    }


    @Transactional(readOnly = true)
    public Page<EstacionamentoResponseDTO> encontrarRegistrosEstPorCpf(Pageable pageable, String cpf) {

        Page<ClienteVagaProjection> estacionamentos =
                clienteVagaRepository.findAllByIdClienteFKCpf(cpf, pageable);

        if (estacionamentos.isEmpty()) {
            throw new RuntimeException(
                    String.format("Nenhum registro encontrado para o CPF: %s", cpf)
            );
        }

        return estacionamentos.map(
                clienteVagaMapper::toEstacionamentoResponseDTO
        );
    }


    @Transactional(readOnly = true)
    public Page<EstacionamentoResponseDTO> encontrarRegistrosEstPorId(Pageable pageable, Long id){

        Page<ClienteVagaProjection> estacionamentos = clienteVagaRepository.findAllByIdClienteFKIdUserFKIdUser(id, pageable);

        if(estacionamentos.isEmpty()){
            throw new EntityNotFoundException("Exception.entityNotFoundException.encontrarRegistrosEstPorId", id);}

        return estacionamentos.map(
                clienteVagaMapper::toEstacionamentoResponseDTO);
    }


}
