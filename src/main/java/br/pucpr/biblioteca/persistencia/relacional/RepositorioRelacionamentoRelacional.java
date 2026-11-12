package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioRelacionamento;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Optional;

/**
 * Esqueleto do repositório relacional de relacionamentos.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioRelacionamentoRelacional implements RepositorioRelacionamento {
    private final UnidadeDeTrabalhoRelacional unidadeDeTrabalho;

    public RepositorioRelacionamentoRelacional(UnidadeDeTrabalhoRelacional unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    public UnidadeDeTrabalhoRelacional unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvarReserva(Reserva reserva) {
        String atualizar = "UPDATE reservas SET id = ?, id_estudante = ?, data_reserva = ? "
                + "WHERE id_livro = ?";
        try (PreparedStatement statement = conexao().prepareStatement(atualizar)) {
            statement.setString(1, reserva.id());
            statement.setString(2, reserva.idEstudante());
            statement.setDate(3, java.sql.Date.valueOf(reserva.dataReserva()));
            statement.setString(4, reserva.idLivro());
            if (statement.executeUpdate() == 0) {
                String inserir = "INSERT INTO reservas "
                        + "(id, id_livro, id_estudante, data_reserva) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insert = conexao().prepareStatement(inserir)) {
                    insert.setString(1, reserva.id());
                    insert.setString(2, reserva.idLivro());
                    insert.setString(3, reserva.idEstudante());
                    insert.setDate(4, java.sql.Date.valueOf(reserva.dataReserva()));
                    insert.executeUpdate();
                }
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar a reserva", excecao);
        }
    }

    @Override
    public Optional<Reserva> buscarReservaAtiva(String idLivro) {
        String sql = "SELECT id, id_livro, id_estudante, data_reserva "
                + "FROM reservas WHERE id_livro = ?";
        try (PreparedStatement statement = conexao().prepareStatement(sql)) {
            statement.setString(1, idLivro);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(mapear(resultado))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar a reserva", excecao);
        }
    }

    @Override
    public void removerReserva(String idLivro) {
        try (PreparedStatement statement = conexao().prepareStatement(
                "DELETE FROM reservas WHERE id_livro = ?")) {
            statement.setString(1, idLivro);
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel remover a reserva", excecao);
        }
    }

    private Connection conexao() {
        return unidadeDeTrabalho.conexao();
    }

    private static Reserva mapear(ResultSet resultado) throws SQLException {
        return new Reserva(
                resultado.getString("id"),
                resultado.getString("id_livro"),
                resultado.getString("id_estudante"),
                resultado.getDate("data_reserva").toLocalDate());
    }
}
