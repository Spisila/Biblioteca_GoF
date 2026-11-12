package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de livros em memória.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioLivroMemoria implements RepositorioLivro {
    private final UnidadeDeTrabalhoMemoria unidadeDeTrabalho;

    public RepositorioLivroMemoria(UnidadeDeTrabalhoMemoria unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    @Override
    public void salvar(Livro livro) {
        unidadeDeTrabalho.livros().put(livro.id(), ArmazenamentoMemoria.copiarLivro(livro));
    }

    @Override
    public Optional<Livro> buscarPorId(String id) {
        return Optional.ofNullable(unidadeDeTrabalho.livros().get(id))
                .map(ArmazenamentoMemoria::copiarLivro);
    }

    @Override
    public List<Livro> listar() {
        List<Livro> copia = new ArrayList<>();
        unidadeDeTrabalho.livros().values().forEach(livro -> copia.add(ArmazenamentoMemoria.copiarLivro(livro)));
        return copia;
    }

    @Override
    public void remover(String id) {
        unidadeDeTrabalho.livros().remove(id);
    }
}
