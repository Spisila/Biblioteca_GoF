package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.comum.excecoes.OperacaoRecusadaException;
import java.util.Objects;

/**
 * Estado em que o livro está reservado para um estudante.
 *
 * <p>Padrão: R01 State — papel: Estado concreto.</p>
 */
public final class EstadoReservado extends EstadoLivroBase {
    @Override
    protected SituacaoLivro situacaoAtual() {
        return SituacaoLivro.RESERVADO;
    }

    @Override
    public void realizarEmprestimo(Livro livro, Emprestimo emprestimo) {
        if (emprestimo == null
                || !Objects.equals(emprestimo.idEstudante(), livro.reservadoPara())
                || !Objects.equals(emprestimo.idLivro(), livro.id())) {
            throw new OperacaoRecusadaException("Emprestimo nao corresponde a reserva do livro");
        }
        livro.definirReservadoPara(null);
        livro.transitarPara(EstadosLivro.de(SituacaoLivro.EMPRESTADO));
    }
}
