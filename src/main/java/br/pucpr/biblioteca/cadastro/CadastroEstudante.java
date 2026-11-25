package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.comum.excecoes.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.academico.estudante.Estudante;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Implementa o fluxo de inclusão de estudantes com pré-requisito configurável.
 *
 * <p>Padrão: R07 Template Method — papel: Subclasse concreta.</p>
 */
public final class CadastroEstudante<T extends RegistroCadastravel> extends TemplateCadastro<T> {
    private final RepositorioCadastro<T> repositorio;
    private final Predicate<T> preRequisito;
    private final RepositorioCadastro<? extends RegistroCadastravel> pessoas;
    private final Class<T> tipo;

    public CadastroEstudante(RepositorioCadastro<T> repositorio, Predicate<T> preRequisito) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.preRequisito = Objects.requireNonNull(preRequisito);
        this.pessoas = null;
        this.tipo = null;
    }

    public CadastroEstudante(
            RepositorioCadastro<T> repositorio,
            RepositorioCadastro<? extends RegistroCadastravel> pessoas,
            Class<T> tipo) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.pessoas = Objects.requireNonNull(pessoas);
        this.preRequisito = null;
        this.tipo = Objects.requireNonNull(tipo);
    }

    public Optional<T> consultarPorId(String id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    protected Optional<T> buscarExistente(T dados) {
        Optional<T> porCpf = repositorio.buscarPorCpf(dados instanceof Pessoa pessoa
                ? pessoa.cpf()
                : null);
        if (porCpf.isPresent()) {
            return porCpf;
        }
        return repositorio.buscarPorId(dados.id());
    }

    @Override
    protected void validarDados(T dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados do estudante nao podem ser nulos");
        }
    }

    @Override
    protected void verificarPreRequisitos(T dados) {
        if (pessoas != null) {
            Pessoa pessoa = (Pessoa) dados;
            Optional<? extends RegistroCadastravel> associada =
                    pessoas.buscarPorCpf(pessoa.cpf());
            if (associada.isEmpty()) {
                throw new PreRequisitoNaoAtendidoException(
                        "Pessoa com CPF " + pessoa.cpf() + " nao esta cadastrada");
            }
            RegistroCadastravel pessoaEncontrada = associada.get();
            if (!(pessoaEncontrada instanceof Pessoa)) {
                throw new PreRequisitoNaoAtendidoException(
                        "Cadastro associado ao CPF nao representa uma Pessoa");
            }
            return;
        }
        if (preRequisito != null && !preRequisito.test(dados)) {
            throw new PreRequisitoNaoAtendidoException("Pre-requisito do estudante nao foi atendido");
        }
    }

    @Override
    protected T criarRegistro(T dados) {
        if (pessoas != null && dados instanceof Estudante estudante) {
            Pessoa pessoa = (Pessoa) pessoas.buscarPorCpf(estudante.cpf()).orElseThrow();
            return tipo.cast(new Estudante(
                    pessoa.id(),
                    pessoa.nome(),
                    pessoa.cpf(),
                    pessoa.email(),
                    estudante.numeroMatricula(),
                    estudante.curso(),
                    estudante.limiteEmprestimos(),
                    pessoa.dataNascimento()));
        }
        return dados;
    }

    @Override
    protected T armazenar(T registro) {
        return repositorio.salvar(registro);
    }
}
