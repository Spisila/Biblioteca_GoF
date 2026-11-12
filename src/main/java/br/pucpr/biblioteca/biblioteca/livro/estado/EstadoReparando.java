package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;

/**
 * Estado em que o livro está em reparo.
 *
 * <p>Padrão: R01 State — papel: Estado concreto.</p>
 */
public final class EstadoReparando extends EstadoLivroBase {
    @Override
    protected SituacaoLivro situacaoAtual() {
        return SituacaoLivro.REPARANDO;
    }

    @Override
    public void retornarDoReparo(Livro livro) {
        livro.transitarPara(EstadosLivro.de(SituacaoLivro.LIVRE));
    }

    @Override
    public void registrarLaudoIrrecuperavel(Livro livro) {
        livro.transitarPara(EstadosLivro.de(SituacaoLivro.INCINERADO));
    }
}
