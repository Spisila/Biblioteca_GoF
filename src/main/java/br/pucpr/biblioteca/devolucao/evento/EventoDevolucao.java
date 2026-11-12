package br.pucpr.biblioteca.devolucao.evento;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Dados imutáveis publicados após a confirmação de uma devolução.
 */
public final class EventoDevolucao {
    private final Livro livro;
    private final String idEstudante;
    private final LocalDate dataVencimento;
    private final LocalDate dataDevolucao;

    public EventoDevolucao(
            Livro livro,
            String idEstudante,
            LocalDate dataVencimento,
            LocalDate dataDevolucao) {
        this.livro = Objects.requireNonNull(livro, "livro nao pode ser nulo");
        this.idEstudante = validarTexto(idEstudante, "idEstudante");
        this.dataVencimento = Objects.requireNonNull(dataVencimento, "dataVencimento nao pode ser nula");
        this.dataDevolucao = Objects.requireNonNull(dataDevolucao, "dataDevolucao nao pode ser nula");
    }

    public Livro livro() {
        return livro;
    }

    public String idLivro() {
        return livro.id();
    }

    public String idEstudante() {
        return idEstudante;
    }

    public LocalDate dataVencimento() {
        return dataVencimento;
    }

    public LocalDate dataDevolucao() {
        return dataDevolucao;
    }

    public long diasDeAtraso() {
        if (!dataDevolucao.isAfter(dataVencimento)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataVencimento, dataDevolucao);
    }

    private static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
