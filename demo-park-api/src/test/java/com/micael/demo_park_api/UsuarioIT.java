package com.micael.demo_park_api;

import com.micael.demo_park_api.dto.UserRegisterDTO;
import com.micael.demo_park_api.dto.UserResponseDTO;
import com.micael.demo_park_api.exception.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class UsuarioIT {

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
    public void createUsuario_ComUsernameEPasswordValidos_RetornoUsuarioCriadoComStatus201() {
        UserResponseDTO responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("definicaounoaha@gmail.com", "1234i5"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(UserResponseDTO.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.idUser()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.name()).isEqualTo("definicaounoaha@gmail.com");

    }

    @Test
    public void createUsuario_ComCamposInvalidos_RetornoErrorMessageComStatus422() {

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("junglao@","1fr4aq"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatusCode()).isEqualTo(422);

         responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("junglao@gmail.com","1fr4aq23"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatusCode()).isEqualTo(422);

    }

    @Test
    public void createUsuario_ComUsernameJaCadastrado_RetornoErrorMessageComStatus409() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("felipe.pereira88@gmail.com","Bk9t2o"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatusCode()).isEqualTo(409);

    }









}
