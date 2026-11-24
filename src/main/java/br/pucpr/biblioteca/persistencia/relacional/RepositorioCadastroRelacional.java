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
import java.sql.Statement;
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
    public T salvar(T registro) {
        if (registro instanceof Estudante) {
            return (T) salvarEstudante((Estudante) registro);
        }
        if (registro instanceof Atendente) {
            return (T) salvarAtendente((Atendente) registro);
        }
        if (registro instanceof Pessoa) {
            return (T) salvarPessoa((Pessoa) registro);
        }
        if (registro instanceof RegistroConsole) {
            return (T) salvarRegistroConsole((RegistroConsole) registro);
        }
        throw new IllegalArgumentException(
                "Tipo de cadastro nao suportado: " + registro.getClass().getName());
    }

    private Optional<Pessoa> buscarPessoa(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome, cpf, email, data_nascimento FROM pessoas WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(new Pessoa(
                        resultado.getString("id"),
                        resultado.getString("nome"),
                        resultado.getString("cpf"),
                        resultado.getString("email"),
                        resultado.getDate("data_nascimento").toLocalDate()))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar pessoa", excecao);
        }
    }

    private Optional<Estudante> buscarEstudante(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                        + "e.numero_matricula, e.curso, e.limite_emprestimos "
                        + "FROM estudantes e JOIN pessoas p ON p.id = e.id "
                        + "WHERE e.id = ?")) {
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
                        resultado.getInt("limite_emprestimos"),
                        resultado.getDate("data_nascimento").toLocalDate()))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar estudante", excecao);
        }
    }

    private Optional<Atendente> buscarAtendente(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                        + "a.turno, a.registro_funcional "
                        + "FROM atendentes a JOIN pessoas p ON p.id = a.id "
                        + "WHERE a.id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(new Atendente(
                        resultado.getString("id"),
                        resultado.getString("nome"),
                        resultado.getString("cpf"),
                        resultado.getString("email"),
                        resultado.getString("turno"),
                        resultado.getString("registro_funcional"),
                        resultado.getDate("data_nascimento").toLocalDate()))
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

    private Pessoa salvarPessoa(Pessoa pessoa) {
        if (pessoa.id().isBlank()) {
            String inserir = "INSERT INTO pessoas (nome, cpf, email, data_nascimento) "
                    + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = unidade.conexao().prepareStatement(
                    inserir, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, pessoa.nome());
                statement.setString(2, pessoa.cpf());
                statement.setString(3, pessoa.email());
                statement.setDate(4, java.sql.Date.valueOf(pessoa.dataNascimento()));
                statement.executeUpdate();
                try (ResultSet chaves = statement.getGeneratedKeys()) {
                    if (!chaves.next()) {
                        throw new SQLException("Banco nao retornou o ID gerado da pessoa");
                    }
                    return new Pessoa(
                            chaves.getString(1), pessoa.nome(), pessoa.cpf(),
                            pessoa.email(), pessoa.dataNascimento());
                }
            } catch (SQLException excecao) {
                throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar pessoa", excecao);
            }
        }
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "UPDATE pessoas SET nome = ?, cpf = ?, email = ?, data_nascimento = ? "
                        + "WHERE id = ?")) {
            statement.setString(1, pessoa.nome());
            statement.setString(2, pessoa.cpf());
            statement.setString(3, pessoa.email());
            statement.setDate(4, java.sql.Date.valueOf(pessoa.dataNascimento()));
            statement.setString(5, pessoa.id());
            statement.executeUpdate();
            return pessoa;
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar pessoa", excecao);
        }
    }

    private Estudante salvarEstudante(Estudante estudante) {
        Pessoa pessoa = salvarPessoa(new Pessoa(
                estudante.id(), estudante.nome(), estudante.cpf(),
                estudante.email(), estudante.dataNascimento()));
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO estudantes "
                        + "(id, numero_matricula, curso, limite_emprestimos) "
                        + "VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE numero_matricula = VALUES(numero_matricula), "
                        + "curso = VALUES(curso), limite_emprestimos = VALUES(limite_emprestimos)")) {
            statement.setString(1, pessoa.id());
            statement.setString(2, estudante.numeroMatricula());
            statement.setString(3, estudante.curso());
            statement.setInt(4, estudante.limiteEmprestimos());
            statement.executeUpdate();
            return new Estudante(
                    pessoa.id(), pessoa.nome(), pessoa.cpf(), pessoa.email(),
                    estudante.numeroMatricula(), estudante.curso(),
                    estudante.limiteEmprestimos(), pessoa.dataNascimento());
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar estudante", excecao);
        }
    }

    private Atendente salvarAtendente(Atendente atendente) {
        Pessoa pessoa = salvarPessoa(new Pessoa(
                atendente.id(), atendente.nome(), atendente.cpf(),
                atendente.email(), atendente.dataNascimento()));
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO atendentes "
                        + "(id, turno, registro_funcional) "
                        + "VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE turno = VALUES(turno), "
                        + "registro_funcional = VALUES(registro_funcional)")) {
            statement.setString(1, pessoa.id());
            statement.setString(2, atendente.turno());
            statement.setString(3, atendente.registroFuncional());
            statement.executeUpdate();
            return new Atendente(
                    pessoa.id(), pessoa.nome(), pessoa.cpf(), pessoa.email(),
                    atendente.turno(), atendente.registroFuncional(),
                    pessoa.dataNascimento());
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar atendente", excecao);
        }
    }

    private RegistroConsole salvarRegistroConsole(RegistroConsole registro) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO pessoas (nome, cpf, email, data_nascimento) "
                        + "VALUES (?, '', '', '1900-01-01')",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, registro.descricao());
            statement.executeUpdate();
            try (ResultSet chaves = statement.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("Banco nao retornou o ID gerado");
                }
                return new RegistroConsole(chaves.getString(1), registro.descricao());
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel salvar registro", excecao);
        }
    }
}
