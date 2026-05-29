package com.micael.demo_park_api.service;

import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.dto.PasswordDTO;
import com.micael.demo_park_api.dto.UserRegisterDTO;
import com.micael.demo_park_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



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
    public User alterarCredenciais(Long id, PasswordDTO password){

      User userPass = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

      if(password.currentPassword().equals(userPass.getPassword())){
          if(password.newPassword().equals(password.confirmNewPassword())){
              if(password.newPassword().equals(userPass.getPassword())){
                  throw new RuntimeException("A nova senha não pode ser igual a senha usada anteriormente!");
              }
              userPass.setPassword(password.newPassword());
              return userRepository.save(userPass);
          }
          throw new RuntimeException("Os campos para inserir a nova senha e confimá-la não são iguais");
      }
      throw new RuntimeException("O campo de última senha utilizada não está de acordo com o que é utilizado");
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
