package br.pucpr.biblioteca.devolucao.ouvintes;

import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;
import br.pucpr.biblioteca.devolucao.publicador.OuvinteDevolucao;

/**
 * Atualiza indicadores estatísticos de devoluções.
 *
 * <p>Padrão: R05 Observer — papel: Observador concreto.</p>
 */
public final class OuvinteIndicadores implements OuvinteDevolucao {
    private int totalDevolucoes;
    private int totalDevolucoesAtrasadas;

    @Override
    public void aoReceber(EventoDevolucao evento) {
        totalDevolucoes++;
        if (evento.diasDeAtraso() > 0) {
            totalDevolucoesAtrasadas++;
        }
    }

    public int totalDevolucoes() {
        return totalDevolucoes;
    }

    public int totalDevolucoesAtrasadas() {
        return totalDevolucoesAtrasadas;
    }
}
