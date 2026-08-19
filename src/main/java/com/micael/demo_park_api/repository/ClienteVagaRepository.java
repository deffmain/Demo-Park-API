package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.ClienteVaga;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {


    Optional<ClienteVaga> findByReciboCVAndDataSaidaCVIsNull(String recibo);

    Optional<Long> countByIdClienteFK_IdClienteAndDataSaidaCVIsNotNull(Long id);
}
