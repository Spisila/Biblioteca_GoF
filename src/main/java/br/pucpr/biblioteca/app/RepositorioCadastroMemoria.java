package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.cadastro.RepositorioCadastro;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Repositório simples para os registros ainda não modelados no domínio.
 */
final class RepositorioCadastroMemoria<T extends RegistroCadastravel>
        implements RepositorioCadastro<T> {
    private final ConcurrentMap<String, T> registros = new ConcurrentHashMap<>();

    @Override
    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(registros.get(id));
    }

    @Override
    public void salvar(T registro) {
        registros.put(registro.id(), registro);
    }
}
