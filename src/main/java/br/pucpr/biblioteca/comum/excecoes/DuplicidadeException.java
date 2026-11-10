package br.pucpr.biblioteca.comum.excecoes;

public class DuplicidadeException extends BibliotecaException {
    public DuplicidadeException(String mensagem) {
        super(mensagem);
    }

    public DuplicidadeException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
