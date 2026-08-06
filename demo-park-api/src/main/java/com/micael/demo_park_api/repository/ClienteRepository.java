package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
