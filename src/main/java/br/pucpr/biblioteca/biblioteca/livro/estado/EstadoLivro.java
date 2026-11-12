package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.ResultadoInspecao;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import java.time.LocalDate;

/**
 * Contrato dos estados do ciclo de vida do livro.
 *
 * <p>Padrão: R01 State — papel: Estado abstrato.</p>
 */
public interface EstadoLivro {
    br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro situacao();

    void solicitarReserva(Livro livro, String idEstudante);

    void realizarEmprestimo(Livro livro, Emprestimo emprestimo);

    void renovarEmprestimo(Livro livro, Emprestimo emprestimo);

    void registrarDevolucao(Livro livro, Emprestimo emprestimo, LocalDate dataDevolucao);

    void inspecionarLivro(Livro livro, ResultadoInspecao resultado);

    void enviarParaReparo(Livro livro, boolean danoIdentificado);

    void retornarDoReparo(Livro livro);

    void registrarLaudoIrrecuperavel(Livro livro);

    void excluirLivro(Livro livro);
}
