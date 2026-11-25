package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Livro salvar(Livro livro) {
        String id = livro.id().isBlank() ? "L-" + UUID.randomUUID() : livro.id();
        Livro persistido = Livro.reconstituir(
                id, livro.titulo(), livro.situacao(),
                livro.reservadoPara(), livro.dataDevolucao());
        unidadeDeTrabalho.livros().put(id, ArmazenamentoMemoria.copiarLivro(persistido));
        return persistido;
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
        copia.sort((primeiro, segundo) -> primeiro.id().compareTo(segundo.id()));
        return copia;
    }

    @Override
    public void remover(String id) {
        unidadeDeTrabalho.livros().remove(id);
    }
}
