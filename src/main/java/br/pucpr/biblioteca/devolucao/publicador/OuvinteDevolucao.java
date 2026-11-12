package br.pucpr.biblioteca.devolucao.publicador;

import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;

/**
 * Contrato para reações independentes a uma devolução.
 *
 * <p>Padrão: R05 Observer — papel: Observador.</p>
 */
@FunctionalInterface
public interface OuvinteDevolucao {
    void aoReceber(EventoDevolucao evento);
}
