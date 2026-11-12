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
