package br.pucpr.biblioteca.circulacao.comando;

import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import java.time.Instant;
import java.util.Objects;

/**
 * Solicita a renovação de um empréstimo.
 *
 * <p>Padrão: R06 Command — papel: Comando concreto.</p>
 */
public final class ComandoRenovacao implements Comando<Emprestimo> {
    private final String idComando;
    private final String autor;
    private final Instant instanteCriacao;
    private final FachadaCirculacao fachada;
    private final String idLivro;
    private final String idTransacao;

    public ComandoRenovacao(
            String idComando,
            String autor,
            Instant instanteCriacao,
            FachadaCirculacao fachada,
            String idLivro,
            String idTransacao) {
        this.idComando = validarTexto(idComando, "idComando");
        this.autor = validarTexto(autor, "autor");
        this.instanteCriacao = Objects.requireNonNull(
                instanteCriacao, "instanteCriacao nao pode ser nulo");
        this.fachada = Objects.requireNonNull(fachada, "fachada nao pode ser nula");
        this.idLivro = validarTexto(idLivro, "idLivro");
        this.idTransacao = validarTexto(idTransacao, "idTransacao");
    }

    @Override
    public String idComando() {
        return idComando;
    }

    @Override
    public String autor() {
        return autor;
    }

    @Override
    public Instant instanteCriacao() {
        return instanteCriacao;
    }

    @Override
    public Emprestimo executar() {
        return fachada.registrarRenovacao(idLivro, idTransacao);
    }

    private static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
