package br.pucpr.biblioteca.financeiro.cobranca.bancossimulados;

import java.math.BigDecimal;

public final class BancoBSimulado {
    private String ultimoDocumento;
    private BigDecimal ultimoValorReais;
    private String ultimaReferencia;

    public String emitir(String documento, BigDecimal valorReais, String identificadorInstitucional) {
        ultimoDocumento = documento;
        ultimoValorReais = valorReais;
        ultimaReferencia = "B/" + identificadorInstitucional;
        return ultimaReferencia;
    }

    public NotificacaoBancoB notificarPagamento() {
        return new NotificacaoBancoB(ultimaReferencia, ultimoValorReais, "PAGAMENTO_CONFIRMADO");
    }

    public String ultimoDocumento() {
        return ultimoDocumento;
    }

    public BigDecimal ultimoValorReais() {
        return ultimoValorReais;
    }

    public String ultimaReferencia() {
        return ultimaReferencia;
    }
}
