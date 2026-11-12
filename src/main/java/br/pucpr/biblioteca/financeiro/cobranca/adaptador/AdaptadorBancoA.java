package br.pucpr.biblioteca.financeiro.cobranca.adaptador;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoASimulado;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.NotificacaoBancoA;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.CobrancaEmitida;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.NotificacaoPagamento;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.SituacaoPagamento;

/**
 * Adapta a API do banco A, que trabalha com centavos inteiros.
 *
 * <p>Padrão: R03 Adapter — papel: Adaptador concreto.</p>
 */
public final class AdaptadorBancoA extends AdaptadorCobrancaBase {
    private final BancoASimulado banco;

    public AdaptadorBancoA(BancoASimulado banco) {
        this.banco = banco;
    }

    @Override
    public CobrancaEmitida emitir(String idCobranca, String documento, Dinheiro valor) {
        banco.emitir(documento, valor.centavos(), idCobranca);
        return registrarEmissao(idCobranca, documento, valor);
    }

    public boolean receberNotificacao(NotificacaoBancoA notificacao) {
        if (!"LIQUIDADO".equals(notificacao.statusA())) {
            return false;
        }
        String idCobranca = notificacao.identificadorA().substring(2);
        return registrarPagamento(new NotificacaoPagamento(
                notificacao.codigoNotificacao(),
                idCobranca,
                Dinheiro.ofCentavos(notificacao.centavos()),
                SituacaoPagamento.PAGO));
    }
}
