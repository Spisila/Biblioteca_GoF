package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import java.util.Objects;

/**
 * Fábrica dos estados usados na reconstituição do livro.
 *
 * <p>Padrão: R01 State — papel: Fábrica de estados.</p>
 */
public final class EstadosLivro {
    private EstadosLivro() {
    }

    public static EstadoLivro de(SituacaoLivro situacao) {
        switch (Objects.requireNonNull(situacao, "situacao nao pode ser nula")) {
            case LIVRE:
                return new EstadoLivre();
            case RESERVADO:
                return new EstadoReservado();
            case EMPRESTADO:
                return new EstadoEmprestado();
            case RENOVADO:
                return new EstadoRenovado();
            case DEVOLVIDO:
                return new EstadoDevolvido();
            case REPARANDO:
                return new EstadoReparando();
            case INCINERADO:
                return new EstadoIncinerado();
            default:
                throw new IllegalStateException("Situacao de livro nao suportada");
        }
    }
}
