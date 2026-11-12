package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioRelacionamento;
import java.util.Optional;

/**
 * Repositório de relacionamentos em memória.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioRelacionamentoMemoria implements RepositorioRelacionamento {
    private final UnidadeDeTrabalhoMemoria unidadeDeTrabalho;

    public RepositorioRelacionamentoMemoria(UnidadeDeTrabalhoMemoria unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    @Override
    public void salvarReserva(Reserva reserva) {
        unidadeDeTrabalho.reservas().put(reserva.idLivro(), ArmazenamentoMemoria.copiarReserva(reserva));
    }

    @Override
    public Optional<Reserva> buscarReservaAtiva(String idLivro) {
        return Optional.ofNullable(unidadeDeTrabalho.reservas().get(idLivro))
                .map(ArmazenamentoMemoria::copiarReserva);
    }

    @Override
    public void removerReserva(String idLivro) {
        unidadeDeTrabalho.reservas().remove(idLivro);
    }
}
