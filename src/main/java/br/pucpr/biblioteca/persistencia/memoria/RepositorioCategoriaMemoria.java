package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.biblioteca.acervo.composite.GrupoAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.ItemAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.SecaoAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.UnidadeAcervo;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioCategoria;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Repositório transacional de categorias em memória.
 */
public final class RepositorioCategoriaMemoria implements RepositorioCategoria {
    private final UnidadeDeTrabalhoMemoria unidadeDeTrabalho;

    public RepositorioCategoriaMemoria(UnidadeDeTrabalhoMemoria unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    @Override
    public void carregar(UnidadeAcervo unidadeAcervo) {
        Map<String, SecaoAcervo> grupos = new HashMap<>();
        for (ArmazenamentoMemoria.CategoriaPersistida categoria
                : unidadeDeTrabalho.categorias().values()) {
            if (unidadeAcervo.buscarGrupo(categoria.id()).isPresent()) {
                throw new IllegalStateException(
                        "Id de categoria duplicado na hierarquia: " + categoria.id());
            }
            grupos.put(categoria.id(), new SecaoAcervo(categoria.id(), categoria.nome()));
        }

        Set<String> anexados = new HashSet<>();
        while (anexados.size() < grupos.size()) {
            boolean progresso = false;
            for (ArmazenamentoMemoria.CategoriaPersistida categoria
                    : unidadeDeTrabalho.categorias().values()) {
                if (anexados.contains(categoria.id())) {
                    continue;
                }
                GrupoAcervo pai;
                if (categoria.idPai() == null) {
                    pai = unidadeAcervo;
                } else if (anexados.contains(categoria.idPai())) {
                    pai = grupos.get(categoria.idPai());
                } else {
                    continue;
                }
                pai.adicionar(grupos.get(categoria.id()));
                anexados.add(categoria.id());
                progresso = true;
            }
            if (!progresso) {
                throw new IllegalStateException("Hierarquia de categorias invalida.");
            }
        }

        for (Map.Entry<String, String> associacao
                : unidadeDeTrabalho.categoriaPorLivro().entrySet()) {
            GrupoAcervo categoria = unidadeAcervo.buscarGrupo(associacao.getValue())
                    .orElseThrow(() -> new IllegalStateException(
                            "Categoria associada nao encontrada: " + associacao.getValue()));
            categoria.adicionar(new ItemAcervo(
                    "ITEM-" + associacao.getKey(), associacao.getKey()));
        }
    }

    @Override
    public void salvarCategoria(String id, String nome, String idPai) {
        Map<String, ArmazenamentoMemoria.CategoriaPersistida> categorias =
                unidadeDeTrabalho.categorias();
        if (categorias.containsKey(id)) {
            throw new IllegalArgumentException("Ja existe uma categoria com o id: " + id);
        }
        if (idPai != null && !categorias.containsKey(idPai)) {
            throw new IllegalArgumentException("Categoria pai nao encontrada: " + idPai);
        }
        categorias.put(id, new ArmazenamentoMemoria.CategoriaPersistida(id, nome, idPai));
    }

    @Override
    public void associarLivro(String idLivro, String idCategoria) {
        if (!unidadeDeTrabalho.livros().containsKey(idLivro)) {
            throw new IllegalArgumentException("Livro nao encontrado: " + idLivro);
        }
        if (!unidadeDeTrabalho.categorias().containsKey(idCategoria)) {
            throw new IllegalArgumentException("Categoria nao encontrada: " + idCategoria);
        }
        if (unidadeDeTrabalho.categoriaPorLivro().containsKey(idLivro)) {
            throw new IllegalArgumentException("O livro ja pertence a uma categoria: " + idLivro);
        }
        unidadeDeTrabalho.categoriaPorLivro().put(idLivro, idCategoria);
    }
}
