package br.pucpr.biblioteca.comum.excecoes;

public class OperacaoRecusadaException extends BibliotecaException {
    public OperacaoRecusadaException(String mensagem) {
        super(mensagem);
    }

    public OperacaoRecusadaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
