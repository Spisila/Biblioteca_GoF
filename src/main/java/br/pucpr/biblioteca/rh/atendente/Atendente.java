package br.pucpr.biblioteca.rh.atendente;

import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import java.util.Objects;

/**
 * Pessoa autorizada a atuar no atendimento da biblioteca.
 */
public final class Atendente extends Pessoa {
    private final String turno;
    private final String registroFuncional;

    public Atendente(
            String id,
            String nome,
            String cpf,
            String email,
            String turno,
            String registroFuncional) {
        super(id, nome, cpf, email);
        this.turno = validarTexto(turno, "turno");
        this.registroFuncional = validarTexto(registroFuncional, "registroFuncional");
    }

    public String turno() {
        return turno;
    }

    public String registroFuncional() {
        return registroFuncional;
    }

    @Override
    public boolean equals(Object objeto) {
        if (!super.equals(objeto)) {
            return false;
        }
        Atendente atendente = (Atendente) objeto;
        return turno.equals(atendente.turno)
                && registroFuncional.equals(atendente.registroFuncional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), turno, registroFuncional);
    }

    @Override
    public String toString() {
        return "Atendente{"
                + "id='" + id() + '\''
                + ", nome='" + nome() + '\''
                + ", cpf='" + cpf() + '\''
                + ", email='" + email() + '\''
                + ", turno='" + turno + '\''
                + ", registroFuncional='" + registroFuncional + '\''
                + '}';
    }
}
