package br.pucpr.biblioteca.financeiro.cobranca.bancossimulados;

public record NotificacaoBancoA(
        String codigoNotificacao,
        String identificadorA,
        long centavos,
        String statusA) {
}
