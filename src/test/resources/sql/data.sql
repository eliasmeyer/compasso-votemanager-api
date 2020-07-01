SET TIMEZONE TO 'America/Sao_Paulo'; 

DELETE FROM Voto;

DELETE FROM Sessao;

DELETE FROM Pauta;

DELETE FROM Resultado;

ALTER SEQUENCE resultado_id_seq RESTART WITH 1;

ALTER SEQUENCE pauta_id_seq RESTART WITH 1;

ALTER SEQUENCE voto_id_seq RESTART WITH 1;

INSERT INTO Resultado (id, opcao_eleita, total) VALUES(nextval('resultado_id_seq'), 'SIM', 3);

INSERT INTO Pauta (id, assunto, descricao, criado_em, resultado_id) VALUES (nextval('pauta_id_seq'), 'Pauta Script Fake #1', 'Descricao Fake #1', now(), currval('resultado_id_seq'));

INSERT INTO Sessao(id, data_hora_abertura, data_hora_fechamento, status) VALUES (currval('pauta_id_seq'), now() - INTERVAL '2 min', now(), 'FECHADO');

INSERT INTO Voto(id, sessao_id, cpf_number, opcao, data_hora_voto) VALUES (nextval('voto_id_seq'), currval('pauta_id_seq'), '83383174204', 'SIM', now() - INTERVAL '1 min');
INSERT INTO Voto(id, sessao_id, cpf_number, opcao, data_hora_voto) VALUES (nextval('voto_id_seq'), currval('pauta_id_seq'), '48171885020', 'SIM', now() - INTERVAL '1 min');
INSERT INTO Voto(id, sessao_id, cpf_number, opcao, data_hora_voto) VALUES (nextval('voto_id_seq'), currval('pauta_id_seq'), '00351456503', 'NAO', now() - INTERVAL '1 min');
INSERT INTO Voto(id, sessao_id, cpf_number, opcao, data_hora_voto) VALUES (nextval('voto_id_seq'), currval('pauta_id_seq'), '05859497865', 'SIM', now() - INTERVAL '1 min');
