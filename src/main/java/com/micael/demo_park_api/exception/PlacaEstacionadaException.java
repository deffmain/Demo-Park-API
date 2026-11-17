package com.micael.demo_park_api.exception;

import lombok.Getter;

@Getter
public class PlacaEstacionadaException extends RuntimeException {

    private final String placa;

    public PlacaEstacionadaException(String placa) {
        super("Veiculo de placa " + placa + " ja esta estacionado");
        this.placa = placa;
    }
}
