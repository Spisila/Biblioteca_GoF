package br.pucpr.biblioteca.financeiro.multa;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;

/**
 * Cobra um real por cada dia de atraso.
 *
 * <p>Padrão: R02 Strategy — papel: Estratégia concreta.</p>
 */
public final class PoliticaOriginal implements PoliticaMulta {
    private static final Dinheiro VALOR_POR_DIA = Dinheiro.ofReais(1, 0);

    @Override
    public Dinheiro calcular(int diasAtraso) {
        validarDias(diasAtraso);
        return VALOR_POR_DIA.multiplicar(diasAtraso);
    }

    private void validarDias(int diasAtraso) {
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("Dias de atraso nao podem ser negativos");
        }
    }
}
