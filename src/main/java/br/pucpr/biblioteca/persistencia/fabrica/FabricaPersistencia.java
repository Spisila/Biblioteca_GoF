package br.pucpr.biblioteca.persistencia.fabrica;

/**
 * Cria uma família coerente de produtos de persistência.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Fábrica abstrata.</p>
 */
public interface FabricaPersistencia {
    UnidadeDeTrabalho criarUnidadeDeTrabalho();

    RepositorioLivro criarRepositorioLivro(UnidadeDeTrabalho unidadeDeTrabalho);

    RepositorioMovimentacao criarRepositorioMovimentacao(UnidadeDeTrabalho unidadeDeTrabalho);

    RepositorioRelacionamento criarRepositorioRelacionamento(UnidadeDeTrabalho unidadeDeTrabalho);
}
