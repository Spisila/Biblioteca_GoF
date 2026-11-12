package br.pucpr.biblioteca.devolucao.ouvintes;

import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;
import br.pucpr.biblioteca.financeiro.multa.EmissorMulta;
import br.pucpr.biblioteca.financeiro.multa.Multa;
import br.pucpr.biblioteca.devolucao.publicador.OuvinteDevolucao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Calcula e registra multas para devoluções atrasadas.
 *
 * <p>Padrão: R05 Observer — papel: Observador concreto.</p>
 */
public final class OuvinteMulta implements OuvinteDevolucao {
    private final EmissorMulta emissor;
    private final List<Multa> multas = new ArrayList<>();

    public OuvinteMulta(EmissorMulta emissor) {
        this.emissor = Objects.requireNonNull(emissor, "emissor nao pode ser nulo");
    }

    @Override
    public void aoReceber(EventoDevolucao evento) {
        if (evento.diasDeAtraso() > 0) {
            multas.add(emissor.emitir(Math.toIntExact(evento.diasDeAtraso())));
        }
    }

    public List<Multa> multas() {
        return Collections.unmodifiableList(new ArrayList<>(multas));
    }
}
