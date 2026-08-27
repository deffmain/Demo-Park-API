package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.Cliente;
import com.micael.demo_park_api.repository.projection.ClienteProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    @Query("SELECT c FROM Cliente c")
    public Page<ClienteProjection> findAllCliente(Pageable pageable);

    Cliente findByIdUserFKIdUser(Long id);

    Optional<Cliente> findByCpf(String cpf);
}
