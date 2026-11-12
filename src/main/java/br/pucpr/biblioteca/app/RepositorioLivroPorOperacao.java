package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Adapta a unidade de trabalho da aplicação ao ciclo de vida do template de livros.
 */
final class RepositorioLivroPorOperacao implements RepositorioLivro {
    private final FabricaPersistencia fabrica;

    RepositorioLivroPorOperacao(FabricaPersistencia fabrica) {
        this.fabrica = Objects.requireNonNull(fabrica);
    }

    @Override
    public void salvar(Livro livro) {
        executar(repositorio -> {
            repositorio.salvar(livro);
            return null;
        });
    }

    @Override
    public Optional<Livro> buscarPorId(String id) {
        return executar(repositorio -> repositorio.buscarPorId(id));
    }

    @Override
    public List<Livro> listar() {
        return executar(RepositorioLivro::listar);
    }

    @Override
    public void remover(String id) {
        executar(repositorio -> {
            repositorio.remover(id);
            return null;
        });
    }

    private <T> T executar(Function<RepositorioLivro, T> operacao) {
        UnidadeDeTrabalho unidade = fabrica.criarUnidadeDeTrabalho();
        RepositorioLivro repositorio = fabrica.criarRepositorioLivro(unidade);
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
