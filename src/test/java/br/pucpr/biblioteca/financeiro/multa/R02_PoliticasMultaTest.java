package br.pucpr.biblioteca.financeiro.multa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import org.junit.jupiter.api.Test;

class R02_PoliticasMultaTest {
    @Test
    void R02_politicaOriginalCobraUmRealPorDia() {
        assertEquals(Dinheiro.ofReais(5, 0), new PoliticaOriginal().calcular(5));
    }

    @Test
    void R02_politicaComTetoLimitaEmVinteReais() {
        assertEquals(Dinheiro.ofReais(20, 0), new PoliticaComTeto().calcular(25));
        assertEquals(Dinheiro.ofReais(5, 0), new PoliticaComTeto().calcular(5));
    }

    @Test
    void R02_politicaComToleranciaCobraTodosOsDiasAposDoisDias() {
        PoliticaMulta politica = new PoliticaComTolerancia();

        assertEquals(Dinheiro.ZERO, politica.calcular(0));
        assertEquals(Dinheiro.ZERO, politica.calcular(2));
        assertEquals(Dinheiro.ofReais(3, 0), politica.calcular(5));
    }

    @Test
    void R02_politicaVigentePodeSerConfiguradaAdministrativamente() {
        EmissorMulta emissor = new EmissorMulta();
        PoliticaMulta tolerancia = new PoliticaComTolerancia();

        emissor.definirPolitica(tolerancia);

        assertSame(tolerancia, emissor.politicaVigente());
        assertEquals(Dinheiro.ofReais(3, 0), emissor.calcular(5));
    }

    @Test
    void R02_emissaoRegistraValorEPoliticaDoMomento() {
        EmissorMulta emissor = new EmissorMulta(new PoliticaOriginal());

        Multa multaOriginal = emissor.emitir(5);
        emissor.definirPolitica(new PoliticaComTeto());
        Multa multaPosterior = emissor.emitir(25);

        assertEquals(Dinheiro.ofReais(5, 0), multaOriginal.valor());
        assertEquals(Dinheiro.ofReais(20, 0), multaPosterior.valor());
        assertEquals(Dinheiro.ofReais(5, 0), multaOriginal.valor());
        assertTruePolitica(multaOriginal, PoliticaOriginal.class);
        assertTruePolitica(multaPosterior, PoliticaComTeto.class);
    }

    @Test
    void R02_diasNegativosSaoRecusados() {
        assertThrows(IllegalArgumentException.class, () -> new PoliticaOriginal().calcular(-1));
        assertThrows(IllegalArgumentException.class, () -> new PoliticaComTolerancia().calcular(-1));
        assertThrows(IllegalArgumentException.class, () -> Multa.emitir(-1, new PoliticaOriginal()));
    }

    private void assertTruePolitica(Multa multa, Class<?> tipo) {
        assertEquals(tipo, multa.politicaAplicada().getClass());
    }
}
