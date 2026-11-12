package br.pucpr.biblioteca.circulacao.comando;

import java.time.Instant;

/**
 * Solicitação executável de circulação.
 *
 * <p>Padrão: R06 Command — papel: Comando.</p>
 */
public interface Comando<T> {
    String idComando();

    String autor();

    Instant instanteCriacao();

    T executar();
}
