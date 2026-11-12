package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;

/**
 * Visão somente de consulta de um livro, sem referência ao objeto de domínio.
 *
 * <p>Padrão: R09 Decorator — papel: Componente de dados.</p>
 */
public record MetadadosLivro(
        String id,
        String titulo,
        SituacaoLivro situacao) {
}
