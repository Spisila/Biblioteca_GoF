CREATE TABLE IF NOT EXISTS livros (
    id VARCHAR(100) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    situacao VARCHAR(30) NOT NULL,
    reservado_para VARCHAR(100) NULL,
    data_devolucao DATE NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS movimentacoes (
    id_transacao VARCHAR(100) NOT NULL,
    id_livro VARCHAR(100) NOT NULL,
    id_estudante VARCHAR(100) NOT NULL,
    data_transacao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    num_renovacoes INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_transacao),
    CONSTRAINT fk_movimentacoes_livro
        FOREIGN KEY (id_livro) REFERENCES livros (id)
) ENGINE = InnoDB;

CREATE INDEX idx_movimentacoes_livro
    ON movimentacoes (id_livro);

CREATE TABLE IF NOT EXISTS reservas (
    id VARCHAR(100) NOT NULL,
    id_livro VARCHAR(100) NOT NULL,
    id_estudante VARCHAR(100) NOT NULL,
    data_reserva DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_reservas_livro (id_livro),
    CONSTRAINT fk_reservas_livro
        FOREIGN KEY (id_livro) REFERENCES livros (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pessoas (
    id VARCHAR(100) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS estudantes (
    id VARCHAR(100) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    numero_matricula VARCHAR(100) NOT NULL,
    curso VARCHAR(255) NOT NULL,
    limite_emprestimos INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_estudantes_matricula (numero_matricula)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS atendentes (
    id VARCHAR(100) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    turno VARCHAR(100) NOT NULL,
    registro_funcional VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_atendentes_registro (registro_funcional)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
