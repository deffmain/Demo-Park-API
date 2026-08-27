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

    private Map<String, Object> params = new HashMap<>();

    private static final  String JASPER_DIRECTORIO = "classpath:reports/";


    public void addParams(String key, Object value){

        this.params.put("IMAGEM_DIRETORIO", JASPER_DIRECTORIO);
        this.params.put("REPORT_LOCALE",  Locale.of("pt","BR"));
        this.params.put(key, value);
    }


    public byte[] gerarPdf(){
        byte[] relatorio = null;

        try{
            Resource resource = resourceLoader.getResource(JASPER_DIRECTORIO.concat("estacionamentos.jasper"));
            InputStream stream = resource.getInputStream();
            JasperPrint jasperPrint = JasperFillManager.fillReport(stream, params, dataSource.getConnection());
            relatorio = JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (IOException | JRException | SQLException e) {
            log.info("Jasper reports :::", e.getCause());
            throw new RuntimeException(e);
        }

        return relatorio;
    }




}
