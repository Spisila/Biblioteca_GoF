package br.pucpr.biblioteca.comum.dinheiro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DinheiroTest {
    @Test
    void somaValoresEmCentavos() {
        assertEquals(Dinheiro.ofCentavos(375), Dinheiro.ofCentavos(125).somar(Dinheiro.ofCentavos(250)));
    }

    @Test
    void subtraiValoresSemPermitirResultadoNegativo() {
        assertEquals(Dinheiro.ofCentavos(75), Dinheiro.ofCentavos(200).subtrair(Dinheiro.ofCentavos(125)));
        assertThrows(IllegalArgumentException.class,
                () -> Dinheiro.ofCentavos(100).subtrair(Dinheiro.ofCentavos(101)));
    }

    @Test
    void multiplicaPorFatorInteiro() {
        assertEquals(Dinheiro.ofCentavos(600), Dinheiro.ofCentavos(200).multiplicar(3));
    }

    @Test
    void comparaValoresEEncontraMinimoEMaximo() {
        Dinheiro menor = Dinheiro.ofCentavos(99);
        Dinheiro maior = Dinheiro.ofCentavos(100);

        assertTrue(menor.compareTo(maior) < 0);
        assertEquals(menor, menor.minimo(maior));
        assertEquals(maior, menor.maximo(maior));
    }

    @Test
    void comparaIgualdadePeloValor() {
        Dinheiro primeiro = Dinheiro.ofCentavos(500);
        Dinheiro segundo = Dinheiro.ofReais(5, 0);

        assertEquals(primeiro, segundo);
        assertEquals(primeiro.hashCode(), segundo.hashCode());
        assertFalse(primeiro.ehZero());
        assertTrue(Dinheiro.ZERO.ehZero());
    }

    @Test
    void rejeitaValoresNegativos() {
        assertThrows(IllegalArgumentException.class, () -> Dinheiro.ofCentavos(-1));
        assertThrows(IllegalArgumentException.class, () -> Dinheiro.ofReais(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> Dinheiro.ofReais(1, -1));
    }

    @Test
    void formataValorComDuasCasasDecimais() {
        assertEquals("R$ 5,00", Dinheiro.ofReais(5, 0).toString());
        assertEquals("R$ 0,07", Dinheiro.ofCentavos(7).toString());
    }
}
