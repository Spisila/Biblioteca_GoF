package br.pucpr.biblioteca.persistencia.fabrica;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import java.util.List;
import java.util.Optional;

/**
 * Repositório abstrato de livros.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto abstrato.</p>
 */
public interface RepositorioLivro {
    void salvar(Livro livro);

    Optional<Livro> buscarPorId(String id);

    List<Livro> listar();

    void remover(String id);
}
