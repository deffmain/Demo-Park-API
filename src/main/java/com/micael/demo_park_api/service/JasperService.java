package com.micael.demo_park_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor

public class JasperService {

    private final ResourceLoader resourceLoader;
    private final DataSource dataSource;

    private static final  String JASPER_DIRECTORIO = "classpath:reports/";
    private static final String JASPER_ARQUIVO = "Estacionamentos.jasper";


    // Os parametros sao montados a cada chamada: o service e singleton, entao guardar
    // estado em campo misturaria relatorios de requisicoes simultaneas.
    public byte[] gerarPdf(Map<String, Object> parametros){
        Map<String, Object> params = new HashMap<>(parametros);
        params.put("IMAGEM_DIRETORIO", JASPER_DIRECTORIO);
        params.put("REPORT_LOCALE",  Locale.of("pt","BR"));

        byte[] relatorio = null;

        Resource resource = resourceLoader.getResource(JASPER_DIRECTORIO.concat(JASPER_ARQUIVO));
        try (InputStream stream = resource.getInputStream();
             Connection connection = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(stream, params, connection);
            relatorio = JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (IOException | JRException | SQLException e) {
            log.info("Jasper reports :::", e.getCause());
            throw new RuntimeException(e);
        }

        return relatorio;
    }




}
