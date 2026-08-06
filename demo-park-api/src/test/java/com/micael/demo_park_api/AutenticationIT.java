package com.micael.demo_park_api;

import com.micael.demo_park_api.dto.userDTO.UserLoginDTO;
import com.micael.demo_park_api.exception.ErrorMessage;
import com.micael.demo_park_api.jwt.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class AutenticationIT {

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
    public void autenticar_ComCredenciaisValidas_RetornarTokenComStatus200(){

        JwtToken responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDTO("admin@gmail.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(JwtToken.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();

    }

    @Test
    public void autenticar_ComCamposInvalidos_RetornarErrorMessageStatus400(){

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDTO("admin@gmail.dercm", "123456"))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(400);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDTO("admin@gmail.cm", "102030"))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(400);

    }


    @Test
    public void autenticar_ComDadosInvalidos_RetornarErrorMessageStatus422() {

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDTO("admin@gmail.com", "1236"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDTO("", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDTO("", ""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserLoginDTO("admin.com", "123456"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);
    }




    }
