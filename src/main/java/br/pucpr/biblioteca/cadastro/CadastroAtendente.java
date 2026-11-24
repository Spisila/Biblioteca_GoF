package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.comum.excecoes.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Implementa o fluxo de inclusão de atendentes com pré-requisito configurável.
 *
 * <p>Padrão: R07 Template Method — papel: Subclasse concreta.</p>
 */
public final class CadastroAtendente<T extends RegistroCadastravel> extends TemplateCadastro<T> {
    private final RepositorioCadastro<T> repositorio;
    private final Predicate<T> preRequisito;
    private final RepositorioCadastro<? extends RegistroCadastravel> pessoas;

    public CadastroAtendente(RepositorioCadastro<T> repositorio, Predicate<T> preRequisito) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.preRequisito = Objects.requireNonNull(preRequisito);
        this.pessoas = null;
    }

    public CadastroAtendente(
            RepositorioCadastro<T> repositorio,
            RepositorioCadastro<? extends RegistroCadastravel> pessoas) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.pessoas = Objects.requireNonNull(pessoas);
        this.preRequisito = null;
    }

    public Optional<T> consultarPorId(String id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    protected Optional<T> buscarExistente(T dados) {
        return repositorio.buscarPorId(dados.id());
    }

    @Override
    protected void validarDados(T dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados do atendente nao podem ser nulos");
        }
    }

    @Override
    protected void verificarPreRequisitos(T dados) {
        if (pessoas != null) {
            Pessoa pessoa = (Pessoa) dados;
            if (pessoas.buscarPorCpf(pessoa.cpf()).isEmpty()) {
                throw new PreRequisitoNaoAtendidoException(
                        "Pessoa com CPF " + pessoa.cpf() + " nao esta cadastrada");
            }
            return;
        }
        if (preRequisito != null && !preRequisito.test(dados)) {
            throw new PreRequisitoNaoAtendidoException("Pre-requisito do atendente nao foi atendido");
        }
    }

    @Override
    protected T criarRegistro(T dados) {
        if (pessoas != null && dados instanceof Atendente atendente) {
            Pessoa pessoa = (Pessoa) pessoas.buscarPorCpf(atendente.cpf()).orElseThrow();
            return (T) new Atendente(
                    pessoa.id(),
                    pessoa.nome(),
                    pessoa.cpf(),
                    pessoa.email(),
                    atendente.turno(),
                    atendente.registroFuncional(),
                    pessoa.dataNascimento());
        }
        return dados;
    }

    @Override
    protected T armazenar(T registro) {
        return repositorio.salvar(registro);
    }
}
