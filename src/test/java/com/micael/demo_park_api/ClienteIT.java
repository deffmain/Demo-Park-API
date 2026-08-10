package com.micael.demo_park_api;


import com.micael.demo_park_api.dto.clienteDTO.ClienteCreateDTO;
import com.micael.demo_park_api.dto.clienteDTO.ClientePageAbleDTO;
import com.micael.demo_park_api.dto.clienteDTO.ClienteResponseDTO;
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
@Sql(scripts = "/sql/clientes/clientes-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clientes/clientes-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)


public class ClienteIT {

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
    public void criarCliente_ComDadosValidos_RetornarDTOeStatus201(){
        ClienteResponseDTO responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("batata", "39829688011"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(ClienteResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.idCliente()).isNotNull();
        assertThat(responseBody.name()).isEqualTo("batata");
        assertThat(responseBody.cpf()).isEqualTo("39829688011");

    }

    @Test
    public void criarCliente_ComCamposInvalidos_RetornarErrorMessageEStatus422(){

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("bat", "39829688011"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("", "39829688011"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);


        responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("batata", "3982968801111"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("batata", "39829"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("batata", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);


    }

    @Test
    public void criarCliente_ComUsuarioSemAutorizacao_RetornarErrorMessageEStatus403(){

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"admin@gmail.com","123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("batata", "39829688011"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(403);

    }

    @Test
    public void criarCliente_ComDadosInvalidos_RetornarErrorMessageEStatus409(){

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "eduarda.barbosa94@outlook.com", "123456"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new ClienteCreateDTO("Eduarda Barbosa", "57386171005"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(409);

    }

    @Test
    public void encontrarCliente_ComIdValido_RetornarClienteResponseDTOeStatus200(){

        ClienteResponseDTO responseBody = webTestClient
            .get()
            .uri("/api/v1/clientes/2")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(ClienteResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.idCliente()).isEqualTo(2);
        assertThat(responseBody.cpf()).isEqualTo("57386171005");

    }

    @Test
    public void encontrarCliente_SemAutorizacao_RetornarErrorMessageEStatus403(){

        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/clientes/2")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "eduarda.barbosa94@outlook.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(403);

    }

    @Test
    public void encontrarCliente_ComIdInexistente_RetornarErrorMessageEStatus404(){

        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/clientes/20")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(404);

    }

    @Test
    public void encontrarTodosClientes_ComUsuarioAutorizado_RetornarClientePageAbleDTOEStatus200(){

        ClientePageAbleDTO responseBody = webTestClient
            .get()
            .uri("/api/v1/clientes?page2&size=2")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(ClientePageAbleDTO.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();


    }

    @Test
    public void encontrarTodosClientes_ComUsuarioSemAutorizacao_RetornarErrorMessageEStatus403(){

        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/clientes?page2&size=2")
            .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "felipe.pereira88@gmail.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(403);

    }

}
