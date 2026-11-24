package br.pucpr.biblioteca.cadastro;

import java.util.Optional;

/**
 * Contrato mínimo usado pelos cadastros ainda não persistidos pelo R08.
 */
public interface RepositorioCadastro<T> {
    Optional<T> buscarPorId(String id);

    default Optional<T> buscarPorCpf(String cpf) {
        return Optional.empty();
    }

    T salvar(T registro);
}
