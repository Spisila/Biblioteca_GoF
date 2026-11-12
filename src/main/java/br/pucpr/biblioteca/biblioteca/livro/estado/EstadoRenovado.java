package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.comum.excecoes.OperacaoRecusadaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Estado em que o livro possui um empréstimo renovado.
 *
 * <p>Padrão: R01 State — papel: Estado concreto.</p>
 */
public final class EstadoRenovado extends EstadoLivroBase {
    @Override
    protected SituacaoLivro situacaoAtual() {
        return SituacaoLivro.RENOVADO;
    }

    @Override
    public void renovarEmprestimo(Livro livro, Emprestimo emprestimo) {
        validarEmprestimo(livro, emprestimo);
        if (emprestimo.numRenovacoes() >= Emprestimo.LIMITE_RENOVACOES) {
            throw new OperacaoRecusadaException("Limite de renovacoes atingido");
        }
        emprestimo.renovar();
        livro.transitarPara(EstadosLivro.de(SituacaoLivro.RENOVADO));
    }

    @Override
    public void registrarDevolucao(Livro livro, Emprestimo emprestimo, LocalDate dataDevolucao) {
        validarEmprestimo(livro, emprestimo);
        Objects.requireNonNull(dataDevolucao, "dataDevolucao nao pode ser nula");
        livro.definirDataDevolucao(dataDevolucao);
        livro.transitarPara(EstadosLivro.de(SituacaoLivro.DEVOLVIDO));
    }
}
