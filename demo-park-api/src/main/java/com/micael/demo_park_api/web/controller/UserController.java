package com.micael.demo_park_api.web.controller;


import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.dto.PassWordDTO;
import com.micael.demo_park_api.dto.UserRegisterDTO;
import com.micael.demo_park_api.dto.UserResponseDTO;
import com.micael.demo_park_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> registarUsuario(@Valid @RequestBody UserRegisterDTO userBody) {

        User user = userService.register(userBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user.getId_user(), user.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> procurarPorId(@PathVariable Long id){

        User user = this.userService.encontrarPorId(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(new UserResponseDTO(user.getId_user(), user.getUsername()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @RequestBody PassWordDTO newPassword){

        User user = userService.alterarCredenciais(this.userService.encontrarPorId(id), newPassword.password());
        return ResponseEntity.status(HttpStatus.OK).body("Senha alterada com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<User>> acessarTodos(){

        List<User> users = userService.listarTodosUsurarios();
        return ResponseEntity.ok(users);
    }


}
