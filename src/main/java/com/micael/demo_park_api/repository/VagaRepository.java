package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    Optional<Vaga> findByCodigoVaga(String codigo);
}
