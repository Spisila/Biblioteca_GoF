package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Transacao;
import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import java.util.HashMap;
import java.util.Map;

public final class ArmazenamentoMemoria {
    private final Map<String, Livro> livros = new HashMap<>();
    private final Map<String, Transacao> transacoes = new HashMap<>();
    private final Map<String, Reserva> reservas = new HashMap<>();

    synchronized Map<String, Livro> copiarLivros() {
        return copiarLivros(livros);
    }

    synchronized Map<String, Transacao> copiarTransacoes() {
        return copiarTransacoes(transacoes);
    }

    synchronized Map<String, Reserva> copiarReservas() {
        return copiarReservas(reservas);
    }

    synchronized void publicar(
            Map<String, Livro> livros,
            Map<String, Transacao> transacoes,
            Map<String, Reserva> reservas) {
        this.livros.clear();
        this.livros.putAll(copiarLivros(livros));
        this.transacoes.clear();
        this.transacoes.putAll(copiarTransacoes(transacoes));
        this.reservas.clear();
        this.reservas.putAll(copiarReservas(reservas));
    }

    private static Map<String, Livro> copiarLivros(Map<String, Livro> origem) {
        Map<String, Livro> copia = new HashMap<>();
        origem.forEach((id, livro) -> copia.put(id, copiarLivro(livro)));
        return copia;
    }

    private static Map<String, Transacao> copiarTransacoes(Map<String, Transacao> origem) {
        Map<String, Transacao> copia = new HashMap<>();
        origem.forEach((id, transacao) -> copia.put(id, copiarTransacao(transacao)));
        return copia;
    }

    private static Map<String, Reserva> copiarReservas(Map<String, Reserva> origem) {
        Map<String, Reserva> copia = new HashMap<>();
        origem.forEach((id, reserva) -> copia.put(id, copiarReserva(reserva)));
        return copia;
    }

    static Livro copiarLivro(Livro livro) {
        return Livro.reconstituir(
                livro.id(),
                livro.titulo(),
                livro.situacao(),
                livro.reservadoPara(),
                livro.dataDevolucao());
    }

    static Transacao copiarTransacao(Transacao transacao) {
        if (transacao instanceof br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo) {
            br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo emprestimo =
                    (br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo) transacao;
            return br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo.reconstituir(
                    emprestimo.idTransacao(),
                    emprestimo.idLivro(),
                    emprestimo.idEstudante(),
                    emprestimo.dataTransacao(),
                    emprestimo.dataVencimento(),
                    emprestimo.numRenovacoes());
        }
        throw new IllegalArgumentException("Transacao nao suportada pela persistencia em memoria");
    }

    static Reserva copiarReserva(Reserva reserva) {
        return new Reserva(reserva.id(), reserva.idLivro(), reserva.idEstudante(), reserva.dataReserva());
    }
}
