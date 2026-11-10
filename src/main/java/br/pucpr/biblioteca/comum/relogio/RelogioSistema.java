package br.pucpr.biblioteca.comum.relogio;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class RelogioSistema implements Relogio {
    private final Clock clock;

    public RelogioSistema() {
        this(Clock.systemDefaultZone());
    }

    RelogioSistema(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public LocalDate hoje() {
        return LocalDate.now(clock);
    }

    @Override
    public Instant agora() {
        return Instant.now(clock);
    }
}
