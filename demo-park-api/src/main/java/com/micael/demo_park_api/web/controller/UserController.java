package com.micael.demo_park_api.web.controller;


import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.dto.PasswordDTO;
import com.micael.demo_park_api.dto.UserRegisterDTO;
import com.micael.demo_park_api.dto.UserResponseDTO;
import com.micael.demo_park_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> registarUsuario(@Valid @RequestBody UserRegisterDTO userBody) {

        User user = userService.register(userBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user.getIdUser(), user.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> procurarPorId(@PathVariable Long id){

        User user = this.userService.encontrarPorId(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(new UserResponseDTO(user.getIdUser(), user.getUsername()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @Valid @RequestBody PasswordDTO newPassword){

        User user = userService.alterarCredenciais(id, newPassword);
        return ResponseEntity.status(HttpStatus.OK).body("Senha alterada com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> acessarTodos(){

        List<UserResponseDTO> users =
            userService
                .listarTodosUsurarios()
                .stream()
                .map(user -> new UserResponseDTO(user.getIdUser(), user.getUsername()))
                .toList();

        return ResponseEntity.ok(users);
    }


}
