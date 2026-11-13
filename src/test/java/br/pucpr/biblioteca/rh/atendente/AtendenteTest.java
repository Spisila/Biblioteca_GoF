package br.pucpr.biblioteca.rh.atendente;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AtendenteTest {
    @Test
    void preservaDadosEspecificosDoAtendente() {
        Atendente atendente = new Atendente(
                "a1", "Ana", "123", "ana@example.com", "manha", "RF-1");

        assertEquals("a1", atendente.id());
        assertEquals("manha", atendente.turno());
        assertEquals("RF-1", atendente.registroFuncional());
    }
}
