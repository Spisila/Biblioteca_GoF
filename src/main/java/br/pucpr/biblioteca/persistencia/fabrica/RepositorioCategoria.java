package br.pucpr.biblioteca.persistencia.fabrica;

import br.pucpr.biblioteca.biblioteca.acervo.composite.UnidadeAcervo;

/**
 * Persistência da hierarquia de categorias e suas associações com livros.
 */
public interface RepositorioCategoria {
    void carregar(UnidadeAcervo unidadeAcervo);

    void salvarCategoria(String id, String nome, String idPai);

    void associarLivro(String idLivro, String idCategoria);
}
