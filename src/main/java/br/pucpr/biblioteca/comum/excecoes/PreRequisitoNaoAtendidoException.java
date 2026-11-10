package br.pucpr.biblioteca.comum.excecoes;

public class PreRequisitoNaoAtendidoException extends BibliotecaException {
    public PreRequisitoNaoAtendidoException(String mensagem) {
        super(mensagem);
    }

    public PreRequisitoNaoAtendidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
