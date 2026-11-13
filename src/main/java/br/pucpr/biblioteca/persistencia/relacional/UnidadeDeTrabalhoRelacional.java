package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Objects;

/**
 * Unidade de trabalho reservada para persistência relacional.
 *
 * <p>
 * Padrão: R08 Abstract Factory — papel: Produto concreto.
 * </p>
 */
public final class UnidadeDeTrabalhoRelacional implements UnidadeDeTrabalho {
    private static final Properties CONFIGURACAO = carregarConfiguracao();
    private final String url;
    private final String usuario;
    private final String senha;
    private Connection conexao;
    private boolean encerrada;

    public UnidadeDeTrabalhoRelacional() {
        this(
                propriedade("biblioteca.db.url", "BIBLIOTECA_DB_URL",
                        "jdbc:mariadb://localhost:3306/biblioteca"),
                propriedade("biblioteca.db.usuario", "BIBLIOTECA_DB_USUARIO", "root"),
                propriedade("biblioteca.db.senha", "BIBLIOTECA_DB_SENHA", ""));
    }

    public UnidadeDeTrabalhoRelacional(String url, String usuario, String senha) {
        this.url = validarTexto(url, "url");
        this.usuario = Objects.requireNonNull(usuario, "usuario nao pode ser nulo");
        this.senha = Objects.requireNonNull(senha, "senha nao pode ser nula");
    }

    @Override
    public synchronized void commit() {
        verificarAberta();
        try {
            if (conexao != null) {
                conexao.commit();
            }
            encerrada = true;
            fecharConexao();
        } catch (SQLException excecao) {
            throw falha("Nao foi possivel efetivar a transacao", excecao);
        }
    }

    @Override
    public synchronized void rollback() {
        verificarAberta();
        try {
            if (conexao != null) {
                conexao.rollback();
            }
            encerrada = true;
            fecharConexao();
        } catch (SQLException excecao) {
            throw falha("Nao foi possivel desfazer a transacao", excecao);
        }
    }

    @Override
    public synchronized void close() {
        if (encerrada) {
            return;
        }
        encerrada = true;
        fecharConexao();
    }

    public synchronized Connection conexao() {
        verificarAberta();
        if (conexao == null) {
            try {
                conexao = DriverManager.getConnection(url, usuario, senha);
                conexao.setAutoCommit(false);
            } catch (SQLException excecao) {
                throw falha("Nao foi possivel abrir a conexao relacional", excecao);
            }
        }
        return conexao;
    }

    private void verificarAberta() {
        if (encerrada) {
            throw new IllegalStateException("Unidade de trabalho encerrada");
        }
    }

    private void fecharConexao() {
        if (conexao == null) {
            return;
        }
        try {
            conexao.close();
        } catch (SQLException excecao) {
            throw falha("Nao foi possivel fechar a conexao relacional", excecao);
        } finally {
            conexao = null;
        }
    }

    private static String propriedade(String sistema, String ambiente, String padrao) {
        String valor = System.getProperty(sistema);
        if (valor != null && !valor.isBlank()) {
            return valor;
        }
        valor = System.getenv(ambiente);
        if (valor != null && !valor.isBlank()) {
            return valor;
        }
        valor = CONFIGURACAO.getProperty(sistema);
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private static Properties carregarConfiguracao() {
        Properties propriedades = new Properties();
        Path caminho = Path.of(
                System.getProperty("biblioteca.config", "config/biblioteca.properties"));
        if (!Files.exists(caminho)) {
            return propriedades;
        }
        try (InputStream entrada = Files.newInputStream(caminho)) {
            propriedades.load(entrada);
            return propriedades;
        } catch (IOException excecao) {
            throw new IllegalStateException(
                    "Nao foi possivel carregar a configuracao: " + caminho, excecao);
        }
    }

    private static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }

    static IllegalStateException falha(String mensagem, SQLException causa) {
        String detalhe = causa.getMessage() == null
                ? "sem detalhes fornecidos pelo driver JDBC"
                : causa.getMessage();
        return new IllegalStateException(
                mensagem + ": " + detalhe
                        + " [SQLState=" + causa.getSQLState()
                        + ", codigo=" + causa.getErrorCode() + "]",
                causa);
    }
}
