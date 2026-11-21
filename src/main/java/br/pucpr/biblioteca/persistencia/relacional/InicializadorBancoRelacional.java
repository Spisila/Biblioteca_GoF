package br.pucpr.biblioteca.persistencia.relacional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class InicializadorBancoRelacional {
    private InicializadorBancoRelacional() {
    }

    static void inicializar(Connection conexao) throws SQLException {
        try (Statement statement = conexao.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS pessoas (
                        id VARCHAR(100) NOT NULL,
                        nome VARCHAR(255) NOT NULL,
                        cpf VARCHAR(20) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
            statement.execute("""
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
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS atendentes (
                        id VARCHAR(100) NOT NULL,
                        nome VARCHAR(255) NOT NULL,
                        cpf VARCHAR(20) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        turno VARCHAR(100) NOT NULL,
                        registro_funcional VARCHAR(100) NOT NULL,
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_atendentes_registro (registro_funcional)
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
        }
    }
}
