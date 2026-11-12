package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;

/**
 * Estado em que o livro está disponível.
 *
 * <p>Padrão: R01 State — papel: Estado concreto.</p>
 */
public final class EstadoLivre extends EstadoLivroBase {
    @Override
    protected SituacaoLivro situacaoAtual() {
        return SituacaoLivro.LIVRE;
    }

    @Override
    public void solicitarReserva(Livro livro, String idEstudante) {
        validarTexto(idEstudante, "idEstudante");
        livro.definirReservadoPara(idEstudante);
        livro.transitarPara(EstadosLivro.de(SituacaoLivro.RESERVADO));
    }

    @Override
    public void enviarParaReparo(Livro livro, boolean danoIdentificado) {
        if (!danoIdentificado) {
            throw new IllegalArgumentException("Dano identificado deve ser verdadeiro");
        }
        livro.transitarPara(EstadosLivro.de(SituacaoLivro.REPARANDO));
    }

    @Override
    public void excluirLivro(Livro livro) {
    }

    private void validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
    }
}
