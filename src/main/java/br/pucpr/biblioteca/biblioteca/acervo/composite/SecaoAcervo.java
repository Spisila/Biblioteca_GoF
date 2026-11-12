package br.pucpr.biblioteca.biblioteca.acervo.composite;

/**
 * Grupo que representa uma seção ou subseção do acervo.
 *
 * <p>Padrão: R10 Composite — papel: Composite concreto.</p>
 */
public final class SecaoAcervo extends GrupoAcervo {
    public SecaoAcervo(String id, String nome) {
        super(id, nome);
    }
}
