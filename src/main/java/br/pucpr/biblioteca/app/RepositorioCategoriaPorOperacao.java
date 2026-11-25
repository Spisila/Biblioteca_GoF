package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.biblioteca.acervo.composite.UnidadeAcervo;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioCategoria;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.util.Objects;
import java.util.function.Function;

/**
 * Adapta as categorias ao ciclo transacional de cada operação da aplicação.
 */
final class RepositorioCategoriaPorOperacao implements RepositorioCategoria {
    private final FabricaPersistencia fabrica;

    RepositorioCategoriaPorOperacao(FabricaPersistencia fabrica) {
        this.fabrica = Objects.requireNonNull(fabrica);
    }

    @Override
    public void carregar(UnidadeAcervo unidadeAcervo) {
        executar(repositorio -> {
            repositorio.carregar(unidadeAcervo);
            return null;
        });
    }

    @Override
    public void salvarCategoria(String id, String nome, String idPai) {
        executar(repositorio -> {
            repositorio.salvarCategoria(id, nome, idPai);
            return null;
        });
    }

    @Override
    public void associarLivro(String idLivro, String idCategoria) {
        executar(repositorio -> {
            repositorio.associarLivro(idLivro, idCategoria);
            return null;
        });
    }

    private <T> T executar(Function<RepositorioCategoria, T> operacao) {
        UnidadeDeTrabalho unidade = fabrica.criarUnidadeDeTrabalho();
        RepositorioCategoria repositorio = fabrica.criarRepositorioCategoria(unidade);
        try {
            T resultado = operacao.apply(repositorio);
            unidade.commit();
            return resultado;
        } catch (RuntimeException excecao) {
            unidade.rollback();
            throw excecao;
        } finally {
            unidade.close();
        }
    }
}
