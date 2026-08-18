package com.micael.demo_park_api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Table(name = "vagas_tb")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vaga implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVaga;

    @Column(nullable = false, unique = true, length = 4)
    private String codigoVaga;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public StatusVaga statusVaga;

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataModificacao;

    @CreatedBy
    private String criadoPor;

    @LastModifiedBy
    private String modificadoPor;


    public enum StatusVaga{
        LIVRE, OCUPADA;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vaga vaga = (Vaga) o;
        return Objects.equals(idVaga, vaga.idVaga);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idVaga);
    }
}
