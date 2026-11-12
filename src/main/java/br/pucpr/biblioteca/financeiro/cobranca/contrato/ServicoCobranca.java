package br.pucpr.biblioteca.financeiro.cobranca.contrato;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import java.util.Objects;

/**
 * Serviço que depende apenas do contrato institucional de cobrança.
 */
public final class ServicoCobranca {
    private final Cobranca cobranca;

    public ServicoCobranca(Cobranca cobranca) {
        this.cobranca = Objects.requireNonNull(cobranca);
    }

    public CobrancaEmitida emitir(String idCobranca, String documento, Dinheiro valor) {
        return cobranca.emitir(idCobranca, documento, valor);
    }

    public boolean registrarPagamento(NotificacaoPagamento notificacao) {
        return cobranca.registrarPagamento(notificacao);
    }

    public SituacaoCobranca situacao(String idCobranca) {
        return cobranca.situacao(idCobranca);
    }
}
