package br.pucpr.biblioteca.financeiro.multa;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;

/**
 * Cobra um real por dia, limitado a vinte reais.
 *
 * <p>Padrão: R02 Strategy — papel: Estratégia concreta.</p>
 */
public final class PoliticaComTeto implements PoliticaMulta {
    private static final Dinheiro TETO = Dinheiro.ofReais(20, 0);
    private final PoliticaMulta politicaOriginal = new PoliticaOriginal();

    @Override
    public Dinheiro calcular(int diasAtraso) {
        return politicaOriginal.calcular(diasAtraso).minimo(TETO);
    }
}
