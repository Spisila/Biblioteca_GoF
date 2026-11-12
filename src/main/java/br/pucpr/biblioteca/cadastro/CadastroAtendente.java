package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.comum.excecoes.PreRequisitoNaoAtendidoException;
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

    public CadastroAtendente(RepositorioCadastro<T> repositorio, Predicate<T> preRequisito) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.preRequisito = Objects.requireNonNull(preRequisito);
    }

    @Override
    protected Optional<T> buscarExistente(T dados) {
        return repositorio.buscarPorId(dados.id());
    }

    @Override
    protected void validarDados(T dados) {
        if (dados.id() == null || dados.id().isBlank()) {
            throw new IllegalArgumentException("Registro deve possuir id");
        }
    }

    @Override
    protected void verificarPreRequisitos(T dados) {
        if (!preRequisito.test(dados)) {
            throw new PreRequisitoNaoAtendidoException("Pre-requisito do atendente nao foi atendido");
        }
    }

    @Override
    protected T criarRegistro(T dados) {
        return dados;
    }

    @Override
    protected void armazenar(T registro) {
        repositorio.salvar(registro);
    }
}
