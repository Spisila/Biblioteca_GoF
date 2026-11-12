package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import br.pucpr.biblioteca.circulacao.comando.ComandoRenovacao;
import br.pucpr.biblioteca.circulacao.comando.ExecutorComando;
import java.time.Instant;
import java.util.Objects;

/**
 * Traduz solicitações de empréstimo e renovação para a fachada de circulação.
 */
public final class TratadorDeEmprestimo {
    private final FachadaCirculacao fachada;
    private final ExecutorComando executorComando;

    public TratadorDeEmprestimo(FachadaCirculacao fachada) {
        this(fachada, new ExecutorComando());
    }

    public TratadorDeEmprestimo(FachadaCirculacao fachada, ExecutorComando executorComando) {
        this.fachada = Objects.requireNonNull(fachada);
        this.executorComando = Objects.requireNonNull(executorComando);
    }

    public String reservar(String idReserva, String idLivro, String idEstudante) {
        try {
            fachada.solicitarReserva(idReserva, idLivro, idEstudante);
            return "Reserva solicitada: " + idReserva;
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return "Nao foi possivel solicitar a reserva: " + excecao.getMessage();
        }
    }

    public String realizar(String idTransacao, String idLivro, String idEstudante) {
        try {
            Emprestimo emprestimo = fachada.realizarEmprestimo(
                    idTransacao, idLivro, idEstudante);
            return "Empréstimo realizado: " + emprestimo.idTransacao();
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return "Nao foi possivel realizar o empréstimo: " + excecao.getMessage();
        }
    }

    public boolean renovar(String idLivro, String idTransacao) {
        try {
            fachada.registrarRenovacao(idLivro, idTransacao);
            return true;
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return false;
        }
    }

    public boolean renovarIdempotente(
            String idComando,
            String autor,
            String idLivro,
            String idTransacao,
            Instant instanteCriacao) {
        try {
            executorComando.executar(new ComandoRenovacao(
                    idComando, autor, instanteCriacao, fachada, idLivro, idTransacao));
            return true;
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return false;
        }
    }
}
