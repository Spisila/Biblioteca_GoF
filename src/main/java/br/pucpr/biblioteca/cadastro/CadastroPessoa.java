package br.pucpr.biblioteca.cadastro;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementa o fluxo de inclusão de pessoas por meio de um registro fornecido
 * pela camada de domínio correspondente.
 *
 * <p>Padrão: R07 Template Method — papel: Subclasse concreta.</p>
 */
public final class CadastroPessoa<T extends RegistroCadastravel> extends TemplateCadastro<T> {
    private final RepositorioCadastro<T> repositorio;

    public CadastroPessoa(RepositorioCadastro<T> repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
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
        validarId(dados);
    }

    @Override
    protected T criarRegistro(T dados) {
        return dados;
    }

    @Override
    protected void armazenar(T registro) {
        repositorio.salvar(registro);
    }

    private void validarId(T dados) {
        if (dados.id() == null || dados.id().isBlank()) {
            throw new IllegalArgumentException("Registro deve possuir id");
        }
    }
}
