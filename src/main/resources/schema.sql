DROP TABLE IF EXISTS Voto;
DROP TABLE IF EXISTS Sessao;
DROP TABLE IF EXISTS Pauta;
DROP TABLE IF EXISTS Resultado;
DROP SEQUENCE IF EXISTS voto_id_seq;
DROP SEQUENCE IF EXISTS resultado_id_seq;
DROP SEQUENCE IF EXISTS pauta_id_seq;
DROP TYPE IF EXISTS opcaoVoto;
DROP TYPE IF EXISTS statusSessao;

--ENUM TYPES
CREATE TYPE opcaoVoto AS ENUM ('SIM', 'NAO', 'INDETERMINADO');
CREATE TYPE statusSessao AS ENUM ('ABERTO', 'APURANDO', 'FECHADO');

CREATE SEQUENCE IF NOT EXISTS resultado_id_seq START 1;
CREATE TABLE Resultado(
  id BIGINT PRIMARY KEY DEFAULT NEXTVAL('resultado_id_seq'),
  opcao_eleita opcaoVoto,
  total BIGINT NOT NULL
);
CREATE SEQUENCE IF NOT EXISTS pauta_id_seq START 1;
CREATE TABLE Pauta(
  id BIGINT PRIMARY KEY DEFAULT NEXTVAL('pauta_id_seq'),
  assunto VARCHAR(70) UNIQUE NOT NULL,
  descricao VARCHAR(300),
  criado_em TIMESTAMP NOT NULL,
  resultado_id BIGINT,
  FOREIGN KEY (resultado_id) REFERENCES Resultado(id)
);

CREATE TABLE Sessao(
  id BIGINT REFERENCES Pauta(id),
  data_hora_abertura TIMESTAMP NOT NULL,
  data_hora_fechamento TIMESTAMP NOT NULL,
  status statusSessao NOT NULL,
  PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS voto_id_seq START 1;
CREATE TABLE Voto(
  id BIGINT DEFAULT NEXTVAL('voto_id_seq'),
  sessao_id BIGINT NOT NULL REFERENCES Sessao(id),
  cpf_number VARCHAR(11) UNIQUE NOT NULL,
  opcao opcaoVoto NOT NULL,
  data_hora_voto TIMESTAMP NOT NULL,
  PRIMARY KEY (sessao_id, cpf_number)
);
