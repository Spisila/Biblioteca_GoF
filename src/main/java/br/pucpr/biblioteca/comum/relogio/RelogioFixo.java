package br.pucpr.biblioteca.comum.relogio;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class RelogioFixo implements Relogio {
    private LocalDate hoje;
    private Instant agora;

    public RelogioFixo(LocalDate hoje, Instant agora) {
        this.hoje = Objects.requireNonNull(hoje);
        this.agora = Objects.requireNonNull(agora);
    }

    @Override
    public LocalDate hoje() {
        return hoje;
    }

    @Override
    public Instant agora() {
        return agora;
    }

    public void avancarDias(int dias) {
        if (dias < 0) {
            throw new IllegalArgumentException("Dias nao pode ser negativo");
        }
        hoje = hoje.plusDays(dias);
        agora = agora.plusSeconds(Math.multiplyExact((long) dias, 86_400));
    }

    public void definirHoje(LocalDate hoje) {
        this.hoje = Objects.requireNonNull(hoje);
    }
}
