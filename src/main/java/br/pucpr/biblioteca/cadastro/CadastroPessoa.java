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
        if (dados == null) {
            throw new IllegalArgumentException("Dados da pessoa nao podem ser nulos");
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
