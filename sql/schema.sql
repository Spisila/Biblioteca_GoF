CREATE TABLE IF NOT EXISTS livros (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    situacao VARCHAR(30) NOT NULL,
    reservado_para VARCHAR(100) NULL,
    data_devolucao DATE NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS movimentacoes (
    id_transacao VARCHAR(100) NOT NULL,
    id_livro BIGINT UNSIGNED NOT NULL,
    id_estudante BIGINT UNSIGNED NOT NULL,
    data_transacao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    num_renovacoes INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_transacao),
    CONSTRAINT fk_movimentacoes_livro
        FOREIGN KEY (id_livro) REFERENCES livros (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_movimentacoes_livro
    ON movimentacoes (id_livro);

CREATE TABLE IF NOT EXISTS reservas (
    id VARCHAR(100) NOT NULL,
    id_livro BIGINT UNSIGNED NOT NULL,
    id_estudante BIGINT UNSIGNED NOT NULL,
    data_reserva DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_reservas_livro (id_livro),
    CONSTRAINT fk_reservas_livro
        FOREIGN KEY (id_livro) REFERENCES livros (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pessoas (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS estudantes (
    id BIGINT UNSIGNED NOT NULL,
    numero_matricula VARCHAR(100) NOT NULL,
    curso VARCHAR(255) NOT NULL,
    limite_emprestimos INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_estudantes_matricula (numero_matricula),
    CONSTRAINT fk_estudantes_pessoa
        FOREIGN KEY (id) REFERENCES pessoas (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS atendentes (
    id BIGINT UNSIGNED NOT NULL,
    turno VARCHAR(100) NOT NULL,
    registro_funcional VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_atendentes_registro (registro_funcional),
    CONSTRAINT fk_atendentes_pessoa
        FOREIGN KEY (id) REFERENCES pessoas (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
