package br.pucpr.biblioteca.devolucao.ouvintes;

import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;
import br.pucpr.biblioteca.devolucao.publicador.OuvinteDevolucao;
import java.util.Objects;

/**
 * Encaminha a devolução para o serviço de e-mail.
 *
 * <p>Padrão: R05 Observer — papel: Observador concreto.</p>
 */
public final class OuvinteEmail implements OuvinteDevolucao {
    private final EnvioEmail envio;

    public OuvinteEmail(EnvioEmail envio) {
        this.envio = Objects.requireNonNull(envio, "envio nao pode ser nulo");
    }

    @Override
    public void aoReceber(EventoDevolucao evento) {
        envio.enviar(evento);
    }
}
