package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Traduz solicitações de devolução para a fachada de circulação.
 */
public final class TratadorDeDevolucao {
    private final FachadaCirculacao fachada;

    public TratadorDeDevolucao(FachadaCirculacao fachada) {
        this.fachada = Objects.requireNonNull(fachada);
    }

    public boolean registrar(String idLivro, String idTransacao, LocalDate dataDevolucao) {
        try {
            fachada.registrarDevolucao(idLivro, idTransacao, dataDevolucao);
            return true;
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return false;
        }
    }

    public boolean registrar(String idLivro, String idTransacao) {
        try {
            fachada.registrarDevolucao(idLivro, idTransacao);
            return true;
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return false;
        }
    }
}
