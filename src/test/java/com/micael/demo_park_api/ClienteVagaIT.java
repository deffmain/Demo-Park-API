package com.micael.demo_park_api;



import com.micael.demo_park_api.domain.Vaga;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoCreateDTO;
import com.micael.demo_park_api.dto.clienteVagaDTO.EstacionamentoResponseDTO;
import com.micael.demo_park_api.dto.vagaDTO.VagaCreateDTO;
import com.micael.demo_park_api.exception.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clienteVagas/clienteVagas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clienteVagas/clienteVagas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class ClienteVagaIT {

    WebTestClient webTestClient;

    @LocalServerPort
    int port;

    @BeforeEach
    void setup(){
        this.webTestClient =
            WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }


    @Test
    public void fazerCheckIn_ComDadosValidos_RetornarEstacionamentoResponseDTOeURLdeAcessoeStatus201(){

        EstacionamentoResponseDTO responseBody = webTestClient
            .post()
            .uri("/api/v1/estacionamentos/check-in")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                new EstacionamentoCreateDTO(
                    "RLH-1237",
                    "HONDA",
                    "CIVIC",
                    "PRATA",
                    "57386171005"))
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaders.LOCATION)
            .expectBody(EstacionamentoResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.cpf()).isEqualTo("57386171005");

    }

    @Test
    public void fazerCheckIn_ComDadosInvalidos_RetornarErrorMessageEStatus422(){

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/estacionamentos/check-in")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                new EstacionamentoCreateDTO(
                    "",
                    "",
                    "",
                    "",
                    ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class).J

        assertThat(responseBody).isNotNull();

    }




}
