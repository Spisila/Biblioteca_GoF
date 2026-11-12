package br.pucpr.biblioteca.persistencia.memoria;

import br.pucpr.biblioteca.biblioteca.movimentacao.Transacao;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioMovimentacao;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de movimentações em memória.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioMovimentacaoMemoria implements RepositorioMovimentacao {
    private final UnidadeDeTrabalhoMemoria unidadeDeTrabalho;

    public RepositorioMovimentacaoMemoria(UnidadeDeTrabalhoMemoria unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    @Override
    public void salvar(Transacao transacao) {
        unidadeDeTrabalho.transacoes().put(
                transacao.idTransacao(),
                ArmazenamentoMemoria.copiarTransacao(transacao));
    }

    @Override
    public Optional<Transacao> buscarPorId(String id) {
        return Optional.ofNullable(unidadeDeTrabalho.transacoes().get(id))
                .map(ArmazenamentoMemoria::copiarTransacao);
    }

    @Override
    public List<Transacao> listarPorLivro(String idLivro) {
        List<Transacao> copia = new ArrayList<>();
        unidadeDeTrabalho.transacoes().values().stream()
                .filter(transacao -> transacao instanceof br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo)
                .filter(transacao -> ((br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo) transacao)
                        .idLivro().equals(idLivro))
                .forEach(transacao -> copia.add(ArmazenamentoMemoria.copiarTransacao(transacao)));
        return copia;
    }
}
