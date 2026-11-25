package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.cadastro.RepositorioCadastro;
import br.pucpr.biblioteca.comum.excecoes.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.ui.RegistroConsole;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
    public List<T> listar() {
        if (tipo == Estudante.class) {
            return listarEstudantes().stream().map(tipo::cast).toList();
        }
        if (tipo == Atendente.class) {
            return listarAtendentes().stream().map(tipo::cast).toList();
        }
        if (tipo == Pessoa.class) {
            return listarPessoas().stream().map(tipo::cast).toList();
        }
        throw new IllegalArgumentException("Tipo de cadastro nao suportado: " + tipo.getName());
    }

    @Override
    public Optional<T> buscarPorCpf(String cpf) {
        if (tipo == Estudante.class) {
            return buscarEstudantePorCpf(cpf).map(tipo::cast);
        }
        if (tipo == Atendente.class) {
            return buscarAtendentePorCpf(cpf).map(tipo::cast);
        }
        if (tipo == Pessoa.class) {
            return buscarPessoaPorCpf(cpf).map(tipo::cast);
        }
        return Optional.empty();
    }

    @Override
    public T salvar(T registro) {
        if (registro instanceof Estudante estudante) {
            return tipo.cast(salvarEstudante(estudante));
        }
        if (registro instanceof Atendente atendente) {
            return tipo.cast(salvarAtendente(atendente));
        }
        if (registro instanceof Pessoa pessoa) {
            return tipo.cast(salvarPessoa(pessoa));
        }
        if (registro instanceof RegistroConsole registroConsole) {
            return tipo.cast(salvarRegistroConsole(registroConsole));
        }
        throw new IllegalArgumentException(
                "Tipo de cadastro nao suportado: " + registro.getClass().getName());
    }

    private Optional<Pessoa> buscarPessoa(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome, cpf, email, data_nascimento FROM pessoas WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                return Optional.of(pessoa(resultado));
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar pessoa", excecao);
        }
    }

    private List<Pessoa> listarPessoas() {
        List<Pessoa> pessoas = new ArrayList<>();
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome, cpf, email, data_nascimento FROM pessoas ORDER BY id");
                ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                pessoas.add(pessoa(resultado));
            }
            return pessoas;
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel listar pessoas", excecao);
        }
    }

    private Optional<Pessoa> buscarPessoaPorCpf(String cpf) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome, cpf, email, data_nascimento FROM pessoas WHERE cpf = ?")) {
            statement.setString(1, cpf);
            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                Pessoa pessoa = pessoa(resultado);
                if (resultado.next()) {
                    throw new IllegalStateException("CPF duplicado encontrado: " + cpf);
                }
                return Optional.of(pessoa);
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar pessoa por CPF", excecao);
        }
    }

    private Optional<Estudante> buscarEstudante(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                        + "e.numero_matricula, e.curso, e.limite_emprestimos "
                        + "FROM estudantes e JOIN pessoas p ON p.id = e.id WHERE e.id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? Optional.of(estudante(resultado)) : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar estudante", excecao);
        }
    }

    private List<Estudante> listarEstudantes() {
        String sql = "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                + "e.numero_matricula, e.curso, e.limite_emprestimos "
                + "FROM estudantes e JOIN pessoas p ON p.id = e.id ORDER BY p.id";
        List<Estudante> estudantes = new ArrayList<>();
        try (PreparedStatement statement = unidade.conexao().prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                estudantes.add(estudante(resultado));
            }
            return estudantes;
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel listar estudantes", excecao);
        }
    }

    private Optional<Estudante> buscarEstudantePorCpf(String cpf) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                        + "e.numero_matricula, e.curso, e.limite_emprestimos "
                        + "FROM estudantes e JOIN pessoas p ON p.id = e.id WHERE p.cpf = ?")) {
            statement.setString(1, cpf);
            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                Estudante estudante = estudante(resultado);
                if (resultado.next()) {
                    throw new IllegalStateException("Mais de um estudante usa o CPF: " + cpf);
                }
                return Optional.of(estudante);
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar estudante por CPF", excecao);
        }
    }

    private Optional<Atendente> buscarAtendente(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                        + "a.turno, a.registro_funcional "
                        + "FROM atendentes a JOIN pessoas p ON p.id = a.id WHERE a.id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? Optional.of(atendente(resultado)) : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar atendente", excecao);
        }
    }

    private List<Atendente> listarAtendentes() {
        String sql = "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                + "a.turno, a.registro_funcional "
                + "FROM atendentes a JOIN pessoas p ON p.id = a.id ORDER BY p.id";
        List<Atendente> atendentes = new ArrayList<>();
        try (PreparedStatement statement = unidade.conexao().prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                atendentes.add(atendente(resultado));
            }
            return atendentes;
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel listar atendentes", excecao);
        }
    }

    private Optional<Atendente> buscarAtendentePorCpf(String cpf) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT p.id, p.nome, p.cpf, p.email, p.data_nascimento, "
                        + "a.turno, a.registro_funcional "
                        + "FROM atendentes a JOIN pessoas p ON p.id = a.id WHERE p.cpf = ?")) {
            statement.setString(1, cpf);
            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                Atendente atendente = atendente(resultado);
                if (resultado.next()) {
                    throw new IllegalStateException("Mais de um atendente usa o CPF: " + cpf);
                }
                return Optional.of(atendente);
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar atendente por CPF", excecao);
        }
    }

    private Optional<RegistroConsole> buscarRegistroConsole(String id) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "SELECT id, nome FROM pessoas WHERE id = ?")) {
            statement.setString(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next()
                        ? Optional.of(new RegistroConsole(
                                resultado.getString("id"), resultado.getString("nome")))
                        : Optional.empty();
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha("Nao foi possivel buscar registro", excecao);
        }
    }

    private Pessoa salvarPessoa(Pessoa pessoa) {
        if (pessoa.id().isBlank()) {
            String sql = "INSERT INTO pessoas (nome, cpf, email, data_nascimento) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = unidade.conexao().prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
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
                "UPDATE pessoas SET nome = ?, cpf = ?, email = ?, data_nascimento = ? WHERE id = ?")) {
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
        Pessoa pessoa = buscarPessoaPorCpf(estudante.cpf())
                .orElseThrow(() -> new PreRequisitoNaoAtendidoException(
                        "Pessoa com CPF " + estudante.cpf() + " nao esta cadastrada"));
        return inserirEstudante(pessoa, estudante);
    }

    private Estudante inserirEstudante(Pessoa pessoa, Estudante estudante) {
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO estudantes (id, numero_matricula, curso, limite_emprestimos) "
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
        Pessoa pessoa = buscarPessoaPorCpf(atendente.cpf())
                .orElseThrow(() -> new PreRequisitoNaoAtendidoException(
                        "Pessoa com CPF " + atendente.cpf() + " nao esta cadastrada"));
        try (PreparedStatement statement = unidade.conexao().prepareStatement(
                "INSERT INTO atendentes (id, turno, registro_funcional) "
                        + "VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE turno = VALUES(turno), "
                        + "registro_funcional = VALUES(registro_funcional)")) {
            statement.setString(1, pessoa.id());
            statement.setString(2, atendente.turno());
            statement.setString(3, atendente.registroFuncional());
            statement.executeUpdate();
            return new Atendente(
                    pessoa.id(), pessoa.nome(), pessoa.cpf(), pessoa.email(),
                    atendente.turno(), atendente.registroFuncional(), pessoa.dataNascimento());
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

    private static Pessoa pessoa(ResultSet resultado) throws SQLException {
        return new Pessoa(
                resultado.getString("id"),
                resultado.getString("nome"),
                resultado.getString("cpf"),
                resultado.getString("email"),
                resultado.getDate("data_nascimento").toLocalDate());
    }

    private static Estudante estudante(ResultSet resultado) throws SQLException {
        return new Estudante(
                resultado.getString("id"),
                resultado.getString("nome"),
                resultado.getString("cpf"),
                resultado.getString("email"),
                resultado.getString("numero_matricula"),
                resultado.getString("curso"),
                resultado.getInt("limite_emprestimos"),
                resultado.getDate("data_nascimento").toLocalDate());
    }

    private static Atendente atendente(ResultSet resultado) throws SQLException {
        return new Atendente(
                resultado.getString("id"),
                resultado.getString("nome"),
                resultado.getString("cpf"),
                resultado.getString("email"),
                resultado.getString("turno"),
                resultado.getString("registro_funcional"),
                resultado.getDate("data_nascimento").toLocalDate());
    }
}
