package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.ClienteVaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {
}
