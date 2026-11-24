package br.pucpr.biblioteca.academico.estudante;

import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Pessoa vinculada a um curso e habilitada a realizar empréstimos.
 */
public final class Estudante extends Pessoa {
    private final String numeroMatricula;
    private final String curso;
    private final int limiteEmprestimos;

    public Estudante(
            String id,
            String nome,
            String cpf,
            String email,
            String numeroMatricula,
            String curso,
            int limiteEmprestimos) {
        this(id, nome, cpf, email, numeroMatricula, curso, limiteEmprestimos,
                LocalDate.of(1900, 1, 1));
    }

    public Estudante(
            String id,
            String nome,
            String cpf,
            String email,
            String numeroMatricula,
            String curso,
            int limiteEmprestimos,
            LocalDate dataNascimento) {
        super(id, nome, cpf, email, dataNascimento);
        this.numeroMatricula = validarTexto(numeroMatricula, "numeroMatricula");
        this.curso = validarTexto(curso, "curso");
        if (limiteEmprestimos < 0) {
            throw new IllegalArgumentException("limiteEmprestimos nao pode ser negativo");
        }
        this.limiteEmprestimos = limiteEmprestimos;
    }

    public Estudante(
            String nome,
            String cpf,
            String email,
            LocalDate dataNascimento,
            String numeroMatricula,
            String curso,
            int limiteEmprestimos) {
        super(nome, cpf, email, dataNascimento);
        this.numeroMatricula = validarTexto(numeroMatricula, "numeroMatricula");
        this.curso = validarTexto(curso, "curso");
        if (limiteEmprestimos < 0) {
            throw new IllegalArgumentException("limiteEmprestimos nao pode ser negativo");
        }
        this.limiteEmprestimos = limiteEmprestimos;
    }

    public String numeroMatricula() {
        return numeroMatricula;
    }

    public String curso() {
        return curso;
    }

    public int limiteEmprestimos() {
        return limiteEmprestimos;
    }

    @Override
    public boolean equals(Object objeto) {
        if (!super.equals(objeto)) {
            return false;
        }
        Estudante estudante = (Estudante) objeto;
        return limiteEmprestimos == estudante.limiteEmprestimos
                && numeroMatricula.equals(estudante.numeroMatricula)
                && curso.equals(estudante.curso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numeroMatricula, curso, limiteEmprestimos);
    }

    @Override
    public String toString() {
        return "Estudante{"
                + "id='" + id() + '\''
                + ", nome='" + nome() + '\''
                + ", cpf='" + cpf() + '\''
                + ", email='" + email() + '\''
                + ", numeroMatricula='" + numeroMatricula + '\''
                + ", curso='" + curso + '\''
                + ", limiteEmprestimos=" + limiteEmprestimos
                + '}';
    }
}
