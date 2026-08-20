package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.ClienteVaga;
import com.micael.demo_park_api.repository.projection.ClienteVagaProjection;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {


    Optional<ClienteVaga> findByReciboCVAndDataSaidaCVIsNull(String recibo);

    Optional<Long> countByIdClienteFK_IdClienteAndDataSaidaCVIsNotNull(Long id);

    Page<ClienteVagaProjection> findAllByIdClienteFKCpf(String cpf, Pageable pageable);
}
