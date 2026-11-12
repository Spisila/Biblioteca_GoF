package br.pucpr.biblioteca.devolucao.ouvintes;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.ResultadoInspecao;
import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;
import br.pucpr.biblioteca.devolucao.publicador.OuvinteDevolucao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registra exemplares devolvidos para inspeção posterior.
 *
 * <p>Padrão: R05 Observer — papel: Observador concreto.</p>
 */
public final class OuvinteInspecaoExemplar implements OuvinteDevolucao {
    private final List<Livro> pendentes = new ArrayList<>();

    @Override
    public void aoReceber(EventoDevolucao evento) {
        pendentes.add(evento.livro());
    }

    public List<Livro> pendentes() {
        return Collections.unmodifiableList(new ArrayList<>(pendentes));
    }

    public void inspecionar(Livro livro, ResultadoInspecao resultado) {
        if (!pendentes.remove(livro)) {
            throw new IllegalArgumentException("Livro nao esta pendente de inspeção");
        }
        livro.inspecionarLivro(resultado);
    }
}
