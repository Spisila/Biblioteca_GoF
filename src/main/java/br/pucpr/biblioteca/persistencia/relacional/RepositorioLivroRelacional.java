package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.util.List;
import java.util.Optional;

/**
 * Esqueleto do repositório relacional de livros.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioLivroRelacional implements RepositorioLivro {
    private final UnidadeDeTrabalhoRelacional unidadeDeTrabalho;

    public RepositorioLivroRelacional(UnidadeDeTrabalhoRelacional unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    public UnidadeDeTrabalhoRelacional unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvar(Livro livro) {
        indisponivel();
    }

    @Override
    public Optional<Livro> buscarPorId(String id) {
        indisponivel();
        return Optional.empty();
    }

    @Override
    public List<Livro> listar() {
        indisponivel();
        return List.of();
    }

    @Override
    public void remover(String id) {
        indisponivel();
    }

    private void indisponivel() {
        throw new UnsupportedOperationException("implementar no passo de persistência relacional");
    }
}
