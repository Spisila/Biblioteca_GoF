package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;

/**
 * Unidade de trabalho reservada para persistência relacional.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class UnidadeDeTrabalhoRelacional implements UnidadeDeTrabalho {
    @Override
    public void commit() {
        indisponivel();
    }

    @Override
    public void rollback() {
        indisponivel();
    }

    @Override
    public void close() {
        indisponivel();
    }

    private void indisponivel() {
        throw new UnsupportedOperationException("implementar no passo de persistência relacional");
    }
}
