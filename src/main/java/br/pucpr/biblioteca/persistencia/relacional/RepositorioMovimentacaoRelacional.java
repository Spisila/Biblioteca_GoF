package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.biblioteca.movimentacao.Transacao;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioMovimentacao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Esqueleto do repositório relacional de movimentações.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioMovimentacaoRelacional implements RepositorioMovimentacao {
    private final UnidadeDeTrabalhoRelacional unidadeDeTrabalho;

    public RepositorioMovimentacaoRelacional(UnidadeDeTrabalhoRelacional unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    public UnidadeDeTrabalhoRelacional unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvar(Transacao transacao) {
        if (!(transacao instanceof Emprestimo)) {
            throw new IllegalArgumentException("Tipo de transacao nao suportado: "
                    + transacao.getClass().getName());
        }
        Emprestimo emprestimo = (Emprestimo) transacao;
        String atualizar = "UPDATE movimentacoes SET id_livro = ?, id_estudante = ?, "
                + "data_transacao = ?, data_vencimento = ?, num_renovacoes = ? "
                + "WHERE id_transacao = ?";
        try (PreparedStatement statement = conexao().prepareStatement(atualizar)) {
            preencher(statement, emprestimo);
            statement.setString(6, emprestimo.idTransacao());
            if (statement.executeUpdate() == 0) {
                String inserir = "INSERT INTO movimentacoes "
                        + "(id_transacao, id_livro, id_estudante, data_transacao, "
                        + "data_vencimento, num_renovacoes) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insert = conexao().prepareStatement(inserir)) {
                    insert.setString(1, emprestimo.idTransacao());
                    preencher(insert, emprestimo, 2);
                    insert.executeUpdate();
                }
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel salvar a movimentacao", excecao);
        }
    }

    @Override
    public Optional<Transacao> buscarPorId(String id) {
        String sql = "SELECT id_transacao, id_livro, id_estudante, data_transacao, "
                + "data_vencimento, num_renovacoes FROM movimentacoes WHERE id_transacao = ?";
        try (PreparedStatement statement = conexao().prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(mapear(resultado))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel buscar a movimentacao", excecao);
        }
    }

    @Override
    public List<Transacao> listarPorLivro(String idLivro) {
        String sql = "SELECT id_transacao, id_livro, id_estudante, data_transacao, "
                + "data_vencimento, num_renovacoes FROM movimentacoes "
                + "WHERE id_livro = ? ORDER BY data_transacao, id_transacao";
        List<Transacao> movimentacoes = new ArrayList<>();
        try (PreparedStatement statement = conexao().prepareStatement(sql)) {
            statement.setString(1, idLivro);
            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    movimentacoes.add(mapear(resultado));
                }
            }
            return movimentacoes;
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel listar as movimentacoes", excecao);
        }
    }

    private Connection conexao() {
        return unidadeDeTrabalho.conexao();
    }

    private static void preencher(PreparedStatement statement, Emprestimo emprestimo)
            throws SQLException {
        preencher(statement, emprestimo, 1);
    }

    private static void preencher(
            PreparedStatement statement,
            Emprestimo emprestimo,
            int indiceInicial) throws SQLException {
        statement.setString(indiceInicial, emprestimo.idLivro());
        statement.setString(indiceInicial + 1, emprestimo.idEstudante());
        statement.setDate(indiceInicial + 2, java.sql.Date.valueOf(emprestimo.dataTransacao()));
        statement.setDate(indiceInicial + 3, java.sql.Date.valueOf(emprestimo.dataVencimento()));
        statement.setInt(indiceInicial + 4, emprestimo.numRenovacoes());
    }

    private static Emprestimo mapear(ResultSet resultado) throws SQLException {
        return Emprestimo.reconstituir(
                resultado.getString("id_transacao"),
                resultado.getString("id_livro"),
                resultado.getString("id_estudante"),
                resultado.getDate("data_transacao").toLocalDate(),
                resultado.getDate("data_vencimento").toLocalDate(),
                resultado.getInt("num_renovacoes"));
    }
}
