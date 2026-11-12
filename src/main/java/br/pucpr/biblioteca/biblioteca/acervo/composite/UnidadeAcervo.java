package br.pucpr.biblioteca.biblioteca.acervo.composite;

/**
 * Raiz de uma organização hierárquica do acervo.
 *
 * <p>Padrão: R10 Composite — papel: Composite raiz.</p>
 */
public final class UnidadeAcervo extends GrupoAcervo {
    public UnidadeAcervo(String id, String nome) {
        super(id, nome);
    }
}
