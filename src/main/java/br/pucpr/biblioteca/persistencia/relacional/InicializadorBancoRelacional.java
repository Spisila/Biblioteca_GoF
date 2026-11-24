package br.pucpr.biblioteca.persistencia.relacional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class InicializadorBancoRelacional {
    private static final String COLLATION = "utf8mb4_unicode_ci";

    private InicializadorBancoRelacional() {
    }

    static void inicializar(Connection conexao) throws SQLException {
        padronizarSessao(conexao);
        criarTabelas(conexao);
        removerConstraintsAntesDaConversao(conexao);
        migrarAutoIncrementos(conexao);
        migrarCamposPessoa(conexao);
        padronizarCollations(conexao);
        adicionarRelacionamentoSePossivel(
                conexao, "movimentacoes", "fk_movimentacoes_livro",
                "id_livro", "livros", "id");
        adicionarRelacionamentoSePossivel(
                conexao, "reservas", "fk_reservas_livro",
                "id_livro", "livros", "id");
        adicionarRelacionamentoSePossivel(
                conexao, "movimentacoes", "fk_movimentacoes_estudante",
                "id_estudante", "pessoas", "id");
        adicionarRelacionamentoSePossivel(
                conexao, "reservas", "fk_reservas_estudante",
                "id_estudante", "pessoas", "id");
        adicionarRelacionamentoSePossivel(
                conexao, "estudantes", "fk_estudantes_pessoa",
                "id", "pessoas", "id");
        adicionarRelacionamentoSePossivel(
                conexao, "atendentes", "fk_atendentes_pessoa",
                "id", "pessoas", "id");
    }

    private static void removerConstraintsAntesDaConversao(
            Connection conexao) throws SQLException {
        String sql = """
                SELECT TABLE_NAME, CONSTRAINT_NAME
                FROM information_schema.TABLE_CONSTRAINTS
                WHERE CONSTRAINT_SCHEMA = DATABASE()
                  AND CONSTRAINT_TYPE = 'FOREIGN KEY'
                  AND TABLE_NAME IN (
                      'livros', 'movimentacoes', 'reservas',
                      'pessoas', 'estudantes', 'atendentes'
                  )
                """;
        java.util.List<String[]> constraints = new java.util.ArrayList<>();
        try (Statement statement = conexao.createStatement();
                ResultSet resultado = statement.executeQuery(sql)) {
            while (resultado.next()) {
                constraints.add(new String[]{
                    resultado.getString("TABLE_NAME"),
                    resultado.getString("CONSTRAINT_NAME")
                });
            }
        }
        for (String[] constraint : constraints) {
            removerConstraint(conexao, constraint[0], constraint[1]);
        }
    }

    private static void removerConstraint(
            Connection conexao,
            String tabela,
            String restricao) throws SQLException {
        if (!tabelaExiste(conexao, tabela)
                || !constraintExiste(conexao, tabela, restricao)) {
            return;
        }
        try (Statement statement = conexao.createStatement()) {
            statement.execute(
                    "ALTER TABLE " + tabela
                            + " DROP FOREIGN KEY " + restricao);
        }
    }

    private static void padronizarSessao(Connection conexao) throws SQLException {
        try (Statement statement = conexao.createStatement()) {
            statement.execute("SET NAMES utf8mb4 COLLATE " + COLLATION);
        }
    }

    private static void migrarCamposPessoa(Connection conexao) throws SQLException {
        if (tabelaExiste(conexao, "pessoas")
                && !colunaExiste(conexao, "pessoas", "data_nascimento")) {
            try (Statement statement = conexao.createStatement()) {
                statement.execute(
                        "ALTER TABLE pessoas ADD COLUMN data_nascimento DATE NULL");
                statement.execute(
                        "UPDATE pessoas SET data_nascimento = '1900-01-01' "
                                + "WHERE data_nascimento IS NULL");
                statement.execute(
                        "ALTER TABLE pessoas MODIFY data_nascimento DATE NOT NULL");
            }
        }
    }

    private static void migrarAutoIncrementos(Connection conexao) throws SQLException {
        // Tabelas novas usam BIGINT AUTO_INCREMENT no schema.sql.
        // Bases existentes nao sao alteradas automaticamente para evitar perda
        // ou conversao invalida de IDs ja utilizados.
    }

    private static void migrarChavesNumericas(Connection conexao) throws SQLException {
        try (Statement statement = conexao.createStatement()) {
            if (tabelaExiste(conexao, "movimentacoes")) {
                statement.execute(
                        "ALTER TABLE movimentacoes "
                                + "MODIFY id_livro BIGINT UNSIGNED NOT NULL");
                statement.execute(
                        "ALTER TABLE movimentacoes "
                                + "MODIFY id_estudante BIGINT UNSIGNED NOT NULL");
            }
            if (tabelaExiste(conexao, "reservas")) {
                statement.execute(
                        "ALTER TABLE reservas "
                                + "MODIFY id_livro BIGINT UNSIGNED NOT NULL");
                statement.execute(
                        "ALTER TABLE reservas "
                                + "MODIFY id_estudante BIGINT UNSIGNED NOT NULL");
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel migrar as chaves numericas de circulacao", excecao);
        }
    }

    private static void migrarAutoIncremento(
            Connection conexao,
            String tabela,
            String coluna) throws SQLException {
        if (!tabelaExiste(conexao, tabela)) {
            return;
        }
        try (Statement statement = conexao.createStatement()) {
            statement.execute(
                    "ALTER TABLE " + tabela
                            + " MODIFY " + coluna
                            + " BIGINT UNSIGNED NOT NULL"
                            + (tabela.equals("livros") || tabela.equals("pessoas")
                            ? " AUTO_INCREMENT" : ""));
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel migrar o ID automatico da tabela " + tabela,
                    excecao);
        }
    }

    private static boolean colunaExiste(
            Connection conexao,
            String tabela,
            String coluna) throws SQLException {
        try (PreparedStatement statement = conexao.prepareStatement("""
                SELECT COUNT(*)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """)) {
            statement.setString(1, tabela);
            statement.setString(2, coluna);
            try (ResultSet resultado = statement.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    private static void criarTabelas(Connection conexao) throws SQLException {
        try (Statement statement = conexao.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS livros (
                        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                        titulo VARCHAR(255) NOT NULL,
                        situacao VARCHAR(30) NOT NULL,
                        reservado_para VARCHAR(100) NULL,
                        data_devolucao DATE NULL,
                        PRIMARY KEY (id)
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS pessoas (
                        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                        nome VARCHAR(255) NOT NULL,
                        cpf VARCHAR(20) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        data_nascimento DATE NOT NULL,
                        PRIMARY KEY (id)
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS movimentacoes (
                        id_transacao VARCHAR(100) NOT NULL,
                        id_livro BIGINT UNSIGNED NOT NULL,
                        id_estudante BIGINT UNSIGNED NOT NULL,
                        data_transacao DATE NOT NULL,
                        data_vencimento DATE NOT NULL,
                        num_renovacoes INT NOT NULL DEFAULT 0,
                        PRIMARY KEY (id_transacao)
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS reservas (
                        id VARCHAR(100) NOT NULL,
                        id_livro BIGINT UNSIGNED NOT NULL,
                        id_estudante BIGINT UNSIGNED NOT NULL,
                        data_reserva DATE NOT NULL,
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_reservas_livro (id_livro)
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
            statement.execute("""
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
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
            statement.execute("""
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
                    ) ENGINE = InnoDB
                      DEFAULT CHARSET = utf8mb4
                      COLLATE = utf8mb4_unicode_ci
                    """);
        }
    }

    private static void padronizarCollations(Connection conexao) throws SQLException {
        String[] tabelas = {
            "livros",
            "pessoas",
            "movimentacoes",
            "reservas",
            "estudantes",
            "atendentes"
        };
        try (Statement statement = conexao.createStatement()) {
            for (String tabela : tabelas) {
                if (tabelaExiste(conexao, tabela)) {
                    statement.execute(
                            "ALTER TABLE " + tabela
                                    + " CONVERT TO CHARACTER SET utf8mb4"
                                    + " COLLATE " + COLLATION);
                }
            }
        }
    }

    private static boolean tabelaExiste(Connection conexao, String tabela) throws SQLException {
        try (PreparedStatement statement = conexao.prepareStatement("""
                SELECT COUNT(*)
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                """)) {
            statement.setString(1, tabela);
            try (ResultSet resultado = statement.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    private static void adicionarRelacionamentoSePossivel(
            Connection conexao,
            String tabela,
            String restricao,
            String coluna,
            String tabelaReferenciada,
            String colunaReferenciada) throws SQLException {
        if (!tabelaExiste(conexao, tabela)
                || !tabelaExiste(conexao, tabelaReferenciada)
                || constraintExiste(conexao, tabela, restricao)) {
            return;
        }
        if (existeOrfao(conexao, tabela, coluna, tabelaReferenciada, colunaReferenciada)) {
            return;
        }
        try (Statement statement = conexao.createStatement()) {
            statement.execute("""
                    ALTER TABLE %s
                    ADD CONSTRAINT %s
                    FOREIGN KEY (%s) REFERENCES %s (%s)
                    ON UPDATE CASCADE
                    ON DELETE RESTRICT
                    """.formatted(
                            tabela, restricao, coluna, tabelaReferenciada, colunaReferenciada));
        }
    }

    private static boolean constraintExiste(
            Connection conexao,
            String tabela,
            String restricao) throws SQLException {
        try (PreparedStatement statement = conexao.prepareStatement("""
                SELECT COUNT(*)
                FROM information_schema.TABLE_CONSTRAINTS
                WHERE CONSTRAINT_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND CONSTRAINT_NAME = ?
                  AND CONSTRAINT_TYPE = 'FOREIGN KEY'
                """)) {
            statement.setString(1, tabela);
            statement.setString(2, restricao);
            try (ResultSet resultado = statement.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    private static boolean existeOrfao(
            Connection conexao,
            String tabela,
            String coluna,
            String tabelaReferenciada,
            String colunaReferenciada) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM %s atual
                LEFT JOIN %s referencia
                  ON referencia.%s = atual.%s
                WHERE atual.%s IS NOT NULL
                  AND referencia.%s IS NULL
                """.formatted(
                        tabela, tabelaReferenciada,
                        colunaReferenciada, coluna,
                        coluna, colunaReferenciada);
        try (Statement statement = conexao.createStatement();
                ResultSet resultado = statement.executeQuery(sql)) {
            resultado.next();
            return resultado.getInt(1) > 0;
        }
    }
}
