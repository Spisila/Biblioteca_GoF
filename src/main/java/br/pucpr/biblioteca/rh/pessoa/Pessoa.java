package br.pucpr.biblioteca.rh.pessoa;

import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Dados básicos de uma pessoa cadastrada na biblioteca.
 */
public class Pessoa implements RegistroCadastravel {
    private final String id;
    private final String nome;
    private final String cpf;
    private final String email;
    private final LocalDate dataNascimento;

    public Pessoa(String id, String nome, String cpf, String email) {
        this(id, nome, cpf, email, LocalDate.of(1900, 1, 1));
    }

    public Pessoa(
            String id,
            String nome,
            String cpf,
            String email,
            LocalDate dataNascimento) {
        this.id = id == null ? "" : id.trim();
        this.nome = validarTexto(nome, "nome");
        this.cpf = validarTexto(cpf, "cpf");
        this.email = validarTexto(email, "email");
        this.dataNascimento = Objects.requireNonNull(
                dataNascimento, "dataNascimento nao pode ser nula");
    }

    public Pessoa(String nome, String cpf, String email, LocalDate dataNascimento) {
        this("", nome, cpf, email, dataNascimento);
    }

    @Override
    public final String id() {
        return id;
    }

    public final String nome() {
        return nome;
    }

    public final String cpf() {
        return cpf;
    }

    public final String email() {
        return email;
    }

    public final LocalDate dataNascimento() {
        return dataNascimento;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Pessoa pessoa = (Pessoa) objeto;
        return id.equals(pessoa.id)
                && nome.equals(pessoa.nome)
                && cpf.equals(pessoa.cpf)
                && email.equals(pessoa.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id, nome, cpf, email);
    }

    @Override
    public String toString() {
        return "Pessoa{"
                + "id='" + id + '\''
                + ", nome='" + nome + '\''
                + ", cpf='" + cpf + '\''
                + ", email='" + email + '\''
                + '}';
    }

    protected static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
