package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioMovimentacao;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioRelacionamento;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;

/**
 * Cria a família de persistência em memória.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Fábrica concreta.</p>
 */
public final class FabricaPersistenciaMemoria implements FabricaPersistencia {
    private final ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();

    @Override
    public UnidadeDeTrabalhoMemoria criarUnidadeDeTrabalho() {
        return new UnidadeDeTrabalhoMemoria(armazenamento);
    }

    @Override
    public RepositorioLivro criarRepositorioLivro(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioLivroMemoria(verificar(unidadeDeTrabalho));
    }

    @Override
    public RepositorioMovimentacao criarRepositorioMovimentacao(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioMovimentacaoMemoria(verificar(unidadeDeTrabalho));
    }

    @Override
    public RepositorioRelacionamento criarRepositorioRelacionamento(UnidadeDeTrabalho unidadeDeTrabalho) {
        return new RepositorioRelacionamentoMemoria(verificar(unidadeDeTrabalho));
    }

    private UnidadeDeTrabalhoMemoria verificar(UnidadeDeTrabalho unidadeDeTrabalho) {
        if (!(unidadeDeTrabalho instanceof UnidadeDeTrabalhoMemoria)) {
            throw new IllegalArgumentException("Unidade de trabalho incompatível com a família em memória");
        }
        return (UnidadeDeTrabalhoMemoria) unidadeDeTrabalho;
    }
}
