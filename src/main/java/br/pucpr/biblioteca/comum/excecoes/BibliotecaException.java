package br.pucpr.biblioteca.comum.excecoes;

public class BibliotecaException extends RuntimeException {
    public BibliotecaException(String mensagem) {
        super(mensagem);
    }

    public BibliotecaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
