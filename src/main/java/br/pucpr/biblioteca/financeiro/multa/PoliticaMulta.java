package br.pucpr.biblioteca.financeiro.multa;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;

/**
 * Contrato para políticas de cálculo de multa.
 *
 * <p>Padrão: R02 Strategy — papel: Estratégia abstrata.</p>
 */
public interface PoliticaMulta {
    Dinheiro calcular(int diasAtraso);
}
