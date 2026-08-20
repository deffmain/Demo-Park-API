package com.micael.demo_park_api.repository.projection;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ClienteVagaProjection {

     Long getIdClienteVaga();
     String getReciboCV();
     String getPlacaCV();
     String getMarcaCV();
     String getModeloCV();
     String getCorCV();
     LocalDateTime getDataEntradaCV();
     LocalDateTime getDataSaidaCV();
     BigDecimal getValorCV();
     BigDecimal getDescontoCV();
     Long getIdClienteFKCpf();
}
