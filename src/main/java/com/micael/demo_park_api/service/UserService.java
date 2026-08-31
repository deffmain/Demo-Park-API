package com.micael.demo_park_api.service;

import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.dto.userDTO.PasswordDTO;
import com.micael.demo_park_api.dto.userDTO.UserRegisterDTO;
import com.micael.demo_park_api.exception.EntityNotFoundException;
import com.micael.demo_park_api.exception.PasswordInvalidException;
import com.micael.demo_park_api.exception.UsernameUniqueViolationException;
import com.micael.demo_park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registrarUsuario(UserRegisterDTO user){

        try {
            User newUser = new User();
            newUser.setUsername(user.username());
            newUser.setPassword(passwordEncoder.encode(user.password()));

            return userRepository.save(newUser);
        }
        catch (org.springframework.dao.DataIntegrityViolationException ex){
            throw new UsernameUniqueViolationException(String.format("Usuário {%s} já cadastrado.", user.username()));
        }
    }

    @Transactional(readOnly = true)
    public User encontrarUserPorId(Long idUser){
        return userRepository.findById(idUser)
            .orElseThrow(()-> new EntityNotFoundException("Exception.entityNotFoundException.encontrarUserPorId", idUser));
    }

    @Transactional
    public User alterarSenha(Long idUser, PasswordDTO password){

      User userPass = userRepository.findById(idUser).orElseThrow(() -> new EntityNotFoundException("Exception.entityNotFoundException.encontrarUserPorId", idUser));

      if (passwordEncoder.matches(password.currentPassword(), userPass.getPassword())){
          if(password.newPassword().equals(password.confirmNewPassword())){
              if(passwordEncoder.matches(password.newPassword(), userPass.getPassword())){
                  throw new PasswordInvalidException(String.format("A nova senha não pode ser igual a senha usada anteriormente!"));
              }
              userPass.setPassword(passwordEncoder.encode(password.newPassword()));
              return userRepository.save(userPass);
          }
          throw new PasswordInvalidException(String.format("Os campos para inserir a nova senha e confimá-la não são iguais."));
      }
      throw new PasswordInvalidException(String.format("O campo de última senha utilizada não está de acordo com o que é utilizado."));
    }

    @Transactional(readOnly = true)
    public List<User> listarTodosUsurarios(){

        List<User> usuarios = userRepository.findAll();
        if(usuarios.isEmpty()){
            throw new RuntimeException("Nenhum usuário encontrado");
        }
        return usuarios;
    }

    @Transactional(readOnly = true)
    public User buscarPorUsername(String username){

        return
            this.userRepository
            .findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("Exception.entityNotFoundException.buscarPorUsername", username));
    }

    @Transactional(readOnly = true)
    public User.Role buscarRolePorUsername(String username){
        return this.userRepository.getRoleByUsername(username);
    }


}
