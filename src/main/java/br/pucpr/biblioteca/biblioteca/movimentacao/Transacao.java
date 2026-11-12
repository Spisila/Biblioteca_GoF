package br.pucpr.biblioteca.biblioteca.movimentacao;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Transacao {
    private final String idTransacao;
    private final LocalDate dataTransacao;

    protected Transacao(String idTransacao, LocalDate dataTransacao) {
        this.idTransacao = validarTexto(idTransacao, "idTransacao");
        this.dataTransacao = Objects.requireNonNull(dataTransacao, "dataTransacao nao pode ser nula");
    }

    public String idTransacao() {
        return idTransacao;
    }

    public LocalDate dataTransacao() {
        return dataTransacao;
    }

    protected static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
