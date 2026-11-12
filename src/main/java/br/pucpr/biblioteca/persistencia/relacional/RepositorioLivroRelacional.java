package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Esqueleto do repositório relacional de livros.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioLivroRelacional implements RepositorioLivro {
    private final UnidadeDeTrabalhoRelacional unidadeDeTrabalho;

    public RepositorioLivroRelacional(UnidadeDeTrabalhoRelacional unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    public UnidadeDeTrabalhoRelacional unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvar(Livro livro) {
        String atualizar = "UPDATE livros SET titulo = ?, situacao = ?, reservado_para = ?, "
                + "data_devolucao = ? WHERE id = ?";
        try (PreparedStatement statement = conexao().prepareStatement(atualizar)) {
            preencher(statement, livro);
            statement.setString(5, livro.id());
            if (statement.executeUpdate() == 0) {
                String inserir = "INSERT INTO livros "
                        + "(id, titulo, situacao, reservado_para, data_devolucao) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insert = conexao().prepareStatement(inserir)) {
                    insert.setString(1, livro.id());
                    preencher(insert, livro, 2);
                    insert.executeUpdate();
                }
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar o livro", excecao);
        }
    }

    @Override
    public Optional<Livro> buscarPorId(String id) {
        String sql = "SELECT id, titulo, situacao, reservado_para, data_devolucao "
                + "FROM livros WHERE id = ?";
        try (PreparedStatement statement = conexao().prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar o livro", excecao);
        }
    }

    @Override
    public List<Livro> listar() {
        String sql = "SELECT id, titulo, situacao, reservado_para, data_devolucao "
                + "FROM livros ORDER BY id";
        List<Livro> livros = new ArrayList<>();
        try (Statement statement = conexao().createStatement();
                ResultSet resultado = statement.executeQuery(sql)) {
            while (resultado.next()) {
                livros.add(mapear(resultado));
            }
            return livros;
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel listar os livros", excecao);
        }
    }

    @Override
    public void remover(String id) {
        try (PreparedStatement statement = conexao().prepareStatement(
                "DELETE FROM livros WHERE id = ?")) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel remover o livro", excecao);
        }
    }

    private Connection conexao() {
        return unidadeDeTrabalho.conexao();
    }

    private static void preencher(PreparedStatement statement, Livro livro) throws SQLException {
        preencher(statement, livro, 1);
    }

    private static void preencher(
            PreparedStatement statement,
            Livro livro,
            int indiceInicial) throws SQLException {
        statement.setString(indiceInicial, livro.titulo());
        statement.setString(indiceInicial + 1, livro.situacao().name());
        statement.setString(indiceInicial + 2, livro.reservadoPara());
        if (livro.dataDevolucao() == null) {
            statement.setNull(indiceInicial + 3, java.sql.Types.DATE);
        } else {
            statement.setDate(indiceInicial + 3, java.sql.Date.valueOf(livro.dataDevolucao()));
        }
    }

    private static Livro mapear(ResultSet resultado) throws SQLException {
        java.sql.Date data = resultado.getDate("data_devolucao");
        return Livro.reconstituir(
                resultado.getString("id"),
                resultado.getString("titulo"),
                br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro.valueOf(
                        resultado.getString("situacao")),
                resultado.getString("reservado_para"),
                data == null ? null : data.toLocalDate());
    }
}
