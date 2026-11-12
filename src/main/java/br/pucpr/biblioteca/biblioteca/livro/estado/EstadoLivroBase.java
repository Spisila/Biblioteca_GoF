package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.ResultadoInspecao;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.comum.excecoes.OperacaoRecusadaException;
import java.time.LocalDate;

/**
 * Implementa a recusa padrão dos eventos não permitidos.
 *
 * <p>Padrão: R01 State — papel: Estado abstrato.</p>
 */
public abstract class EstadoLivroBase implements EstadoLivro {
    protected abstract SituacaoLivro situacaoAtual();

    protected final void recusar(String evento) {
        throw new OperacaoRecusadaException(
                "Evento " + evento + " não é aceito no estado " + situacaoAtual());
    }

    @Override
    public final SituacaoLivro situacao() {
        return situacaoAtual();
    }

    @Override
    public void solicitarReserva(Livro livro, String idEstudante) {
        recusar("solicitarReserva");
    }

    @Override
    public void realizarEmprestimo(Livro livro, Emprestimo emprestimo) {
        recusar("realizarEmprestimo");
    }

    @Override
    public void renovarEmprestimo(Livro livro, Emprestimo emprestimo) {
        recusar("renovarEmprestimo");
    }

    @Override
    public void registrarDevolucao(Livro livro, Emprestimo emprestimo, LocalDate dataDevolucao) {
        recusar("registrarDevolucao");
    }

    @Override
    public void inspecionarLivro(Livro livro, ResultadoInspecao resultado) {
        recusar("inspecionarLivro");
    }

    @Override
    public void enviarParaReparo(Livro livro, boolean danoIdentificado) {
        recusar("enviarParaReparo");
    }

    @Override
    public void retornarDoReparo(Livro livro) {
        recusar("retornarDoReparo");
    }

    @Override
    public void registrarLaudoIrrecuperavel(Livro livro) {
        recusar("registrarLaudoIrrecuperavel");
    }

    @Override
    public void excluirLivro(Livro livro) {
        recusar("excluirLivro");
    }
}
