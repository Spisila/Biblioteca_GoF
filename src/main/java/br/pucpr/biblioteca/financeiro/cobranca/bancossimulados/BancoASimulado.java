package br.pucpr.biblioteca.financeiro.cobranca.bancossimulados;

public final class BancoASimulado {
    private String ultimoDocumento;
    private long ultimosCentavos;
    private String ultimoIdentificador;

    public String emitir(String documento, long centavos, String identificadorInstitucional) {
        ultimoDocumento = documento;
        ultimosCentavos = centavos;
        ultimoIdentificador = "A-" + identificadorInstitucional;
        return ultimoIdentificador;
    }

    public NotificacaoBancoA notificarPagamento() {
        return new NotificacaoBancoA(
                "NOT-A-" + ultimoIdentificador,
                ultimoIdentificador,
                ultimosCentavos,
                "LIQUIDADO");
    }

    public String ultimoDocumento() {
        return ultimoDocumento;
    }

    public long ultimosCentavos() {
        return ultimosCentavos;
    }

    public String ultimoIdentificador() {
        return ultimoIdentificador;
    }
}
