package br.pucpr.biblioteca.financeiro.multa;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import java.util.Objects;

/**
 * Registro imutável do cálculo de uma multa.
 */
public final class Multa {
    private final int diasAtraso;
    private final Dinheiro valor;
    private final PoliticaMulta politicaAplicada;

    private Multa(int diasAtraso, Dinheiro valor, PoliticaMulta politicaAplicada) {
        this.diasAtraso = diasAtraso;
        this.valor = valor;
        this.politicaAplicada = politicaAplicada;
    }

    public static Multa emitir(int diasAtraso, PoliticaMulta politica) {
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("Dias de atraso nao podem ser negativos");
        }
        PoliticaMulta politicaAplicada = Objects.requireNonNull(politica, "politica nao pode ser nula");
        return new Multa(diasAtraso, politicaAplicada.calcular(diasAtraso), politicaAplicada);
    }

    public int diasAtraso() {
        return diasAtraso;
    }

    public Dinheiro valor() {
        return valor;
    }

    public PoliticaMulta politicaAplicada() {
        return politicaAplicada;
    }
}
