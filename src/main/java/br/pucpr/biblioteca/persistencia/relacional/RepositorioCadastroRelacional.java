package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.cadastro.RepositorioCadastro;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.ui.RegistroConsole;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class RepositorioCadastroRelacional<T extends RegistroCadastravel>
        implements RepositorioCadastro<T> {
    private final UnidadeDeTrabalhoRelacional unidade;
    private final Class<T> tipo;

    public RepositorioCadastroRelacional(
            UnidadeDeTrabalhoRelacional unidade,
            Class<T> tipo) {
        this.unidade = unidade;
        this.tipo = tipo;
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        if (tipo == Estudante.class) {
            return buscarEstudante(id).map(tipo::cast);
        }
        if (tipo == Atendente.class) {
            return buscarAtendente(id).map(tipo::cast);
        }
        if (tipo == Pessoa.class) {
            return buscarPessoa(id).map(tipo::cast);
        }
        if (tipo == RegistroConsole.class) {
            return buscarRegistroConsole(id).map(tipo::cast);
        }
        throw new IllegalArgumentException("Tipo de cadastro nao suportado: " + tipo.getName());
    }

    @Override
    public void salvar(T registro) {
        if (registro instanceof Estudante) {
            salvarEstudante((Estudante) registro);
            return;
        }
        if (registro instanceof Atendente) {
            salvarAtendente((Atendente) registro);
            return;
        }
        if (registro instanceof Pessoa) {
            salvarPessoa((Pessoa) registro);
            return;
        }
        if (registro instanceof RegistroConsole) {
            salvarRegistroConsole((RegistroConsole) registro);
            return;
        }
        throw new IllegalArgumentException(
                "Tipo de cadastro nao suportado: " + registro.getClass().getName());
    }

    private Optional<Pessoa> buscarPessoa(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome, cpf, email FROM pessoas WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(new Pessoa(
                        resultado.getString("id"),
                        resultado.getString("nome"),
                        resultado.getString("cpf"),
                        resultado.getString("email")))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar pessoa", excecao);
        }
    }

    private Optional<Estudante> buscarEstudante(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome, cpf, email, numero_matricula, curso, limite_emprestimos "
                        + "FROM estudantes WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(new Estudante(
                        resultado.getString("id"),
                        resultado.getString("nome"),
                        resultado.getString("cpf"),
                        resultado.getString("email"),
                        resultado.getString("numero_matricula"),
                        resultado.getString("curso"),
                        resultado.getInt("limite_emprestimos")))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar estudante", excecao);
        }
    }

    private Optional<Atendente> buscarAtendente(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome, cpf, email, turno, registro_funcional "
                        + "FROM atendentes WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(new Atendente(
                        resultado.getString("id"),
                        resultado.getString("nome"),
                        resultado.getString("cpf"),
                        resultado.getString("email"),
                        resultado.getString("turno"),
                        resultado.getString("registro_funcional")))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar atendente", excecao);
        }
    }

    private Optional<RegistroConsole> buscarRegistroConsole(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome FROM pessoas WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(new RegistroConsole(
                        resultado.getString("id"),
                        resultado.getString("nome")))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar registro", excecao);
        }
    }

    private void salvarPessoa(Pessoa pessoa) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO pessoas (id, nome, cpf, email) VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), "
                        + "cpf = VALUES(cpf), email = VALUES(email)")) {
            statement.setString(1, pessoa.id());
            statement.setString(2, pessoa.nome());
            statement.setString(3, pessoa.cpf());
            statement.setString(4, pessoa.email());
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar pessoa", excecao);
        }
    }

    private void salvarEstudante(Estudante estudante) {
        salvarPessoa(new Pessoa(
                estudante.id(), estudante.nome(), estudante.cpf(), estudante.email()));
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO estudantes "
                        + "(id, nome, cpf, email, numero_matricula, curso, limite_emprestimos) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), cpf = VALUES(cpf), "
                        + "email = VALUES(email), numero_matricula = VALUES(numero_matricula), "
                        + "curso = VALUES(curso), limite_emprestimos = VALUES(limite_emprestimos)")) {
            statement.setString(1, estudante.id());
            statement.setString(2, estudante.nome());
            statement.setString(3, estudante.cpf());
            statement.setString(4, estudante.email());
            statement.setString(5, estudante.numeroMatricula());
            statement.setString(6, estudante.curso());
            statement.setInt(7, estudante.limiteEmprestimos());
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar estudante", excecao);
        }
    }

    private void salvarAtendente(Atendente atendente) {
        salvarPessoa(new Pessoa(
                atendente.id(), atendente.nome(), atendente.cpf(), atendente.email()));
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO atendentes "
                        + "(id, nome, cpf, email, turno, registro_funcional) "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), cpf = VALUES(cpf), "
                        + "email = VALUES(email), turno = VALUES(turno), "
                        + "registro_funcional = VALUES(registro_funcional)")) {
            statement.setString(1, atendente.id());
            statement.setString(2, atendente.nome());
            statement.setString(3, atendente.cpf());
            statement.setString(4, atendente.email());
            statement.setString(5, atendente.turno());
            statement.setString(6, atendente.registroFuncional());
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar atendente", excecao);
        }
    }

    private void salvarRegistroConsole(RegistroConsole registro) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO pessoas (id, nome, cpf, email) VALUES (?, ?, '', '') "
                        + "ON DUPLICATE KEY UPDATE nome = VALUES(nome)")) {
            statement.setString(1, registro.id());
            statement.setString(2, registro.descricao());
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar registro", excecao);
        }
    }
}
