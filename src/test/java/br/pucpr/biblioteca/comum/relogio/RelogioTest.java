package br.pucpr.biblioteca.comum.relogio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class RelogioTest {
    @Test
    void relogioFixoAvancaDataEInstanteEmDias() {
        RelogioFixo relogio = new RelogioFixo(
                LocalDate.of(2026, 1, 10),
                Instant.parse("2026-01-10T12:00:00Z"));

        relogio.avancarDias(3);

        assertEquals(LocalDate.of(2026, 1, 13), relogio.hoje());
        assertEquals(Instant.parse("2026-01-13T12:00:00Z"), relogio.agora());
    }

    @Test
    void relogioFixoNaoAvancaSemComando() {
        LocalDate hoje = LocalDate.of(2026, 1, 10);
        Instant agora = Instant.parse("2026-01-10T12:00:00Z");
        RelogioFixo relogio = new RelogioFixo(hoje, agora);

        assertEquals(hoje, relogio.hoje());
        assertEquals(agora, relogio.agora());
    }

    @Test
    void definirHojeAlteraSomenteAData() {
        Instant agora = Instant.parse("2026-01-10T12:00:00Z");
        RelogioFixo relogio = new RelogioFixo(LocalDate.of(2026, 1, 10), agora);

        relogio.definirHoje(LocalDate.of(2026, 2, 1));

        assertEquals(LocalDate.of(2026, 2, 1), relogio.hoje());
        assertEquals(agora, relogio.agora());
    }
}
