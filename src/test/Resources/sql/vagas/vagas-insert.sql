INSERT INTO users_tb (id_user, username, password, role) VALUES (1, 'felipe.pereira88@gmail.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (2, 'eduarda.barbosa94@outlook.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (3, 'felipe.silva35@email.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (4, 'yago.moreira46@hotmail.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (5, 'kaique.melo10@email.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (6, 'quezia.teixeira33@hotmail.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (7, 'mariana.almeida21@hotmail.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (8, 'lucas.carvalho78@gmail.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (9, 'ana.costa74@email.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (10, 'vanessa.rocha81@gmail.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (11, 'diego.ferreira72@email.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_CLIENTE');
INSERT INTO users_tb (id_user, username, password, role) VALUES (12, 'admin@gmail.com', '$2a$12$TxXW6RycNiIlXyBLkrExGuceVzd5SEgu2aua5f07Sgctqdi1zJ7iq', 'ROLE_ADMIN');
ALTER TABLE users_tb ALTER COLUMN id_user RESTART WITH 201;

INSERT INTO vagas_tb (id_vaga, codigovaga, statusvaga) VALUES (2, 'A-01', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigovaga, statusvaga) VALUES (3, 'B-02', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigovaga, statusvaga) VALUES (4, 'C-03', 'OCUPADA');
ALTER TABLE vagas_tb ALTER COLUMN id_vaga RESTART WITH 201;
