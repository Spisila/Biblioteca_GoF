package br.pucpr.biblioteca.financeiro.cobranca.bancossimulados;

import java.math.BigDecimal;

public record NotificacaoBancoB(
        String referenciaB,
        BigDecimal valorReais,
        String eventoB) {
}
