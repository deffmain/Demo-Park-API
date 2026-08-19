package com.micael.demo_park_api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoUtils {

    public static String gerarRecibo(){

        LocalDateTime localDateTime = LocalDateTime.now();

        String recibo = localDateTime.toString().substring(0, 19);

        return recibo
            .replace("-", "")
            .replace(":", "")
            .replace("T", "-");

    }


    private static final double DESCONTO_PERCENTUAL = 0.30;
    public static BigDecimal calcularDesconto(BigDecimal custo, long numeroDeVezes) {


        BigDecimal desconto = BigDecimal.valueOf(0);

        if(numeroDeVezes>0&numeroDeVezes %10 == 0){
            desconto = custo.multiply(BigDecimal.valueOf(DESCONTO_PERCENTUAL));
        }

        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }



    private static final double PRIMEIROS_15_MINUTES = 5.00;
    private static final double PRIMEIROS_60_MINUTES = 9.25;
    private static final double ADICIONAL_15_MINUTES = 1.75;
    public static BigDecimal calcularCusto(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {
            total = PRIMEIROS_15_MINUTES;
        } else if (minutes <= 60) {
            total = PRIMEIROS_60_MINUTES;
        } else {
            total = PRIMEIROS_60_MINUTES;
            minutes -=60;
            while(minutes>0){
                minutes-=15;
                total += ADICIONAL_15_MINUTES;
            }
        }
        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }


}
