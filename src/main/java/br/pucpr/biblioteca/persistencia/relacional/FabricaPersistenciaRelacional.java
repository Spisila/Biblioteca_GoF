package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioMovimentacao;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioRelacionamento;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;

/**
 * Esqueleto da família de persistência relacional.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Fábrica concreta.</p>
 */
public final class FabricaPersistenciaRelacional implements FabricaPersistencia {
    private final String url;
    private final String usuario;
    private final String senha;

    public FabricaPersistenciaRelacional() {
        this.url = null;
        this.usuario = null;
        this.senha = null;
    }

    public FabricaPersistenciaRelacional(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    @Override
    public UnidadeDeTrabalhoRelacional criarUnidadeDeTrabalho() {
        return url == null
                ? new UnidadeDeTrabalhoRelacional()
                : new UnidadeDeTrabalhoRelacional(url, usuario, senha);
    }

    @Override
    public RepositorioLivroRelacional criarRepositorioLivro(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioLivroRelacional(verificar(unidadeDeTrabalho));
    }

    @Override
    public RepositorioMovimentacaoRelacional criarRepositorioMovimentacao(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioMovimentacaoRelacional(verificar(unidadeDeTrabalho));
    }

    @Override
    public RepositorioRelacionamentoRelacional criarRepositorioRelacionamento(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioRelacionamentoRelacional(verificar(unidadeDeTrabalho));
    }

    private UnidadeDeTrabalhoRelacional verificar(UnidadeDeTrabalho unidadeDeTrabalho) {
        if (!(unidadeDeTrabalho instanceof UnidadeDeTrabalhoRelacional)) {
            throw new IllegalArgumentException("Unidade de trabalho incompatível com a família relacional");
        }
        return (UnidadeDeTrabalhoRelacional) unidadeDeTrabalho;
    }
}
