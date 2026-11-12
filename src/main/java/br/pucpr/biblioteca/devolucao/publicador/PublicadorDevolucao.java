package br.pucpr.biblioteca.devolucao.publicador;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.comum.excecoes.OperacaoRecusadaException;
import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Publica devoluções confirmadas para ouvintes independentes.
 *
 * <p>Padrão: R05 Observer — papel: Sujeito concreto.</p>
 */
public final class PublicadorDevolucao {
    private final List<OuvinteDevolucao> ouvintes = new CopyOnWriteArrayList<>();
    private final List<RuntimeException> falhasDosOuvintes = new CopyOnWriteArrayList<>();

    public void adicionar(OuvinteDevolucao ouvinte) {
        ouvintes.add(Objects.requireNonNull(ouvinte, "ouvinte nao pode ser nulo"));
    }

    public void remover(OuvinteDevolucao ouvinte) {
        ouvintes.remove(ouvinte);
    }

    public List<RuntimeException> publicar(EventoDevolucao evento) {
        Objects.requireNonNull(evento, "evento nao pode ser nulo");
        if (evento.livro().situacao() != br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro.DEVOLVIDO) {
            throw new OperacaoRecusadaException("Devolucao deve ser confirmada antes da publicação");
        }
        List<RuntimeException> falhas = new ArrayList<>();
        for (OuvinteDevolucao ouvinte : ouvintes) {
            try {
                ouvinte.aoReceber(evento);
            } catch (RuntimeException falha) {
                falhas.add(falha);
                falhasDosOuvintes.add(falha);
            }
        }
        return Collections.unmodifiableList(falhas);
    }

    public List<RuntimeException> falhasDosOuvintes() {
        return Collections.unmodifiableList(new ArrayList<>(falhasDosOuvintes));
    }

    public EventoDevolucao confirmarDevolucao(
            Livro livro,
            Emprestimo emprestimo,
            LocalDate dataDevolucao) {
        livro.registrarDevolucao(emprestimo, dataDevolucao);
        EventoDevolucao evento = new EventoDevolucao(
                livro,
                emprestimo.idEstudante(),
                emprestimo.dataVencimento(),
                dataDevolucao);
        publicar(evento);
        return evento;
    }
}
