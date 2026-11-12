package br.pucpr.biblioteca.financeiro.cobranca.adaptador;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.Cobranca;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.CobrancaEmitida;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.NotificacaoPagamento;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.SituacaoCobranca;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.SituacaoPagamento;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Compartilha o controle institucional de emissão e quitação entre adaptadores.
 *
 * <p>Padrão: R03 Adapter — papel: Adaptador abstrato.</p>
 */
abstract class AdaptadorCobrancaBase implements Cobranca {
    private final Map<String, CobrancaEmitida> cobrancas = new HashMap<>();
    private final Set<String> notificacoesProcessadas = new HashSet<>();

    @Override
    public final boolean registrarPagamento(NotificacaoPagamento notificacao) {
        if (!notificacoesProcessadas.add(notificacao.idNotificacao())) {
            return false;
        }
        CobrancaEmitida cobranca = cobrancas.get(notificacao.idCobranca());
        if (cobranca == null || notificacao.situacao() != SituacaoPagamento.PAGO) {
            return false;
        }
        if (cobranca.situacao() == SituacaoCobranca.PAGA) {
            return false;
        }
        cobrancas.put(
                cobranca.idCobranca(),
                new CobrancaEmitida(
                        cobranca.idCobranca(),
                        cobranca.documento(),
                        cobranca.valor(),
                        SituacaoCobranca.PAGA));
        return true;
    }

    @Override
    public final SituacaoCobranca situacao(String idCobranca) {
        CobrancaEmitida cobranca = cobrancas.get(idCobranca);
        if (cobranca == null) {
            throw new IllegalArgumentException("Cobranca nao encontrada: " + idCobranca);
        }
        return cobranca.situacao();
    }

    protected final CobrancaEmitida registrarEmissao(
            String idCobranca,
            String documento,
            Dinheiro valor) {
        CobrancaEmitida cobranca = new CobrancaEmitida(
                idCobranca,
                documento,
                valor,
                SituacaoCobranca.EMITIDA);
        cobrancas.put(idCobranca, cobranca);
        return cobranca;
    }
}
