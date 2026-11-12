package br.pucpr.biblioteca.persistencia.fabrica;

import br.pucpr.biblioteca.biblioteca.movimentacao.Transacao;
import java.util.List;
import java.util.Optional;

/**
 * Repositório abstrato de movimentações.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto abstrato.</p>
 */
public interface RepositorioMovimentacao {
    void salvar(Transacao transacao);

    Optional<Transacao> buscarPorId(String id);

    List<Transacao> listarPorLivro(String idLivro);
}
