package br.pucpr.biblioteca.persistencia.fabrica;

import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import java.util.List;
import java.util.Optional;

/**
 * Repositório abstrato de relacionamentos.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto abstrato.</p>
 */
public interface RepositorioRelacionamento {
    void salvarReserva(Reserva reserva);

    List<Reserva> listarReservas();

    Optional<Reserva> buscarReservaAtiva(String idLivro);

    void removerReserva(String idLivro);
}
