package br.pucpr.biblioteca.financeiro.cobranca.adaptador;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoBSimulado;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.NotificacaoBancoB;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.CobrancaEmitida;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.NotificacaoPagamento;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.SituacaoPagamento;
import java.math.BigDecimal;

/**
 * Adapta a API do banco B, que trabalha com valores em reais.
 *
 * <p>Padrão: R03 Adapter — papel: Adaptador concreto.</p>
 */
public final class AdaptadorBancoB extends AdaptadorCobrancaBase {
    private final BancoBSimulado banco;

    public AdaptadorBancoB(BancoBSimulado banco) {
        this.banco = banco;
    }

    @Override
    public CobrancaEmitida emitir(String idCobranca, String documento, Dinheiro valor) {
        banco.emitir(documento, BigDecimal.valueOf(valor.centavos(), 2), idCobranca);
        return registrarEmissao(idCobranca, documento, valor);
    }

    public boolean receberNotificacao(NotificacaoBancoB notificacao) {
        if (!"PAGAMENTO_CONFIRMADO".equals(notificacao.eventoB())) {
            return false;
        }
        String idCobranca = notificacao.referenciaB().substring(2);
        Dinheiro valor = Dinheiro.ofCentavos(notificacao.valorReais().movePointRight(2).longValueExact());
        return registrarPagamento(new NotificacaoPagamento(
                "NOT-B-" + notificacao.referenciaB(),
                idCobranca,
                valor,
                SituacaoPagamento.PAGO));
    }
}
