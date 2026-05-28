package com.micael.demo_park_api.service;

import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.dto.PassWordDTO;
import com.micael.demo_park_api.dto.UserRegisterDTO;
import com.micael.demo_park_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User register(UserRegisterDTO user){

        User newUser = new User();
        newUser.setUsername(user.username());
        newUser.setPassword(user.password());

        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public User encontrarPorId(Long id){
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public User alterarCredenciais(User user, String novaSenha){
        user.setPassword(novaSenha);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> listarTodosUsurarios(){
        List<User> usuarios = userRepository.findAll();

        if(usuarios.isEmpty()){
            throw new RuntimeException("Nenhum usuário encontrado");
        }
        return usuarios;
    }


}
