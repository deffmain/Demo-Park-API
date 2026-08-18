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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "clientes_vagas_tb")
@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)

public class ClienteVaga {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClienteVaga;

    @Column(nullable = false,unique = true, length = 15)
    private String reciboCV;

    @Column(nullable = false,unique = true, length = 45)
    private String placaCV;

    @Column(nullable = false,length = 15)
    private String marcaCV;

    @Column(nullable = false,length = 15)
    private String modeloCV;

    @Column(nullable = false,length = 15)
    private String corCV;

    @Column(nullable = false)
    private LocalDateTime dataEntradaCV;

    @Column(nullable = true)
    private LocalDateTime dataSaidaCV;

    @Column(nullable = true, columnDefinition = "decimal(7,2)")
    private BigDecimal valorCV;

    @Column(nullable = true, columnDefinition = "decimal(7,2)")
    private BigDecimal descontoCV;

    @ManyToOne
    @JoinColumn(name = "id_cliente_fk")
    private Cliente idClienteFK;

    @ManyToOne
    @JoinColumn(name = "id_vaga")
    private Vaga idVagaFK;

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataModificacao;

    @CreatedBy
    private String criadoPor;

    @LastModifiedBy
    private String modificadoPor;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClienteVaga that = (ClienteVaga) o;
        return Objects.equals(idClienteVaga, that.idClienteVaga);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idClienteVaga);
    }
}
