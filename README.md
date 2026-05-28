# Demo Park API

API REST para gerenciamento de um sistema de estacionamento, desenvolvida com **Spring Boot** e **Java 21**. O projeto expõe endpoints para cadastro e gerenciamento de usuários, com persistência em banco de dados relacional.

> Projeto de estudos para praticar a construção de APIs REST com Spring Boot, JPA/Hibernate, validação de dados e Lombok.

---

## Tecnologias

- **Java 21**
- **Spring Boot** (Spring Web MVC, Spring Data JPA)
- **Hibernate** (JPA)
- **MySQL** (banco principal)
- **H2 Database** (banco em memória / console)
- **Bean Validation** (Jakarta Validation)
- **Lombok**
- **Maven** (com Maven Wrapper)

---

## Funcionalidades

- Cadastro de usuários com validação de e-mail e senha
- Busca de usuário por ID
- Atualização de senha
- Listagem de todos os usuários
- Controle de perfis de acesso (`ROLE_ADMIN`, `ROLE_CLIENTE`)
- Configuração de fuso horário (`America/Sao_Paulo`) e locale (`pt_BR`)

---

## Estrutura do projeto

```
demo-park-api/
├── src/main/java/com/micael/demo_park_api/
│   ├── DemoParkApiApplication.java        # Classe principal
│   ├── config/
│   │   └── SpringTimezoneConfig.java      # Configuração de fuso horário
│   ├── domain/
│   │   └── User.java                      # Entidade de usuário
│   ├── dto/
│   │   ├── UserRegisterDTO.java           # DTO de cadastro
│   │   ├── UserResponseDTO.java           # DTO de resposta
│   │   └── PassWordDTO.java               # DTO de alteração de senha
│   ├── repository/
│   │   └── UserRepository.java            # Repositório JPA
│   ├── service/
│   │   └── UserService.java               # Regras de negócio
│   └── web/controller/
│       └── UserController.java            # Endpoints REST
└── src/test/java/...                      # Testes
```

---

## Pré-requisitos

- JDK 21 ou superior
- MySQL em execução (local ou remoto)
- Maven (opcional — o projeto inclui o Maven Wrapper `mvnw`)

---

## Configuração

As credenciais do banco **não são versionadas** (o arquivo `application.properties` está no `.gitignore` por segurança). Crie o arquivo em `src/main/resources/application.properties` com o conteúdo abaixo, substituindo `SEU_BANCO`, `SEU_USUARIO` e `SUA_SENHA` pelos seus dados:

```properties
spring.application.name=demo-park-api

#=========================================
# LOCALE - configurando o local do sistema
#=========================================
spring.mvc.locale-resolver=fixed
spring.mvc.locale=pt_BR

#=====================================
# MySQL - configuração do banco de dados
#=====================================
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://SEU_BANCO?useSSL=false&allowPublicKeyRetrieval=true&serverTimeZone=America/Sao_Paulo
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

#=======================================
#              JPA
#=======================================
spring.jpa.show.sql=true
spring.jpa.properties.hibernate.format_sql=true

# Cria/atualiza as tabelas automaticamente conforme as anotações (@Entity)
spring.jpa.hibernate.ddl-auto=update
```

> A propriedade `spring.jpa.hibernate.ddl-auto=update` faz o Hibernate criar e atualizar as tabelas automaticamente a partir das entidades anotadas com `@Entity`.

---

## Como executar

Na pasta do projeto (`demo-park-api/`):

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A aplicação sobe, por padrão, em `http://localhost:8080`.

---

## Endpoints

Base: `/api/v1/usuarios`

### Cadastrar usuário
`POST /api/v1/usuarios`

Corpo da requisição:
```json
{
  "username": "usuario@email.com",
  "password": "123456"
}
```
- `username`: e-mail válido e obrigatório
- `password`: obrigatório, exatamente 6 caracteres

**Resposta:** `201 CREATED`
```json
{
  "id": 1,
  "name": "usuario@email.com"
}
```

### Buscar usuário por ID
`GET /api/v1/usuarios/{id}`

**Resposta:** `302 FOUND`
```json
{
  "id": 1,
  "name": "usuario@email.com"
}
```

### Atualizar senha
`PATCH /api/v1/usuarios/{id}`

Corpo da requisição:
```json
{
  "password": "novaSenha"
}
```

**Resposta:** `200 OK`
```
Senha alterada com sucesso!
```

### Listar todos os usuários
`GET /api/v1/usuarios`

**Resposta:** `200 OK` — lista de usuários.

---

## Modelo de dados

Entidade `User` (tabela `users_tb`):

| Campo             | Tipo            | Descrição                                  |
|-------------------|-----------------|--------------------------------------------|
| `id_user`         | `Long`          | Identificador (auto incremento)            |
| `username`        | `String`        | Único, obrigatório (até 100 caracteres)    |
| `password`        | `String`        | Obrigatório (até 300 caracteres)           |
| `role`            | `Role`          | `ROLE_ADMIN` ou `ROLE_CLIENTE`             |
| `dataCriacao`     | `LocalDateTime` | Data de criação                            |
| `dataModificacao` | `LocalDateTime` | Data da última modificação                 |
| `criadoPor`       | `String`        | Autor da criação                           |
| `modificadoPor`   | `String`        | Autor da última modificação                |

---

## Autor

Micael Cruz Batista Martins - Engenharia de software
