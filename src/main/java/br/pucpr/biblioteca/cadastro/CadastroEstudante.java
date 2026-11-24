package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.comum.excecoes.PreRequisitoNaoAtendidoException;
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

    public CadastroEstudante(RepositorioCadastro<T> repositorio, Predicate<T> preRequisito) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.preRequisito = Objects.requireNonNull(preRequisito);
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
            throw new IllegalArgumentException("Dados do estudante nao podem ser nulos");
        }
    }

    @Override
    protected void verificarPreRequisitos(T dados) {
        if (!preRequisito.test(dados)) {
            throw new PreRequisitoNaoAtendidoException("Pre-requisito do estudante nao foi atendido");
        }
    }

    @Override
    protected T criarRegistro(T dados) {
        return dados;
    }

    @Override
    protected T armazenar(T registro) {
        return repositorio.salvar(registro);
    }
}
