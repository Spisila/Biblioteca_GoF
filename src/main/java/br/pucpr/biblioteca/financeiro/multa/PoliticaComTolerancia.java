package br.pucpr.biblioteca.financeiro.multa;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;

/**
 * Isenta atrasos de até dois dias e cobra somente os dias além da tolerância.
 *
 * <p>Padrão: R02 Strategy — papel: Estratégia concreta.</p>
 */
public final class PoliticaComTolerancia implements PoliticaMulta {
    private static final int DIAS_DE_TOLERANCIA = 2;
    private final PoliticaMulta politicaOriginal = new PoliticaOriginal();

    @Override
    public Dinheiro calcular(int diasAtraso) {
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("Dias de atraso nao podem ser negativos");
        }
        if (diasAtraso <= DIAS_DE_TOLERANCIA) {
            return Dinheiro.ZERO;
        }
        return politicaOriginal.calcular(diasAtraso - DIAS_DE_TOLERANCIA);
    }
}
