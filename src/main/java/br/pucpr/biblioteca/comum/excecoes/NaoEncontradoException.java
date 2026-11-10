package br.pucpr.biblioteca.comum.excecoes;

public class NaoEncontradoException extends BibliotecaException {
    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public NaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
