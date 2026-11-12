package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Transacao;
import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.util.Map;

/**
 * Unidade transacional com cópias de trabalho em memória.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class UnidadeDeTrabalhoMemoria implements UnidadeDeTrabalho {
    private final ArmazenamentoMemoria armazenamento;
    private final Map<String, Livro> livros;
    private final Map<String, Transacao> transacoes;
    private final Map<String, Reserva> reservas;
    private boolean encerrada;

    public UnidadeDeTrabalhoMemoria(ArmazenamentoMemoria armazenamento) {
        this.armazenamento = armazenamento;
        this.livros = armazenamento.copiarLivros();
        this.transacoes = armazenamento.copiarTransacoes();
        this.reservas = armazenamento.copiarReservas();
    }

    @Override
    public synchronized void commit() {
        verificarAberta();
        armazenamento.publicar(livros, transacoes, reservas);
        encerrada = true;
    }

    @Override
    public synchronized void rollback() {
        verificarAberta();
        encerrada = true;
    }

    @Override
    public synchronized void close() {
        if (!encerrada) {
            encerrada = true;
        }
    }

    synchronized void verificarAberta() {
        if (encerrada) {
            throw new IllegalStateException("Unidade de trabalho encerrada");
        }
    }

    Map<String, Livro> livros() {
        verificarAberta();
        return livros;
    }

    Map<String, Transacao> transacoes() {
        verificarAberta();
        return transacoes;
    }

    Map<String, Reserva> reservas() {
        verificarAberta();
        return reservas;
    }
}
