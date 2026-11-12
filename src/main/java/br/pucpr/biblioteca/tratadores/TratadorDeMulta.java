package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.financeiro.multa.EmissorMulta;
import br.pucpr.biblioteca.financeiro.multa.Multa;
import br.pucpr.biblioteca.financeiro.multa.PoliticaMulta;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import java.util.Objects;

/**
 * Traduz solicitações de cálculo, emissão e configuração de multas.
 */
public final class TratadorDeMulta {
    private final EmissorMulta emissor;

    public TratadorDeMulta(EmissorMulta emissor) {
        this.emissor = Objects.requireNonNull(emissor);
    }

    public String emitir(int diasAtraso) {
        try {
            Multa multa = emissor.emitir(diasAtraso);
            return "Multa emitida: " + multa.valor();
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return "Nao foi possivel emitir a multa: " + excecao.getMessage();
        }
    }

    public boolean definirPolitica(PoliticaMulta politica) {
        try {
            emissor.definirPolitica(politica);
            return true;
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return false;
        }
    }
}
