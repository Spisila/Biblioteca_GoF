package br.pucpr.biblioteca.financeiro.cobranca.contrato;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;

/**
 * Contrato institucional de cobrança independente do banco externo.
 *
 * <p>Padrão: R03 Adapter — papel: Alvo.</p>
 */
public interface Cobranca {
    CobrancaEmitida emitir(String idCobranca, String documento, Dinheiro valor);

    boolean registrarPagamento(NotificacaoPagamento notificacao);

    SituacaoCobranca situacao(String idCobranca);
}
