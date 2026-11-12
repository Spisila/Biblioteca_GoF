package br.pucpr.biblioteca.biblioteca.relacionamento;

import java.time.LocalDate;
import java.util.Objects;

public final class Reserva {
    private final String id;
    private final String idLivro;
    private final String idEstudante;
    private final LocalDate dataReserva;

    public Reserva(String id, String idLivro, String idEstudante, LocalDate dataReserva) {
        this.id = validarTexto(id, "id");
        this.idLivro = validarTexto(idLivro, "idLivro");
        this.idEstudante = validarTexto(idEstudante, "idEstudante");
        this.dataReserva = Objects.requireNonNull(dataReserva, "dataReserva nao pode ser nula");
    }

    public String id() {
        return id;
    }

    public String idLivro() {
        return idLivro;
    }

    public String idEstudante() {
        return idEstudante;
    }

    public LocalDate dataReserva() {
        return dataReserva;
    }

    private static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
