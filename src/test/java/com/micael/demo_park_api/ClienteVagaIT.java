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

       webTestClient
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
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("statusCode").isEqualTo("422")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();


        webTestClient
            .post()
            .uri("/api/v1/estacionamentos/check-in")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                new EstacionamentoCreateDTO(
                    "######:12344566",
                    "RENAULT",
                    "KWID",
                    "VERMELHO",
                    "57386171005"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("statusCode").isEqualTo("422")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();

        webTestClient
            .post()
            .uri("/api/v1/estacionamentos/check-in")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                new EstacionamentoCreateDTO(
                    "ABC-1234",
                    "RENAULT",
                    "KWID",
                    "VERMELHO",
                    "123412"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("statusCode").isEqualTo("422")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();


    }

    @Test
    public void fazerCheckIn_ComUsuarioSemAutorização_RetornarErrorMessageEStatus403(){

       webTestClient
            .post()
            .uri("/api/v1/estacionamentos/check-in")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                new EstacionamentoCreateDTO(
                    "RLH-1237",
                    "HONDA",
                    "CIVIC",
                    "PRATA",
                    "57386171005"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("statusCode").isEqualTo("403")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();
    }

    @Test
    public void fazerCheckIn_ComCpfSemCadastro_RetornarErrorMessageEStatus404(){

        webTestClient
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
                    "54897697000"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("statusCode").isEqualTo("404")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();
    }


    @Test
    @Sql(scripts = "/sql/clienteVagas/estacionamentosVagas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/clienteVagas/estacionamentosVagas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void fazerCheckIn_semVagaLivre_RetornarErrorMessageEStatus404(){

        webTestClient
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
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("statusCode").isEqualTo("404")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();
    }

    @Test
    public void fazerLeituraDeDadosDoEstacionamento_ComReciboValido_RetornarEstacionamentoResponseDTOEStatus200(){

        webTestClient
            .get()
            .uri("/api/v1/estacionamentos/check-in/20260818-104453")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("placaCV").isEqualTo("LFJ-1023")
            .jsonPath("marcaCV").isEqualTo("Honda")
            .jsonPath("modeloCV").isEqualTo("Civic")
            .jsonPath("corCV").isEqualTo("Preto")
            .jsonPath("cpf").isEqualTo("57386171005");

        webTestClient
            .get()
            .uri("/api/v1/estacionamentos/check-in/20260818-104453")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "eduarda.barbosa94@outlook.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("placaCV").isEqualTo("LFJ-1023")
            .jsonPath("marcaCV").isEqualTo("Honda")
            .jsonPath("modeloCV").isEqualTo("Civic")
            .jsonPath("corCV").isEqualTo("Preto")
            .jsonPath("cpf").isEqualTo("57386171005");
    }

    @Test
    public void fazerLeituraDeDadosDoEstacionamento_ComReciboInexistente_RetornarErrorMessageEStatus404(){

        webTestClient
            .get()
            .uri("/api/v1/estacionamentos/check-in/20240945-111153")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in/20240945-111153")
            .jsonPath("method").isEqualTo("GET")
            .jsonPath("statusCode").isEqualTo("404")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();

        webTestClient
            .get()
            .uri("/api/v1/estacionamentos/check-in/20240945-111153")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "eduarda.barbosa94@outlook.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in/20240945-111153")
            .jsonPath("method").isEqualTo("GET")
            .jsonPath("statusCode").isEqualTo("404")
            .jsonPath("statusText").exists()
            .jsonPath("message").exists();
    }








}
