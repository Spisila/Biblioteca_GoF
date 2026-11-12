package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioRelacionamento;
import java.util.Optional;

/**
 * Esqueleto do repositório relacional de relacionamentos.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioRelacionamentoRelacional implements RepositorioRelacionamento {
    private final UnidadeDeTrabalhoRelacional unidadeDeTrabalho;

    public RepositorioRelacionamentoRelacional(UnidadeDeTrabalhoRelacional unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    public UnidadeDeTrabalhoRelacional unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvarReserva(Reserva reserva) {
        indisponivel();
    }

    @Override
    public Optional<Reserva> buscarReservaAtiva(String idLivro) {
        indisponivel();
        return Optional.empty();
    }

    @Override
    public void removerReserva(String idLivro) {
        indisponivel();
    }

    private void indisponivel() {
        throw new UnsupportedOperationException("implementar no passo de persistência relacional");
    }
}
