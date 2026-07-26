package com.micael.demo_park_api;

import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.dto.PasswordDTO;
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
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


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
    public void registrarUsuario_ComUsernameEPasswordValidos_RetornoUsuarioCriadoComStatus201() {
        UserResponseDTO responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("definicaounoaha@gmail.com", "1234i5"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(UserResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.idUser()).isNotNull();
        assertThat(responseBody.name()).isEqualTo("definicaounoaha@gmail.com");

    }

    @Test
    public void registrarUsuario_ComCamposInvalidos_RetornoErrorMessageComStatus422() {

        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("junglao@","1fr4aq"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

         responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("kunglao@gmail.com","1fr4aq23"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("",""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("kunglao@gmail.com",""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);
        responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("","1hlmp1"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);
        responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("","1hlm"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);


    }

    @Test
    public void registrarUsuario_ComUsernameJaCadastrado_RetornoErrorMessageComStatus409() {
        ErrorMessage responseBody = webTestClient
            .post()
            .uri("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRegisterDTO("felipe.pereira88@gmail.com","Bk9t2o"))
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(409);

    }

    @Test
    public void procurarPorId_ComIdNaoEncontrado_RetornoErrorMessageComStatus404() {
        ErrorMessage responseBody = webTestClient
            .get()
            .uri("/api/v1/usuarios/0")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(404);

    }

    @Test
    public void alterarSenha_ComDadosValidos_RetornaStatus204(){
       webTestClient
            .patch()
            .uri("/api/v1/usuarios/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("Bk9t2o", "k7-bt2","k7-bt2"))
            .exchange()
            .expectStatus().isNoContent();

    }

    @Test
    public void alterarSenha_ComDadosInvalidos_RetornaErrorMessageEStatus422(){
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/usuarios/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("mlmlml", "k7-bt2","k7-bt2"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/usuarios/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("Bk9t2o", "k7-bt2","mlmlml"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/usuarios/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("Bk9t2o", "Bk9t2o","Bk9t2o"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/usuarios/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("", "",""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/usuarios/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("Bk9t2o", "",""))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

        responseBody = webTestClient
            .patch()
            .uri("/api/v1/usuarios/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("", "Bk9t2o","Bk9t2o"))
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(422);

    }

    @Test
    public void alterarSenha_ComIdInvalido_RetornaErrorMessageEStatus404(){
        ErrorMessage responseBody = webTestClient
            .patch()
            .uri("/api/v1/usuarios/0")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new PasswordDTO("Bk9t2o", "k7-bt2","k7-bt2"))
            .exchange()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatusCode()).isEqualTo(404);

    }

    @Test
    public void listarTodosUsurarios_ComUsuariosRegistrados_RetornaListaDeUserResponseDTOEStatus200(){
        List<UserResponseDTO> responseBody = webTestClient
            .get()
            .uri("/api/v1/usuarios")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(responseBody)
            .isNotNull()
            .isNotEmpty();

        assertThat(responseBody)
            .hasSize(110);

        assertThat(responseBody.getFirst().role()).isEqualTo(User.Role.ROLE_CLIENTE);
    }


}
