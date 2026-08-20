
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


INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (2, 'Eduarda Barbosa', '57386171005', 2);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (3, 'Felipe Silva', '85207502046', 3);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (4, 'Yago Moreira', '10924814070', 4);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (5, 'Kaique Melo', '43705127009', 5);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (6, 'Quezia Teixeira', '92617384005', 6);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (7, 'Mariana Almeida', '57284910023', 7);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (8, 'Lucas Carvalho', '31098472056', 8);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (9, 'Ana Costa', '68420173098', 9);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (10, 'Vanessa Rocha', '74519028061', 10);
INSERT INTO clientes_tb (id_cliente, name, cpf, id_user) VALUES (11, 'Diego Ferreira', '20863457012', 11);
ALTER TABLE clientes_tb ALTER COLUMN id_cliente RESTART WITH 201;

INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(1, 'A-01', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(2, 'A-02', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(3, 'A-03', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(4, 'A-04', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(5, 'A-05', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(6, 'A-06', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(7, 'A-07', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(8, 'A-08', 'OCUPADA');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(9, 'A-09', 'OCUPADA');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(10, 'A-10', 'LIVRE');
INSERT INTO vagas_tb (id_vaga, codigo_vaga, status_vaga) VALUES(11, 'B-01', 'LIVRE');
ALTER TABLE vagas_tb ALTER COLUMN id_vaga RESTART WITH 201;

INSERT INTO clientes_vagas_tb(id_cliente_vaga, corcv, data_entradacv, data_saidacv, descontocv, marcacv, modelocv, placacv, recibocv, valorcv, id_vaga, id_cliente_fk)
VALUES(2,'Preto','2026-08-18 10:44:53.067961',null,null,'Honda','Civic','LFJ-1023','20260818-104453',null,8,2);
INSERT INTO clientes_vagas_tb(id_cliente_vaga, corcv, data_entradacv,data_saidacv, descontocv, marcacv, modelocv, placacv, recibocv, valorcv, id_vaga, id_cliente_fk)
VALUES(3,'Cinza','2026-08-19 11:50:39.028952',null,null,'Fiat','Argo','HOA-4632','20260819-115039',null,9,3);
INSERT INTO clientes_vagas_tb(id_cliente_vaga, corcv, data_entradacv, data_saidacv, descontocv, marcacv, modelocv, placacv, recibocv, valorcv, id_vaga, id_cliente_fk)
VALUES(4,'Branco','2026-09-18 12:00:00.075124',null,null,'Bmw','320i','GSK-4424','20260918-120000',null,7,4);
