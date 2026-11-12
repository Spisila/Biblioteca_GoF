package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import java.util.Objects;

/**
 * Traduz solicitações de empréstimo e renovação para a fachada de circulação.
 */
public final class TratadorDeEmprestimo {
    private final FachadaCirculacao fachada;

    public TratadorDeEmprestimo(FachadaCirculacao fachada) {
        this.fachada = Objects.requireNonNull(fachada);
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
}
