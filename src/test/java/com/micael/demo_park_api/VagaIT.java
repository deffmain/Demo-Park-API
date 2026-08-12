package com.micael.demo_park_api;


import com.micael.demo_park_api.domain.Vaga;
import com.micael.demo_park_api.dto.VagaDTO.VagaCreateDTO;
import com.micael.demo_park_api.dto.VagaDTO.VagaResponseDTO;
import com.micael.demo_park_api.exception.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/vagas/vagas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/vagas/vagas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class VagaIT {

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
    public void criarVaga_ComDadosValidosEUsuarioAutorizado_RetornarStatus200(){

        webTestClient
            .post()
            .uri("/api/v1/vagas")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new VagaCreateDTO("D-01", Vaga.StatusVaga.OCUPADA))
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists("location");
    }

    @Test
    public void criarVaga_ComDadosValidosEUsuarioSemAutorizado_RetornarErrorMessageEStatus403(){

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/vagas")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new VagaCreateDTO("D-01", Vaga.StatusVaga.OCUPADA))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(403);
    }

    @Test
    public void criarVaga_ComCamposInvalidosEUsuarioAutorizado_RetornarErrorMessageEStatus422() {

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/vagas")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new VagaCreateDTO("13344", Vaga.StatusVaga.OCUPADA))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/vagas")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new VagaCreateDTO("13", Vaga.StatusVaga.OCUPADA))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/vagas")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new VagaCreateDTO("", Vaga.StatusVaga.OCUPADA))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);
    }

    @Test
    public void criarVaga_ComDadosIvalidosEUsuarioAutorizado_RetornarErrorMessageEStatus409(){

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/vagas")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new VagaCreateDTO("A-01", Vaga.StatusVaga.OCUPADA))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(409);
    }

    @Test
    public void localizarVaga_ComURIValidaEUsuarioAutorizado_RetornarVagasResponseDTOEStatus200(){

        VagaResponseDTO responseBody = webTestClient
            .get()
            .uri("/api/v1/vagas/A-01")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(VagaResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.codigoVaga()).isEqualTo("A-01");
    }


    @Test
    public void localizarVaga_ComURIInvalidaEUsuarioAutorizado_RetornarErrorMessageEStatus404(){

        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/vagas/{codigoVaga}", "Z-##")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(404);
    }




}
