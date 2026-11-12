package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;

/**
 * Estado final de um livro incinerado.
 *
 * <p>Padrão: R01 State — papel: Estado concreto.</p>
 */
public final class EstadoIncinerado extends EstadoLivroBase {
    @Override
    protected SituacaoLivro situacaoAtual() {
        return SituacaoLivro.INCINERADO;
    }
}
