package com.micael.demo_park_api.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false,length = 300)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private Role role = Role.ROLE_CLIENTE;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataModificacao;
    private String criadoPor;
    private String modificadoPor;



    public enum Role{

        ROLE_ADMIN, ROLE_CLIENTE

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id_user, user.id_user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_user);
    }

    @Override
    public String toString() {
        return "User{" +
            "id_user=" + id_user +
            '}';
    }
}
