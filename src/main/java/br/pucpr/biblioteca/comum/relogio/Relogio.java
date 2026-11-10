package br.pucpr.biblioteca.comum.relogio;

import java.time.Instant;
import java.time.LocalDate;

public interface Relogio {
    LocalDate hoje();

    Instant agora();
}
