package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.biblioteca.movimentacao.Transacao;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioMovimentacao;
import java.util.List;
import java.util.Optional;

/**
 * Esqueleto do repositório relacional de movimentações.
 *
 * <p>Padrão: R08 Abstract Factory — papel: Produto concreto.</p>
 */
public final class RepositorioMovimentacaoRelacional implements RepositorioMovimentacao {
    private final UnidadeDeTrabalhoRelacional unidadeDeTrabalho;

    public RepositorioMovimentacaoRelacional(UnidadeDeTrabalhoRelacional unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    public UnidadeDeTrabalhoRelacional unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void salvar(Transacao transacao) {
        indisponivel();
    }

    @Override
    public Optional<Transacao> buscarPorId(String id) {
        indisponivel();
        return Optional.empty();
    }

    @Override
    public List<Transacao> listarPorLivro(String idLivro) {
        indisponivel();
        return List.of();
    }

    private void indisponivel() {
        throw new UnsupportedOperationException("implementar no passo de persistência relacional");
    }
}
