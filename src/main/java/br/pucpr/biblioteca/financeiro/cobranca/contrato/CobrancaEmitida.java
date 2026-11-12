package br.pucpr.biblioteca.financeiro.cobranca.contrato;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import java.util.Objects;

public record CobrancaEmitida(
        String idCobranca,
        String documento,
        Dinheiro valor,
        SituacaoCobranca situacao) {
    public CobrancaEmitida {
        if (idCobranca == null || idCobranca.isBlank()) {
            throw new IllegalArgumentException("idCobranca nao pode ser vazio");
        }
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("documento nao pode ser vazio");
        }
        Objects.requireNonNull(valor, "valor nao pode ser nulo");
        Objects.requireNonNull(situacao, "situacao nao pode ser nula");
    }
}
