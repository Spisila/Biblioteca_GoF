package br.pucpr.biblioteca.financeiro.cobranca.contrato;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import java.util.Objects;

public record NotificacaoPagamento(
        String idNotificacao,
        String idCobranca,
        Dinheiro valor,
        SituacaoPagamento situacao) {
    public NotificacaoPagamento {
        if (idNotificacao == null || idNotificacao.isBlank()) {
            throw new IllegalArgumentException("idNotificacao nao pode ser vazio");
        }
        if (idCobranca == null || idCobranca.isBlank()) {
            throw new IllegalArgumentException("idCobranca nao pode ser vazio");
        }
        Objects.requireNonNull(valor, "valor nao pode ser nulo");
        Objects.requireNonNull(situacao, "situacao nao pode ser nula");
    }
}
