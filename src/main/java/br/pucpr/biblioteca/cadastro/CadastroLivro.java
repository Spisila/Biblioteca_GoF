package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.util.Optional;

/**
 * Implementa o fluxo de inclusão de livros.
 *
 * <p>Padrão: R07 Template Method — papel: Subclasse concreta.</p>
 */
public final class CadastroLivro extends TemplateCadastro<Livro> {
    private final RepositorioLivro repositorio;

    public CadastroLivro(RepositorioLivro repositorio) {
        this.repositorio = repositorio;
    }

    public CadastroLivro(FabricaPersistencia fabrica, UnidadeDeTrabalho unidadeDeTrabalho) {
        this(fabrica.criarRepositorioLivro(unidadeDeTrabalho));
    }

    @Override
    protected Optional<Livro> buscarExistente(Livro dados) {
        return repositorio.buscarPorId(dados.id());
    }

    @Override
    protected void validarDados(Livro dados) {
        if (dados.id().isBlank() || dados.titulo().isBlank()) {
            throw new IllegalArgumentException("Livro deve possuir id e titulo");
        }
    }

    @Override
    protected Livro criarRegistro(Livro dados) {
        return new Livro(dados.id(), dados.titulo());
    }

    @Override
    protected void armazenar(Livro registro) {
        repositorio.salvar(registro);
    }
}
