package br.pucpr.biblioteca.financeiro.multa;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import java.util.Objects;

/**
 * Mantém a política vigente e emite registros de multa.
 *
 * <p>Padrão: R02 Strategy — papel: Contexto.</p>
 */
public final class EmissorMulta {
    private PoliticaMulta politicaVigente;

    public EmissorMulta() {
        this(new PoliticaOriginal());
    }

    public EmissorMulta(PoliticaMulta politicaVigente) {
        this.politicaVigente = Objects.requireNonNull(politicaVigente, "politicaVigente nao pode ser nula");
    }

    public void definirPolitica(PoliticaMulta politicaVigente) {
        this.politicaVigente = Objects.requireNonNull(politicaVigente, "politicaVigente nao pode ser nula");
    }

    public PoliticaMulta politicaVigente() {
        return politicaVigente;
    }

    public Dinheiro calcular(int diasAtraso) {
        return politicaVigente.calcular(diasAtraso);
    }

    public Multa emitir(int diasAtraso) {
        return Multa.emitir(diasAtraso, politicaVigente);
    }
}
