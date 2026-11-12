package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.comum.excecoes.OperacaoRecusadaException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Estado em que o livro está emprestado.
 *
 * <p>Padrão: R01 State — papel: Estado concreto.</p>
 */
public final class EstadoEmprestado extends EstadoLivroBase {
    @Override
    protected SituacaoLivro situacaoAtual() {
        return SituacaoLivro.EMPRESTADO;
    }

    @Override
    public void renovarEmprestimo(Livro livro, Emprestimo emprestimo) {
        validarEmprestimo(livro, emprestimo);
        if (emprestimo.numRenovacoes() != 0) {
            throw new OperacaoRecusadaException("A primeira renovacao exige zero renovacoes anteriores");
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
