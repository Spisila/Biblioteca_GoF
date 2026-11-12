package br.pucpr.biblioteca.biblioteca.movimentacao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class Emprestimo extends Transacao {
    public static final int PRAZO_DIAS = 30;
    public static final int DIAS_POR_RENOVACAO = 30;
    public static final int LIMITE_RENOVACOES = 2;

    private final String idLivro;
    private final String idEstudante;
    private LocalDate dataVencimento;
    private int numRenovacoes;

    public Emprestimo(
            String idTransacao,
            String idLivro,
            String idEstudante,
            LocalDate dataEmprestimo) {
        super(idTransacao, dataEmprestimo);
        this.idLivro = validarTexto(idLivro, "idLivro");
        this.idEstudante = validarTexto(idEstudante, "idEstudante");
        this.dataVencimento = dataEmprestimo.plusDays(PRAZO_DIAS);
    }

    public static Emprestimo reconstituir(
            String idTransacao,
            String idLivro,
            String idEstudante,
            LocalDate dataEmprestimo,
            LocalDate dataVencimento,
            int numRenovacoes) {
        if (numRenovacoes < 0) {
            throw new IllegalArgumentException("numRenovacoes nao pode ser negativo");
        }
        Emprestimo emprestimo = new Emprestimo(idTransacao, idLivro, idEstudante, dataEmprestimo);
        emprestimo.dataVencimento = Objects.requireNonNull(dataVencimento, "dataVencimento nao pode ser nula");
        emprestimo.numRenovacoes = numRenovacoes;
        return emprestimo;
    }

    public void renovar() {
        numRenovacoes++;
        dataVencimento = dataVencimento.plusDays(DIAS_POR_RENOVACAO);
    }

    public long diasDeAtraso(LocalDate dataDevolucao) {
        Objects.requireNonNull(dataDevolucao, "dataDevolucao nao pode ser nula");
        if (!dataDevolucao.isAfter(dataVencimento)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataVencimento, dataDevolucao);
    }

    public String idLivro() {
        return idLivro;
    }

    public String idEstudante() {
        return idEstudante;
    }

    public LocalDate dataVencimento() {
        return dataVencimento;
    }

    public int numRenovacoes() {
        return numRenovacoes;
    }
}
