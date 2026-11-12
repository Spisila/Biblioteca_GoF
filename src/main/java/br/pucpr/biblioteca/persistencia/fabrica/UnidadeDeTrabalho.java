package br.pucpr.biblioteca.persistencia.fabrica;

/**
 * Unidade transacional independente da família de persistência.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto abstrato.</p>
 */
public interface UnidadeDeTrabalho extends AutoCloseable {
    void commit();

    void rollback();

    @Override
    void close();
}
